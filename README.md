# BYO-Tech

A collection of tools for technical stuff

## Environment

* IntelliJ IDEA 2025.1.2 (Community Edition)
* Gradle 8.14.2
* Java 24

## Custom tasks

* ``./gradlew generateJavaInfoFile`` or ``./gradlew gJIF``: generate java files with information about build.
* ``./gradlew generatePkgInfoFile``  or ``./gradlew gPIF``: generate package info files for the previously generated
  java files.
* ``./gradlew sanitizeVersionsFile`` or ``./gradlew sVF``: sanitize the versions file (versions.yml).
* ``./gradlew bumpMajorVersion``: increase major version.
* ``./gradlew bumpMinorVersion``: increase minor version.
* ``./gradlew bumpPatchVersion``: increase patch version.
* ``./gradlew updateSinceTag``: update unset `@since` tag in code.
* ``./gradlew copyLog4J2Conf4Test``: copy the Log4J2 configuration file from the buildSrc to the test resources.
* ``./gradlew copyLog4J2Conf``: copy the Log4J2 configuration file from the buildSrc to the resources.

## GIT

Git hooks are shared in the directory `.githooks/`. Configuring the property **core.hooksPath** (git >= 2.9) is
required to use them. This configuration is done when running the [setup_project.bash](scripts/setup_project.bash)
script. You can configure manually by running the following command: ``git config --local core.hooksPath .githooks/``.

## [Scripts](scripts)

All scripts shall be written in Bash.

Logging level is controlled through an environment property: `LOG_LEVEL`. Allowed levels are (case-sensitive):

* TRACE
* DEBUG
* INFO
* WARN
* ERROR
* FATAL

By default, `INFO` is considered.

### [setup_project.bash](scripts/setup_project.bash)

Run all commands used to set up properly the project (git hooks).

### [check_encoding.bash](scripts/check_encoding.bash)

List all files with encoding not supported. We do not put this script in pre-commit because it is slow on Windows.

### lib.*

Those scripts are libraries that other scripts can use. They have an _include_ mechanism that protect them from being
loaded multiple times (which can create issues with readonly variables). This mechanism is implemented by defining a
unique environment variable for each library. Here is the template to follow:

```bash
# Those two line make sure this library is sourced
: ${LIB_DIR:=$(dirname ${BASH_SOURCE[0]})}
source "$LIB_DIR/lib.source.bash"

# The unique environment variable that prevents multiple loading. Must start with LIB_
[[ -z ${LIB_XXX+x} ]] && export LIB_XXX= || return 0 # Cannot source it more than once

# How other libraries shall be sourced from a library
source "$LIB_DIR/lib.util.bash"
```
