//Dokka task
rootProject {
    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.16'
        }
    }
/*
    repositories {
        google()
    }
    apply plugin:'com.android.application'
    apply plugin:'kotlin-android'
    apply plugin:'org.jetbrains.dokka-android'
*/
}

projectsEvaluated {
    rootProject.subprojects {
        if (project.hasProperty('android')) {
            apply plugin:'org.jetbrains.dokka-android'
//            project.dokka {
//                outputFormat = 'javadoc'
//            }
        }
    }
}

