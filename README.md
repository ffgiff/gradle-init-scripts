# Gradle init scripts for Android

These init scripts augment an Android build by adding support for:

- Static checkers
  - Checkstyle
  - Findbugs
  - Pmd
  - Sonar
- Documentation
  - Doxygen
  - Javadoc
- Test
  - Pitest
- External APK signing

# Usage

Add checkstyle, findbugs and pmd to the standard Android/gradle `check` task.
~~~~
./gradlew -I init.gradle build
~~~~

Upload source and test results to a local sonar server.
~~~~
./gradlew -I init.gradle sonar
~~~~

Make some documentation.
~~~~
./gradlew -I init.gradle javadoc
./gradlew -I init.gradle doxygen
~~~~

Run mutation tests against your unit tests.
~~~~
./gradlew -I init.gradle pitest
~~~~
