projectsEvaluated {
    rootProject.subprojects {
        if (project.hasProperty('android')) {
            repositories {
                mavenCentral()
            }
            apply plugin:'findbugs'

            //Findbugs task
//        task findbugs(type: FindBugs, dependsOn: [project.assembleDebug]) {
            task findbugs(type:FindBugs, dependsOn:[project.assembleRelease]) {
                // Find excludes filter
                final String CONFIG_FILE_NAME = 'findbugs-filter.xml'
                if (rootProject.file(CONFIG_FILE_NAME).exists()) {
                    excludeFilter rootProject.file(CONFIG_FILE_NAME)
                } else {
                    for (final File dir : startParameter.getInitScripts()) {
                        if (new File(dir.getParentFile(), CONFIG_FILE_NAME).exists()) {
                            excludeFilter new File(dir.getParentFile(), CONFIG_FILE_NAME)
                            break
                        }
                    }
                }
                //excludeFilter script.file(CONFIG_FILE_NAME)
                classes = files()
                if (android.hasProperty('applicationVariants')) {
                    project.android.applicationVariants.all { variant ->
                        if (variant.buildType.name == 'debug') {
                            classes += files("${variant.javaCompile.destinationDir}")
                        }
                    }
                } else if (android.hasProperty('libraryVariants')) {
                    project.android.libraryVariants.all { variant ->
                        if (variant.buildType.name == 'debug') {
                            classes += files("${variant.javaCompile.destinationDir}")
                        }
                    }
                }
                if (android.hasProperty('testVariants')) {
                    project.android.testVariants.all { variant ->
                        if (variant.buildType.name == 'debug') {
                            classes += files("${variant.javaCompile.destinationDir}")
                        }
                    }
                }
                source = [android.sourceSets.main.java.srcDirs,
                          android.sourceSets.androidTest.java.srcDirs,
                          android.sourceSets.test.java.srcDirs]
                classpath = project.configurations.compile + files(project.android.bootClasspath)
                effort = 'max'
                reportLevel = 'low'
                reports {
                    //html.enabled = true
                    xml {
                    //    enabled = false
                        withMessages = true
                    }
                }
                ignoreFailures = true // Don't report error if there are bugs found.
            }
            // Flavourless projects need assembleDebugAndroidTest
            // Flavoured projects need assembleAndroidTest
            if (project.hasProperty('assembleDebugAndroidTest')) {
                project.tasks.findbugs.dependsOn += ['assembleDebugAndroidTest']
            } else if (project.hasProperty('assembleAndroidTest')) {
                project.tasks.findbugs.dependsOn += ['assembleAndroidTest']
            }
            if (project.hasProperty('assembleDebugUnitTest')) {
                project.tasks.findbugs.dependsOn += ['assembleDebugUnitTest']
            } else if (project.hasProperty('assembleUnitTest')) {
                project.tasks.findbugs.dependsOn += ['assembleUnitTest']
            }
            project.check.dependsOn += [project.tasks.findbugs]
        }
    }
}
