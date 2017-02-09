// Android-gradle external signing.
// This allows an external signing service to be used by providing
// a closure in a file given by -PsigningClosure.
// Example:
// def signingFunction(project, variant, output) {
//     // Sign output.outputFile somehow.
// }
// ext.sign = this.&signingFunction
//
projectsEvaluated {
    rootProject.subprojects {
        if (project.hasProperty('android')) {
            if (android.hasProperty('applicationVariants')) {
                project.android.applicationVariants.all { variant ->
                    signApk(project, variant)
                    if (null != variant.testVariant) {
                        signApk(project, variant.testVariant)
                    }
                }
            }
        }
    }
}

def signApk(project, variant) {
    variant.assemble.doLast {
        variant.outputs.each { output ->
            if (project.hasProperty('signingClosure')) {
                apply from:project.signingClosure
                sign(project, variant, output)
            } else {
                println 'No signing closure supplied.'
            }
        }
    }
}