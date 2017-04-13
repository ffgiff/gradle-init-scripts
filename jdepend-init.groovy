import org.gradle.api.Project
import org.gradle.api.file.FileCollection

projectsEvaluated {
    ext.applyJdepend = {
        if (project.hasProperty('android')) {
            repositories {
                jcenter()
            }

            addJdependTask project

            project.check.dependsOn += [project.tasks.jdepend]
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyJdepend
    } else {
        rootProject.subprojects applyJdepend
    }
}

// JDepend task
void addJdependTask(final Project project) {
    final String TASK_NAME = 'jdepend'
    project.apply plugin:TASK_NAME
    project.tasks.create([name:TASK_NAME,
                          type:JDepend,
                          dependsOn:[project.assembleRelease],]) {
        jdependClasspath = setConfig(jdependClasspath)
        for (final String variantType : ['applicationVariants', 'libraryVariants',]) {
            if (project.android.hasProperty(variantType)) {
                project.android."${variantType}".all { variant ->
                    if (variant.buildType.name == 'release') {
                        classesDir file("${variant.javaCompile.destinationDir}")
                    }
                }
            }
        }
    }
}

FileCollection setConfig(final FileCollection jdependClasspath) {
    final String CONFIG_NAME = 'jdepend.properties'
    FileCollection result = jdependClasspath
    // Find jdepend properties file
    if (rootProject.file(CONFIG_NAME).exists()) {
        result = result + files(rootProject.projectDir)
    } else {
        for (final File dir : startParameter.initScripts) {
            if (new File(dir.parentFile, CONFIG_NAME).exists()) {
                result = result + files(dir.parentFile)
                break
            }
        }
    }
    result
}

