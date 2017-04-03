# Gradle init scripts for Android

[![Build Status](https://travis-ci.org/vertu20140207/gradle-init-scripts.svg?branch=master)](https://travis-ci.org/vertu20140207/gradle-init-scripts)

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
  - Needs you to write a closure containing your service's logic.
- Custom keystore APK signing
  - Lets you avoid modifying build.gradle and chooses key for connected device.

# Usage

Add checkstyle, findbugs and pmd to the standard Android/gradle `check` task.
~~~~
./gradlew -I init.groovy build
~~~~

Upload source and test results to a local sonar server.
~~~~
./gradlew -I init.groovy sonar
~~~~

Make some documentation.
~~~~
./gradlew -I init.groovy javadoc
./gradlew -I init.groovy doxygen
~~~~

Run mutation tests against your unit tests.
~~~~
./gradlew -I init.groovy pitest
~~~~

Install release apk for a project, even though its build.gradle doesn't specify the release key.
~~~~
./gradlew -I signing-init.groovy installRelease
~~~~
