# Baleen Build Instructions

## Prerequisites
- Oracle JDK 8
- Eclipse IDE for Java EE

## Instructions

1. Download the Baleen source from GitHub
2. In Eclipse, import a new Maven Project, selecting the top level POM (`baleen.pom`) to import: File -> Import... -> Maven -> Existing Maven Project
3. Right click on `baleen` project, select Run As... -> 3. Maven Build...
4. Type `package` into the Goals box, and then click Run
5. The Baleen JAR will be built and saved in the target directory under the top level project directory
6. Run Baleen by running `java -jar baleen-2.0.0.jar` and then navigating to <http://localhost:6413>
