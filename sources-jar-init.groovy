projectsEvaluated {
    rootProject.subprojects {
        if (it.hasProperty('android')) {
            task sourcesJar(type:Jar) {
                classifier = 'sources'
                from android.sourceSets.main.javaDirectories
            }
        }
    }
}
