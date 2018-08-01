FROM openjdk:jdk-slim as builder

RUN mkdir -p /build/
WORKDIR /build/

COPY . /build/

# Install maven
RUN apt-get update &&  \
    apt-get install -y maven git

# Installs https://github.com/kubernetes-client/java
RUN path="$(pwd)" && \
    cd .. && \
    git clone --recursive https://github.com/kubernetes-client/java && \
    cd java && \
    git checkout client-java-parent-2.0.0 && \
    mvn install -DskipTests && \
    cd $path

# Builds Harbor
RUN mvn package && \
    cd target/ && \
    mkdir -p bundle && \
    cp ../api_sample.json bundle/ && \
    cp $(ls | grep dependencies.jar) bundle/ && \
    cd bundle && \
    mv *.jar harbor.jar


FROM openjdk:8-jre-alpine

LABEL maintainer="poloniodavide@gmail.com"
LABEL license="GPLv3+"
LABEL description="Harbor Docker image"

# Available environment variables:
# -HARBOR_PORT: custom port in wich harbor will run (the default is 80)
# -HARBOR_API_CONFIG: path to your API configuration json
ENV HARBOR_API_CONFIG=api_sample.json

RUN mkdir -p /srv/config/
VOLUME config/
WORKDIR /srv/

COPY --from=builder /build/target/bundle/harbor.jar /srv/
COPY --from=builder /build/target/bundle/api_sample.json /srv/

ENTRYPOINT ["java", "-jar", "harbor.jar"]

