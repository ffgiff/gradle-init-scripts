import org.gradle.api.Project
import org.gradle.api.file.FileCollection

ext.COVERAGE_REPORT_TASK_NAME = 'createDebugCoverageReport'
projectsEvaluated {
    ext.applyJacocoTestReport = {
        if (project.hasProperty('android')) {
            if (project.tasks.findByName(COVERAGE_REPORT_TASK_NAME)) {
                addJacocoTestReport project
            }
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applyJacocoTestReport
    } else {
        rootProject.subprojects applyJacocoTestReport
    }
}

// Jacoco aggregation report
void addJacocoTestReport(final Project project) {
    project.apply plugin:'jacoco'

    project.jacoco {
        toolVersion = '0.8.1'
    }

    project.tasks.withType(Test) {
        jacoco.includeNoLocationClasses = true
    }

    project.tasks.create([name:'jacocoTestReport',
                          type:JacocoReport,
                          dependsOn:['testDebugUnitTest', COVERAGE_REPORT_TASK_NAME],]) {
        reports {
            xml.enabled = true
            html.enabled = true
        }

        final List<String> FILE_FILTER =
                ['**/R.class', '**/R$*.class', '**/BuildConfig.*', '**/Manifest*.*', '**/*Test*.*', 'android/**/*.*',]
        final FileCollection DEBUG_TREE =
                fileTree(dir:"$project.buildDir/tmp/kotlin-classes/debug", excludes:FILE_FILTER)
        final String MAIN_SRC = "$project.projectDir/src/main/java"

        sourceDirectories = files([MAIN_SRC])
        classDirectories = files([DEBUG_TREE])
        executionData = fileTree(dir:project.buildDir, includes:[
                'jacoco/testDebugUnitTest.exec', 'outputs/code-coverage/connected/*coverage.ec', '../jacoco.exec',
        ])
    }
}
