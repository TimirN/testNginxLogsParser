# The simple parser for Nginx logs (default format)

## How to build

### Prerequisites

* [jdk8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [sbt1](https://www.scala-sbt.org/download.html)

### How to assembly

```shell
sbt assembly
```
### Jar's params

1) logs-path - the path for log file
2) save-to - the path for saving result
3) format-complex - flag for changing result's format, if it is true the result will be in complex format, default - false

## Running the tests

```shell
sbt test
```
Logs sample is in src/test/resources/example.log
Submit to local cluster:
```shell
spark-submit --class example.Main --master local target/scala-2.11/nginxLogs-assembly-0.1.0-SNAPSHOT.jar --logs-path src/test/resources/example.log --save-to tmp/
```
