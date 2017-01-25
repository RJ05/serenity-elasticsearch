# Serenity ElasticSearch Uploaded

This is a simple (and experimental) utility that lets you upload Serenity test results to an ElasticSearch server.

## Building the project

The project uses Gradle and the [Gradle Wrapper](https://docs.gradle.org/current/userguide). You can build the executable JAR using

```
$gradlew jar
```
or (on Windows)
```
gradlew.bat jar
```

This will produce an executable JAR file in the `builds/lib` directory

## Running the project

You can run the executable jar as shown here:

```
$java -jar build/libs/elasticsearch-uploader-0.0.1-SNAPSHOT.jar --help
```

Usage options are:
```
Usage: java -jar elasticsearch-uploader.jar [options]
  Options:
    --aux, -x
      Elasticsearch auxilliary port
      Default: 9200
    --cluster, -c
      Elasticsearch Cluster name
      Default: elasticsearch
  * --environment, -e
      The environment the tests are running against
      Default: <empty string>
    --help, -?
      Display this message
      Default: false
  * --host, -h
      Elasticsearch host address
      Default: localhost
    --port, -p
      Elasticsearch port
      Default: 9300
  * --project, -pr
      The project name
      Default: <empty string>
    -g, -d
      Directory containing the JSON test outcomes
      Default: target/site/serenity
```