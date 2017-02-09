// Insert signingConfigs block into build.gradle.
// Keystore name is set with command parameter or defaults to '.keystore'
// Key alias is taken from connected device properties or defaults to 'androiddebugkey'
apply from:'build.gradle'
final boolean ONE_CONNECTED_DEVICE = 0 ==
        new ProcessBuilder(project.android.adbExe.path, 'get-serialno')
                .start()
                .waitFor()
String signCert =
        (project.hasProperty('signCert')) ? signCert = project.signCert : ''
String signFor =
        (project.hasProperty('signFor')) ? signFor = project.signFor : 'androiddebugkey'
if (ONE_CONNECTED_DEVICE || null != System.getenv('ANDROID_SERIAL')) {
    signFor = getAndroidProperty('ro.product.name') + '_' + getAndroidProperty('ro.build.type')
}

project.android {
    signingConfigs {
        signingConfig {
            storeFile new File(buildscript.sourceFile.parentFile,
                    "${signCert}.keystore")
            storePassword 'android'
            keyAlias signFor
            keyPassword storePassword
        }
    }
    buildTypes {
        debug {
            signingConfig signingConfigs.signingConfig
        }
        release {
            signingConfig signingConfigs.signingConfig
        }
    }
}
String getAndroidProperty(final String prop) {
    String result = ''
    final Process PROCESS =
        new ProcessBuilder(project.android.adbExe.path, 'shell', 'getprop', prop)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
    PROCESS.waitFor()
// Gradle doesn't support try-with-resources :-(
//    try (final BufferedReader READER =
//            new BufferedReader(new InputStreamReader(PROCESS.getInputStream(), 'US-ASCII'));
    try {
        final BufferedReader READER = new BufferedReader(
                new InputStreamReader(PROCESS.inputStream, 'US-ASCII'))
        result = READER.readLine()
        READER.close()
    } catch (final IOException e) {
        project.logger.log(0, e.localizedMessage, e)
    }
    result
}
