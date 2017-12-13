# Baleen Build Instructions

## Prerequisites
- Oracle JDK 8
- Apache Maven 3

## Instructions

1. Download the Baleen source from GitHub
2. Run `mvn package` from the Baleen project directory
3. Optionally run `mvn javadoc:aggregate-jar` to build Javadoc
4. The Baleen JAR will be built and saved in the target directory under the top level project directory
5. Run Baleen by running `java -jar baleen-2.4.1.jar` and then navigating to <http://localhost:6413>

## Docker Instructions

1. Download the Baleen source from GitHub
2. Run `docker build -t baleen:2.4.1-SNAPSHOT .` from the root of the project
5. Run Baleen by running `docker run --name baleen -p 6413:6413 baleen:2.4.1-SNAPSHOT` and then navigating to <http://localhost:6413>

