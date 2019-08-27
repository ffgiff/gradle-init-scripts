import org.gradle.api.Project
import org.gradle.api.file.FileCollection

ext.COVERAGE_REPORT_TASK_NAME = 'createDebugCoverageReport'
gradle.projectsEvaluated {
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

ext.FILE_FILTER =
        ['**/R.class', '**/R$*.class', '**/BuildConfig.*', '**/Manifest*.*', '**/*Test*.*', 'android/**/*.*',]

// Jacoco aggregation report
void addJacocoTestReport(final Project project) {
    project.apply plugin:'jacoco'

    project.jacoco {
        toolVersion = '0.8.2'
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

        final FileCollection DEBUG_TREE =
                fileTree(dir:"$project.buildDir/tmp/kotlin-classes/debug", excludes:FILE_FILTER) +
                getDebugSources(project)
        final String MAIN_SRC = "$project.projectDir/src/main/java"

        sourceDirectories = files([MAIN_SRC])
        classDirectories = files([DEBUG_TREE])
        executionData = fileTree(dir:project.buildDir, includes:[
                'jacoco/testDebugUnitTest.exec', 'outputs/code-coverage/connected/*coverage.ec', '../jacoco.exec',
        ])
    }
}

FileCollection getDebugSources(final Project project) {
    FileCollection classes = project.files()
    for (final String variantType : ['applicationVariants', 'libraryVariants', 'testVariants']) {
        if (project.android.hasProperty(variantType)) {
            project.android."${variantType}".all { variant ->
                if (variant.buildType.name == 'debug') {
                    classes += fileTree(dir:"${variant.javaCompileProvider.get().destinationDir}", excludes:FILE_FILTER)
                }
            }
        }
    }
    classes
}

