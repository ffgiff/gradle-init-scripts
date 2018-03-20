// Android-gradle external signing.
// This allows an external signing service to be used by providing
// a closure in a file given by -PsigningClosure.
// Example:
// void signingFunction(project, variant, output) {
//     // Sign output.outputFile somehow.
// }
// ext.sign = this.&signingFunction
//
import org.gradle.api.Project

buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
    }
}

projectsEvaluated {
    ext.applySigning = {
        if (project.hasProperty('android') &&
                android.hasProperty('applicationVariants')) {
            project.android.applicationVariants.all { variant ->
                signApk(project, variant)
                if (null != variant.testVariant) {
                    signApk(project, variant.testVariant)
                }
            }
        }
    }
    if (rootProject.subprojects.isEmpty()) {
        rootProject applySigning
    } else {
        rootProject.subprojects applySigning
    }
}

void signApk(final Project project, final Object variant) {
    variant.assemble.doLast {
        variant.outputs.each { output ->
            if (project.hasProperty('signingClosure')) {
                apply from:project.signingClosure
                sign(project, variant, output)
            } else {
                project.logger.log(LogLevel.WARN, 'No signing closure supplied.')
            }
        }
    }
}
