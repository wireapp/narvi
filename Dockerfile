FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine AS build
LABEL description="Wire Export Backup Tool"
LABEL project="wire-bots:exports"

ENV PROJECT_ROOT /src
WORKDIR $PROJECT_ROOT

# Copy gradle settings
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew $PROJECT_ROOT/
# Make sure gradlew is executable
RUN chmod +x gradlew
# Copy gradle specification
COPY gradle $PROJECT_ROOT/gradle
# Download gradle
RUN ./gradlew --version --no-daemon
# download and cache dependencies
RUN ./gradlew resolveDependencies --no-daemon

# Copy project and build
COPY src $PROJECT_ROOT/src
RUN ./gradlew shadowJar --no-daemon

# runtime stage
FROM dejankovacevic/bots.runtime:2.10.3

ENV APP_ROOT /opt/narvi
WORKDIR $APP_ROOT

# Copy configuration
COPY narvi.yaml $APP_ROOT/

# Copy built target
COPY --from=build /src/build/libs/narvi.jar $APP_ROOT/

# create version file
ARG release_version=development
ENV RELEASE_FILE_PATH=$APP_ROOT/release.txt
RUN echo $release_version > $RELEASE_FILE_PATH

EXPOSE  8080 8081 8082
ENTRYPOINT ["java", "-jar", "narvi.jar", "server", "narvi.yaml"]
