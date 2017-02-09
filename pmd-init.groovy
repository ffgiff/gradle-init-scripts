final String CONFIG_FILE_NAME = 'pmd.config'
projectsEvaluated {
    rootProject.subprojects {
        if (project.hasProperty('android')) {
            // Find ruleSets config file
            if (rootProject.file(CONFIG_FILE_NAME).exists()) {
                apply from:rootProject.file(CONFIG_FILE_NAME)
            } else {
                for (final File dir : startParameter.getInitScripts()) {
                    if (new File(dir.getParentFile(), CONFIG_FILE_NAME).exists()) {
                        apply from:new File(dir.getParentFile(), CONFIG_FILE_NAME)
                        break
                    }
                }
            }
            repositories {
                mavenCentral()
            }
            apply plugin:'pmd'

            //PMD task
            task pmd(type:Pmd) {
                ruleSets = rootProject.ext.ruleSets
                source = [android.sourceSets.main.java.srcDirs,
                          android.sourceSets.androidTest.java.srcDirs,
                          android.sourceSets.test.java.srcDirs]
                ignoreFailures = true // Don't report error if there are bugs found.
            }

            project.check.dependsOn += [project.tasks.pmd]
        }
    }
}
