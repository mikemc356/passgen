# PassGen for Java 
An implementation of IBMs Passticket written in Java

## Building
Build the project using Maven :

First build the common library in repo SXCommon :

    mvn install

Then build the Passgen app :

	mvn package

Run this command from the project root and this builds :

    - Passgen for Java runtime 
    - Passgen for Java webapp
    - Passgen for Java installer

Build the dependancy SXCommon before attempting the build.

Install using the installer :

    java -jar (installer jar)
