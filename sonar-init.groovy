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

        sonarqube {
            properties {
                property 'sonar.sourceEncoding', 'UTF-8'
            }
        }
        subprojects {
            if (project.hasProperty('android')) {
                setSonarProperties(project)
            }
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
                    final String variantName = variant.name[0..1].toUpperCase() + variant.name[1..-1]
                    if (project.hasProperty("connected${variantName}AndroidTest") &&
                            project.tasks.getByName("connected${variantName}AndroidTest")
                                    .resultsDir.isDirectory() &&
                            project.tasks.getByName("connected${variantName}AndroidTest")
                                    .resultsDir.listFiles().length > 0) {
                        property 'sonar.tests', android.sourceSets.androidTest.java.srcDirs
                        property 'sonar.junit.reportsPath',
                                project.tasks.getByName("connected${variantName}AndroidTest").resultsDir
                    }
                    if (project.hasProperty("create${variantName}AndroidTestCoverageReport") &&
                            null != project.tasks.getByName("create${variantName}AndroidTestCoverageReport")
                                    .coverageFile &&
                            project.tasks.getByName("create${variantName}AndroidTestCoverageReport")
                                    .coverageFile.exists()) {
                        property 'sonar.jacoco.reportPath',
                                project.tasks.getByName("create${variantName}AndroidTestCoverageReport")
                                        .coverageFile
                        property 'sonar.java.coveragePlugin', 'jacoco'
                        property 'sonar.dynamicAnalysis', 'reuseReports'
                    }
                }
/*
            } else if (android.hasProperty('libraryVariants')) {
                project.android.libraryVariants.all { variant ->
                    property "sonar.java.binaries", variant.javaCompile.destinationDir
                    property "sonar.java.libraries",
                            variant.javaCompile.classpath + files(project.android.bootClasspath)
                    final String variantName = variant.name.[0..1].toUpperCase() + variant.name.[1..-1]
                    if (project.hasProperty("connected${variantName}AndroidTest") &&
                            project.tasks.getByName("connected${variantName}AndroidTest")
                                    .resultsDir.isDirectory() &&
                            project.tasks.getByName("connected${variantName}AndroidTest")
                                    .resultsDir.listFiles().length > 0) {
                        property "sonar.tests", android.sourceSets.androidTest.java.srcDirs
                        property "sonar.junit.reportsPath",
                                project.tasks.getByName("connected${variantName}AndroidTest").resultsDir
                    }
                    if (project.hasProperty("create${variantName}AndroidTestCoverageReport") &&
                            null != project.tasks.getByName("create${variantName}AndroidTestCoverageReport")
                                    .coverageFile &&
                            project.tasks.getByName("create${variantName}AndroidTestCoverageReport")
                                    .coverageFile.exists()) {
                        property "sonar.jacoco.reportPath",
                                project.tasks.getByName("create${variantName}AndroidTestCoverageReport")
                                        .coverageFile
                        property "sonar.java.coveragePlugin", "jacoco"
                        property "sonar.dynamicAnalysis", "reuseReports"
                    }
                }
*/
            }
        }
    }
}
