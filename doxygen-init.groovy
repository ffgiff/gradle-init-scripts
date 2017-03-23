//Doxygen task
rootProject {
    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'org.ysb33r.gradle:doxygen:0.2'
        }
    }
}

projectsEvaluated {
    ext.applyDoxygen = {
        addDoxygenTask project
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyDoxygen
    } else {
        rootProject.subprojects applyDoxygen
    }
}
void addDoxygenTask(final Project project) {
    final File TEMPLATE = findConfig()
    if (project.hasProperty('android')) {
        project.apply plugin:'org.ysb33r.doxygen'

        project.tasks.doxygen {
            if (TEMPLATE.exists()) {
                template TEMPLATE.absolutePath
            }
//            dependsOn project.assembleDebug
            dependsOn project.assembleRelease
            project_number project.android.defaultConfig.versionName +
                    " (${project.android.defaultConfig.versionCode})"
            // project_logo
            output_directory project.docsDir
            source = [project.android.sourceSets.main.java.srcDirs]
//            source = [project.android.sourceSets.main.java.srcDirs,
//                      project.android.sourceSets.androidTest.java.srcDirs,
//                      project.android.sourceSets.test.java.srcDirs]
        }
    }
}
// Find default configuration.
File findConfig() {
    final String CONFIG_NAME = 'Doxyfile'
    File configFile = rootProject.file(CONFIG_NAME)
    if (!configFile.exists()) {
        for (final File dir : startParameter.initScripts) {
            configFile = new File(dir.parentFile, CONFIG_NAME)
            if (configFile.exists()) {
                break
            }
        }
    }
    configFile
}
