import org.gradle.api.Project

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'io.gitlab.arturbosch.detekt:detekt-cli:1.0.0.RC8'
    }
}

ext.DETEKT = 'detekt'
gradle.projectsEvaluated {
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
    ext.source = [project.android.sourceSets.main.java.srcDirs,
            project.android.sourceSets.androidTest.java.srcDirs,
            project.android.sourceSets.test.java.srcDirs,]
    project.tasks.create([name:DETEKT, type:JavaExec, group:'verification']) {
        args '--input', 'src/main/java'
        args '--output', "${project.reportsDir.path}/$DETEKT"
        classpath files(buildscript.scriptClassPath.asFiles)
        description 'Kotlin static checker'
        ignoreExitValue true
        main 'io.gitlab.arturbosch.detekt.cli.Main'
    }
    project.check.dependsOn project.tasks[DETEKT]
}
