# Harbor

[![Build Status](https://travis-ci.org/Augugrumi/harbor.svg?branch=master)](https://travis-ci.org/Augugrumi/harbor)
[![](https://images.microbadger.com/badges/image/augugrumi/harbor.svg)](https://microbadger.com/images/augugrumi/harbor "Get your own image badge on microbadger.com")

Harbor is a simple backend, written in Java, capable to launch k8s YAML
definitions in a cluster deployment.

## How to build it

To build it, first of all check to have [kubernetes-client/java](https://github.com/kubernetes-client/java)
installed in your system. To install it, open a terminal and write:
```bash
git clone --recursive https://github.com/kubernetes-client/java && \
cd java && \
git checkout 8d6ab536f565ee951141b14bfd170629399d8c67 && \
mvn install -DskipTests
```

This will install the Java client necessary to Harbor to properly work.
Next, compile Harbor as follows:
```bash
git clone https://github.com/Augugrumi/harbor.git && \
cd harmor && \
mvn package
```

This will create a "jar with dependencies" in `target/`. You can launch
this with `java -jar` from the project root:
```bash
java -jar target/harbor-<version>-jar-with-dependencies.jar -f api_definition.json -p 57684
```

## Flags
There are different flags that can be used to customize Harbor behaviour:
- `-f`: file pointing to an API definition
- `-p`: specify the port where Harbor will run
- `-k`: specify kubernetes api URL
- `-y`: specify Harbor YAML home

## License

This software is under GPLv3+ License