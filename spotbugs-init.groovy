import org.gradle.api.Project
import org.gradle.api.file.FileCollection

ext.SPOTBUGS = 'spotbugs'
rootProject {
    buildscript {
        repositories {
            maven {
                url 'https://plugins.gradle.org/m2/'
            }
        }
        dependencies {
            classpath 'com.github.spotbugs:spotbugs-gradle-plugin:3.0.0'
        }
    }
}

gradle.projectsEvaluated {
    ext.applySpotBugs = {
        if (project.hasProperty('android') &&
                !project.hasProperty(SPOTBUGS)) {
            addSpotBugsTask project
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applySpotBugs
    } else {
        rootProject.subprojects applySpotBugs
    }
}

//Spotbugs task
void addSpotBugsTask(final Project project) {
    project.apply plugin:'com.github.spotbugs'
    project.sourceSets {
        main {
            java.srcDirs = ['main', 'androidTest', 'test'].collect {
                project.android.sourceSets.findByName(it)
            }.find { null != it }.collect { it.java.srcDirs }
        }
    }
    project.tasks.matching { it.name.startsWith(SPOTBUGS) }.each {
        it.with {
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
            classpath = project.configurations.compile + files(project.android.bootClasspath)
            effort = 'max'
            reportLevel = 'low'
            reports {
                // html.enabled = true
                xml {
                    // enabled = false
                    withMessages = true
                }
            }
            ignoreFailures = true // Don't report error if there are bugs found.
        }
    }
}

FileCollection getDebugSources(final Project project) {
    FileCollection classes = project.files()
    for (final String variantType : ['applicationVariants', 'libraryVariants', 'testVariants']) {
        if (project.android.hasProperty(variantType)) {
            project.android."${variantType}".all { variant ->
                if (variant.buildType.name == 'debug') {
                    classes += files("${variant.javaCompileProvider.get().destinationDir}")
                }
            }
        }
    }
    classes
}

