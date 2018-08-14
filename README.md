# Harbor

[![Build Status](https://travis-ci.org/Augugrumi/harbor.svg?branch=master)](https://travis-ci.org/Augugrumi/harbor)
[![](https://images.microbadger.com/badges/image/augugrumi/harbor.svg)](https://microbadger.com/images/augugrumi/harbor "Get your own image badge on microbadger.com")

Harbor is a simple backend, written in Java, capable to launch k8s YAML
definitions in a cluster deployment.

## Documentation

Of course we have a Javadoc! To generate one, clone the project and then
run in a terminal:
```bash
mvn javadoc:javadoc
```

This will generate a javadoc in `./target/site/apidocs`.

## How to build it

To build it, first of all check to have [kubernetes-client/java](https://github.com/kubernetes-client/java)
installed in your system. To install it, open a terminal and write:
```bash
git clone --recursive https://github.com/kubernetes-client/java && \
cd java && \
git checkout 8d6ab536f565ee951141b14bfd170629399d8c67 && \
mvn install -DskipTests && \
cd ..
```

This will install the Java client necessary to Harbor to properly work.
Next, compile Harbor as follows:
```bash
git clone https://github.com/Augugrumi/harbor.git && \
cd harbor && \
mvn package
```

This will create a "jar with dependencies" in `target/`. You can launch
this with `java -jar` from the project root:
```bash
java -jar target/harbor-<version>-jar-with-dependencies.jar -f api_definition.json -p 57684
```

## Flags
There are different flags that can be used to customize Harbor behaviour:
- `-f`: file pointing to an API definition (the default one will use the
 hello world API definition)
- `-p`: specify the port where Harbor will run (default to 80)
- `-k`: specify kubernetes api URL (default to localhost or, if it's
 running as a container in docker, to kubernetes environment variables)
- `-y`: specify Harbor YAML home (default to `.harbor/yaml`)

## Docker image
There is also a docker image that you can use. Download it with:
```bash
docker pull augugrumi/harbor
```

At this point, you can run it with:
```bash
docker run --rm -p57684:80 augugrumi/harbor
```

### Environment variables
There are environment variables, equal to the flags previously
described, that allow you to customize Harbor behaviour.
These are:
- `HARBOR_PORT`: custom port in which harbor will run
- `HARBOR_API_CONFIG`: path to your API configuration json
- `HARBOR_KUBERNETES_URL`: url to your Kubernetes API endpoint
- `HARBOR_YAML_STORAGE_PATH`: path to an empty folder where Harbor will create it's home for YAML storage

### Building your own docker image
The Dockerfile in the project uses _multi-stage docker builds_. To
build it, open a terminal and type:
```bash
docker build -t <your nickname>/harbor .
```

## License

This software is under GPLv3+ License