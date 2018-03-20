import org.gradle.api.Project

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'org.pitest:pitest:1.3.2'
        classpath 'org.pitest:pitest-command-line:1.3.2'
    }
}

projectsEvaluated {
    ext.applyPitest = {
        if (project.hasProperty('android')) {
            task pitest {
                // Meta-task, will dependOn pitestDebugUnitTest etc.
                group 'Verification'
                description 'Runs mutation tests using pitest tool.'
            }
            project.tasks.test.dependsOn.each { testTask ->
                if (testTask.metaClass.respondsTo(testTask, 'startsWith') &&
                        testTask.startsWith('test')) {
                    addPitestTask(project, testTask)
                    project.pitest.dependsOn += [project.tasks."pi$testTask"]
                }
            }
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyPitest
    } else {
        rootProject.subprojects applyPitest
    }
}

void addPitestTask(final Project project, final String testTask) {
    project.tasks.create([type:JavaExec,
                          name:"pi$testTask",
                          dependsOn:["$testTask"],]) {
        ext.testSource = project.android.sourceSets.main.java.srcDirs
        project.android.unitTestVariants.matching
                { it.name == testTask[4..5].toLowerCase() + testTask[5..-1] }
                .sourceSets.each { testSource ->
            ext.testSource += testSource.java.srcDirs
        }
        if (project.hasProperty('processReleaseManifest')) {
            args '--reportDir', "${project.reportsDir.path}/pitest"
            args '--sourceDirs', files(ext.testSource).asPath.replaceAll(':', ',')
            args '--targetClasses', "${project.processReleaseManifest.packageOverride}.*"
            args '--threads', '4'
            args '--timestampedReports', 'false'
//            args '--verbose'
            classpath files(buildscript.scriptClassPath.asFiles) + project.tasks[testTask].classpath
            ignoreExitValue true
            jvmArgs '-XX:+CMSClassUnloadingEnabled', '-XX:MaxPermSize=2048m'
            main 'org.pitest.mutationtest.commandline.MutationCoverageReport'
            workingDir project.projectDir
        }
    }
}

