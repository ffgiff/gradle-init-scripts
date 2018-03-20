// Insert signingConfigs block into build.gradle.
// Keystore name is set with command parameter or defaults to '.keystore'
// Key alias is taken from connected device properties or defaults to 'androiddebugkey'
apply from:'build.gradle'
final boolean ONE_CONNECTED_DEVICE = 0 ==
        new ProcessBuilder(project.android.adbExe.path, 'get-serialno')
                .start()
                .waitFor()
final String SIGN_CERT = (project.hasProperty('signCert')) ? project.signCert : ''
final String SIGN_FOR = (project.hasProperty('signFor'))
        ? project.signFor : (ONE_CONNECTED_DEVICE || null != System.getenv('ANDROID_SERIAL'))
                ? getAndroidProperty('ro.product.name') + '_' + getAndroidProperty('ro.build.type') : 'androiddebugkey'

project.android {
    signingConfigs {
        signingConfig {
            storeFile new File(buildscript.sourceFile.parentFile,
                    "${SIGN_CERT}.keystore")
            storePassword 'android'
            keyAlias SIGN_FOR
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
        project.logger.log(LogLevel.ERROR, e.localizedMessage, e)
    }
    result
}
