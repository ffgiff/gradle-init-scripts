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
//    rootProject {
//        if (null == subprojects || 0 == subprojects.size()) {
//            addDoxygenTask rootProject
//        } else {
            rootProject.subprojects {
                addDoxygenTask project
            }
//        }
//    }
}
void addDoxygenTask(final Project project) {
    project.logger.log(LogLevel.WARN, this.toString())
    final File TEMPLATE = findConfig()
    if (project.hasProperty('android')) {
        project.apply plugin:'org.ysb33r.doxygen'

        project.tasks.doxygen {
            if (TEMPLATE.exists()) {
                template TEMPLATE.getAbsolutePath()
            }
//            dependsOn project.assembleDebug
            dependsOn project.assembleRelease
            project_number project.android.defaultConfig.versionName +
                    "(${project.android.defaultConfig.versionCode})"
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
        for (final File dir : startParameter.getInitScripts()) {
            configFile = new File(dir.getParentFile(), CONFIG_NAME)
            if (configFile.exists()) {
                break
            }
        }
    }
    configFile
}
