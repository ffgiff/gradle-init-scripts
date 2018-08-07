import org.gradle.api.Project

rootProject {
    buildscript {
        repositories {
            maven {
                url 'https://plugins.gradle.org/m2/'
            }
        }
        dependencies {
            classpath 'gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0.RC8'
        }
    }
}

ext.DETEKT = 'detekt'
projectsEvaluated {
    ext.applyDetekt = {
        if (project.hasProperty('android')) {
            if (!project.hasProperty(DETEKT)) {
                addDetektTask project
            }
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyDetekt
    } else {
        rootProject.subprojects applyDetekt
    }
}
//Detekt task
void addDetektTask(final Project project) {
    project.apply plugin:'io.gitlab.arturbosch.detekt'
    project.detekt {
        defaultProfile {
            input = project.android.sourceSets.main.java.sourceFiles.asPath
            output = "${project.reportsDir.path}/$DETEKT"
        }
    }
    project.check.dependsOn project.tasks.detektCheck
}
