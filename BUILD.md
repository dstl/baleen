# Baleen Build Instructions

## Prerequisites
- Oracle JDK 8
- Apache Maven 3

## Instructions

1. Download the Baleen source from GitHub
2. Run `mvn package` from the Baleen project directory
3. Optionally run `mvn javadoc:aggregate-jar` to build Javadoc
4. The Baleen JAR will be built and saved in the target directory under the top level project directory
5. Run Baleen by running `java -jar baleen-2.5.0-SNAPSHOT.jar` and then navigating to <http://localhost:6413>