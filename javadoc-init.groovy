projectsEvaluated {
    rootProject.subprojects {
        if (project.hasProperty('android')) {
            addJavadocTask(project)
        }
    }
}
void addJavadocTask(final Project project) {
//        task javadoc (type: Javadoc, dependsOn: project.tasks.assembleDebug) {
    project.tasks.create([name:'javadoc',
                          type:Javadoc,
                          dependsOn:[project.assembleRelease]]) {
        classpath = project.configurations.compile +
                    project.configurations.testCompile +
                    project.configurations.androidTestCompile +
                    files(project.android.bootClasspath)
//        classpath = project.configurations.compile + files(android.bootClasspath)
        for (final String variantType : ['applicationVariants', 'libraryVariants']) {
            if (project.android.hasProperty(variantType)) {
                classpath += getClasspathForVariantType(variantType, project)
            }
        }
        exclude '**/*.aj'
        source = [project.android.sourceSets.main.java.srcDirs,
                  project.android.sourceSets.androidTest.java.srcDirs,
                  project.android.sourceSets.test.java.srcDirs]
//        source = [android.sourceSets.androidTest.java.srcDirs]
        options.setLinksOffline([new JavadocOfflineLink(
                'https://developer.android.com/reference',
                project.android.sdkDirectory.absolutePath
                        .replace(File.separatorChar, (char)'/')
                    + '/docs/reference')])
        options.encoding('utf-8')
    }
}

FileCollection getClasspathForVariantType(final String variantType, final Project project) {
    FileCollection classpath = files()
    project.android."${variantType}".all { variant ->
        if (variant.buildType.name == 'release') {
            classpath += files("${variant.javaCompile.destinationDir}")
            if ("${variantType}" == 'applicationVariants') {
                variant.compileLibraries.each { lib ->
                    classpath += files(lib)
                }
            } else if ("${variantType}" == 'libraryVariants') {
                classpath += variant.javaCompile.classpath
            }
        }
    }
    classpath
}
