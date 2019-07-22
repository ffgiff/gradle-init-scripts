import org.gradle.api.Project

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.github.shyiko:ktlint:0.19.0'
    }
}
gradle.projectsEvaluated {
    ext.applyKtlint = {
        if (project.hasProperty('android')) {
            addKtlintTask(project)
            project.check.dependsOn project.tasks.ktlint
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyKtlint
    } else {
        rootProject.subprojects applyKtlint
    }
}

void addKtlintTask(final Project project) {
        ext.source = ['main', 'androidTest', 'test'].collect {
            project.android.sourceSets.findByName(it)
        }.find { null != it }.collect { it.java.srcDirs }
    project.tasks.create([name:'ktlint', type:JavaExec, group:'verification']) {
        args '--android'
        classpath files(buildscript.scriptClassPath.asFiles)
        description 'Check Kotlin code style.'
        ignoreExitValue true
        main 'com.github.shyiko.ktlint.Main'
        source.each { args "${it.absolutePath}/**/*.kt" }
    }
}
