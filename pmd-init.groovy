import org.gradle.api.Project

ext.PMD = 'pmd'
gradle.projectsEvaluated {
    ext.applyPmd = {
        if (project.hasProperty('android')
                && !project.hasProperty(PMD)) {
            setConfig(project)
            repositories {
                mavenCentral()
            }
            apply plugin:PMD

            //PMD task
            task pmd(type:Pmd) {
                ruleSets = rootProject.ext.ruleSets
                source = ['main', 'androidTest', 'test'].collect {
                    project.android.sourceSets.findByName(it)
                }.find { null != it }.collect { it.java.srcDirs }
                exclude '**/*.kt'
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
