// Insert signingConfigs block into build.gradle.
// Keystore name is set with command parameter or defaults to '.keystore'
// Key alias is taken from connected device properties or defaults to 'androiddebugkey'
apply from:'build.gradle'
final boolean oneConnectedDevice = 0 ==
        new ProcessBuilder(project.android.adbExe.path, 'get-serialno')
                .start()
                .waitFor()
String signCert =
        (project.hasProperty('signCert')) ? signCert = project.signCert : ''
String signFor =
        (project.hasProperty('signFor')) ? signFor = project.signFor : 'androiddebugkey'
if (oneConnectedDevice || null != System.getenv('ANDROID_SERIAL')) {
    final Process getName =
        new ProcessBuilder(project.android.adbExe.path, 'shell', 'getprop', 'ro.product.name')
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
    getName.waitFor()
    final Process getType =
        new ProcessBuilder(project.android.adbExe.path, 'shell', 'getprop', 'ro.build.type')
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
    getType.waitFor()
// Gradle doesn't support try-with-resources :-(
//    try (final BufferedReader nameReader =
//            new BufferedReader(new InputStreamReader(getName.getInputStream(), 'US-ASCII'));
//         final BufferedReader typeReader =
//            new BufferedReader(new InputStreamReader(getType.getInputStream(), 'US-ASCII'))) {
    try {
        final BufferedReader nameReader = new BufferedReader(
                new InputStreamReader(getName.inputStream, 'US-ASCII'))
        final BufferedReader typeReader = new BufferedReader(
                new InputStreamReader(getType.inputStream, 'US-ASCII'))
        signFor = nameReader.readLine() + '_' + typeReader.readLine()
        nameReader.close()
        typeReader.close()
    } catch (final IOException e) {
        project.logger.log(0, e.localizedMessage, e)
    }
}

project.android {
    signingConfigs {
        signingConfig {
            storeFile new File(buildscript.sourceFile.parentFile,
                    "${signCert}.keystore")
            storePassword 'android'
            keyAlias signFor
            keyPassword 'android'
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
