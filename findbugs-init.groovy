projectsEvaluated {
    rootProject.subprojects {
        if (project.hasProperty('android')) {
            repositories {
                mavenCentral()
            }

            addFindBugsTask project

            // Flavourless projects need assembleDebugAndroidTest
            // Flavoured projects need assembleAndroidTest
            for (final String taskProperty : ['assembleDebugAndroidTest',
                                              'assembleAndroidTest',
                                              'assembleDebugUnitTest',
                                              'assembleUnitTest']) {
                if (project.hasProperty(taskProperty)) {
                    project.tasks.findbugs.dependsOn += [taskProperty]
                }
            }
            project.check.dependsOn += [project.tasks.findbugs]
        }
    }
}

//Findbugs task
void addFindBugsTask(final Project project) {
    final String TASK_NAME = 'findbugs'
    project.apply plugin:TASK_NAME
    project.tasks.create([name:TASK_NAME,
                          type:FindBugs,
                          dependsOn:[project.assembleRelease]]) {
        // Find excludes filter
        final String CONFIG_NAME = 'findbugs-filter.xml'
        if (rootProject.file(CONFIG_NAME).exists()) {
            excludeFilter rootProject.file(CONFIG_NAME)
        } else {
            for (final File dir : startParameter.initScripts) {
                 if (new File(dir.parentFile, CONFIG_NAME).exists()) {
                    excludeFilter new File(dir.parentFile, CONFIG_NAME)
                    break
                }
            }
        }
        //excludeFilter script.file(CONFIG_NAME)
        classes = getDebugSources(project)
        source = [project.android.sourceSets.main.java.srcDirs,
                  project.android.sourceSets.androidTest.java.srcDirs,
                  project.android.sourceSets.test.java.srcDirs]
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
}

FileCollection getDebugSources(final Project project) {
    FileCollection classes = project.files()
    for (final String variantType : ['applicationVariants', 'libraryVariants', 'testVariants']) {
        if (project.android.hasProperty(variantType)) {
            project.android."${variantType}".all { variant ->
                if (variant.buildType.name == 'debug') {
                    classes += files("${variant.javaCompile.destinationDir}")
                }
            }
        }
    }
    classes
}

