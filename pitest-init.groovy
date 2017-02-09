buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'org.pitest:pitest:1.1.10'
        classpath 'org.pitest:pitest-command-line:1.1.10'
    }
}

projectsEvaluated {
    rootProject.subprojects {
        if (project.hasProperty('android')) {
            task pitest {
                // Meta-task, will dependOn pitestDebugUnitTest etc.
            }
            setTestTaskDependencies project
        }
    }
}

void setTestTaskDependencies(final Project project) {
    project.tasks.test.dependsOn.each { testTask ->
        if (testTask.metaClass.respondsTo(testTask, 'startsWith') &&
                testTask.startsWith('test')) {
            project.tasks.create([name:"pi$testTask",
                                  dependsOn:["$testTask"]]) {
                runPitest(project, testTask)
            }
            project.pitest.dependsOn += [project.tasks."pi$testTask"]
        }
    }
}

void runPitest(project, testTask) {
    ext.testSource = project.android.sourceSets.main.java.srcDirs
    project.android.unitTestVariants.matching
            { it.name == testTask[4..5].toLowerCase() + testTask[5..-1] }
            .sourceSets.each { testSource ->
        ext.testSource += testSource.java.srcDirs
    }
    if (project.hasProperty('processReleaseManifest')) {
        new ProcessBuilder('java',
            '-classpath', (files(buildscript.scriptClassPath.asFiles) +
                           project.tasks[testTask].classpath).asPath,
            'org.pitest.mutationtest.commandline.MutationCoverageReport',
            '--reportDir', project.reportsDir.path,
            '--targetClasses', project.processReleaseManifest.packageOverride + '.*',
            '--sourceDirs', files(ext.testSource).asPath.replaceAll(':', ','),
            '--jvmArgs', '-XX:+CMSClassUnloadingEnabled,-XX:MaxPermSize=2048m',
            '--verbose',
            '--threads', '4')
            .directory(project.projectDir)
            .redirectInput(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    }
}

