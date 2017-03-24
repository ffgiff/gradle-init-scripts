projectsEvaluated {
    ext.applyPmd = {
        if (project.hasProperty('android')) {
            setConfig(project)
            repositories {
                mavenCentral()
            }
            apply plugin:'pmd'

            //PMD task
            task pmd(type:Pmd) {
                ruleSets = rootProject.ext.ruleSets
                source = [android.sourceSets.main.java.srcDirs,
                          android.sourceSets.androidTest.java.srcDirs,
                          android.sourceSets.test.java.srcDirs,]
                ignoreFailures = true // Don't report error if there are bugs found.
            }

            project.check.dependsOn += [project.tasks.pmd]
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyPmd
    } else {
        rootProject.subprojects applyPmd
    }
}

File setConfig(final Project project) {
    final String CONFIG_NAME = 'pmd.config'
    // Find ruleSets config file
    if (rootProject.file(CONFIG_NAME).exists()) {
        project.apply from:rootProject.file(CONFIG_NAME)
    } else {
        for (final File dir : startParameter.initScripts) {
            if (new File(dir.parentFile, CONFIG_NAME).exists()) {
                project.apply from:new File(dir.parentFile, CONFIG_NAME)
                break
            }
        }
    }
}
