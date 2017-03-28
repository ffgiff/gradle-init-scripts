rootProject {
    buildscript {
        repositories {
            maven {
                url 'https://plugins.gradle.org/m2/'
            }
        }
        dependencies {
            classpath 'org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:2.1-rc3'
        }
    }
}

projectsEvaluated {
    rootProject {
        apply plugin:'org.sonarqube'

        tasks.sonarqube.group 'Verification'
        sonarqube {
            properties {
                property 'sonar.sourceEncoding', 'UTF-8'
            }
        }
        ext.applySonar = {
            if (project.hasProperty('android')) {
                setSonarProperties(project)
            }
        }
        if (subprojects.isEmpty()) {
            rootProject applySonar
        } else {
            subprojects applySonar
        }
    }
}
void setSonarProperties(final Project project) {
    project.sonarqube.properties {
        property 'sonar.projectBaseDir', rootProject.projectDir.absolutePath
        if (!project.android.sourceSets.main.java.sourceFiles.isEmpty()) {
            property 'sonar.sources', project.android.sourceSets.main.java.srcDirs
            if (project.android.hasProperty('applicationVariants')) {
                project.android.applicationVariants.all { variant ->
                    property 'sonar.java.binaries', variant.javaCompile.destinationDir
                    property 'sonar.java.libraries',
                            variant.javaCompile.classpath + files(project.android.bootClasspath)
                    final String VARIANT_NAME = variant.name[0].toUpperCase() + variant.name[1..-1]
                    final Task DEVICE_TEST_TASK =
                            project.tasks.findByName("connected${VARIANT_NAME}AndroidTest")
                    if (DEVICE_TEST_TASK != null &&
                            DEVICE_TEST_TASK.resultsDir.isDirectory() &&
                            DEVICE_TEST_TASK.resultsDir.listFiles().length > 0) {
                        property 'sonar.tests', project.android.sourceSets.androidTest.java.srcDirs
                        property 'sonar.junit.reportsPath',
                                  DEVICE_TEST_TASK.resultsDir
                    }
                    final Task COVERAGE_REPORT_TASK =
                            project.tasks.findByName("create${VARIANT_NAME}AndroidTestCoverageReport")
                    if (COVERAGE_REPORT_TASK != null &&
                            COVERAGE_REPORT_TASK.coverageFile != null &&
                            COVERAGE_REPORT_TASK.coverageFile.exists()) {
                        property 'sonar.jacoco.reportPaths',
                                  COVERAGE_REPORT_TASK.coverageFile
                        property 'sonar.java.coveragePlugin', 'jacoco'
                        property 'sonar.dynamicAnalysis', 'reuseReports'
                    }
                }
            }
        }
    }
}
