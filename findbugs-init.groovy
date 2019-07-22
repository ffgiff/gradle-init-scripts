import org.gradle.api.Project
import org.gradle.api.file.FileCollection

ext.FINDBUGS = 'findbugs'
gradle.projectsEvaluated {
    ext.applyFindBugs = {
        if (project.hasProperty('android')) {
            repositories {
                mavenCentral()
            }
            if (!project.hasProperty(FINDBUGS)) {
                addFindBugsTask project
            }
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyFindBugs
    } else {
        rootProject.subprojects applyFindBugs
    }
}

//Findbugs task
void addFindBugsTask(final Project project) {
    project.apply plugin:FINDBUGS
    project.tasks.create([name:FINDBUGS,
                          type:FindBugs,
                          dependsOn:[project.assembleRelease],]) {
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
        source = ['main', 'androidTest', 'test'].collect {
            project.android.sourceSets.findByName(it)
        }.find { null != it }.collect { it.java.srcDirs }
        classpath = project.configurations.compile + files(project.android.bootClasspath)
        effort = 'max'
        reportLevel = 'low'
        reports {
            //html.enabled = true
            xml {
                //enabled = false
                withMessages = true
            }
        }
        ignoreFailures = true // Don't report error if there are bugs found.
    }
    // Flavourless projects need assembleDebugAndroidTest
    // Flavoured projects need assembleAndroidTest
    for (final String taskProperty : ['assembleDebugAndroidTest',
                                      'assembleAndroidTest',
                                      'assembleDebugUnitTest',
                                      'assembleUnitTest',]) {
        if (project.hasProperty(taskProperty)) {
            project.tasks.findbugs.dependsOn += [taskProperty]
        }
    }
    project.check.dependsOn += [project.tasks.findbugs]
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

