
###############################################################################
# BUILD
###############################################################################
FROM maven:3.5-jdk-8 AS build

# we need to create a new user as some of the
# tests rely on files being readonly and fail
# when run as root
RUN useradd -ms /bin/bash baleen
WORKDIR /home/baleen

COPY . .

RUN chown -R baleen *
USER baleen

RUN mvn package

###############################################################################
# RELEASE
###############################################################################
FROM java:8-jdk-alpine AS release

ARG baleen_version=2.4.1-SNAPSHOT

COPY --from=build /home/baleen/target/baleen-${baleen_version}.jar ./baleen.jar

EXPOSE 6413

ENTRYPOINT [ "java", "-jar", "baleen.jar" ]
