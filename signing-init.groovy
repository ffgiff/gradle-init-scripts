// Find application projects and insert correct signingConfig.
import java.nio.file.FileSystems

settingsEvaluated { settings ->
    settings.rootProject.children.each {
        try {
            final BufferedReader READER = new BufferedReader(new FileReader(it.buildFile))
            String line = ''
            while (line != null) {
                if (line.matches('^\\s*apply\\s+plugin\\s*:\\s*[\'"]com.android.application[\'"]\\s*$')) {
                    it.setBuildFileName(
                            FileSystems.default.getPath(it.dir.toString())
                                    .relativize(FileSystems.default.getPath(new File(
                                            buildscript.sourceFile.parentFile,
                                            'fix-signing-config.groovy').toString()))
                                    .toString())
                    break
                }
                line = READER.readLine()
            }
            READER.close()
        } catch (final IOException e) {
            logger.log(LogLevel.ERROR, e.localizedMessage, e)
        }
    }
}
