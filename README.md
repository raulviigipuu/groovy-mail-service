# groovy-mail-service

Sending mail with groovy and jvm

## todo

  - conf auto reload 

## run

    gradlew run
    gradlew run --args="-t 'john.doe@unknown.com' -s 'no subject' --content 'some message'"

## dist

Generating distributable packages

### Executables

    gradlew installDist

### Zip/tar for linux/windows

    gradlew assemble

### run dist

Win

    build\install\app-one\bin\app-one.bat

Linux/Mac

    build/install/app-one/bin/app-one