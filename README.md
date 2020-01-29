# Kafka AWS Config Provider

Kafak ConfigProvider implementation using AWS System Manger Parameter Store.

## Kafka Connect getting started

This package can be used as an [external secrets](https://docs.confluent.io/current/connect/security.html#externalizing-secrets) provider for Kafka Connect.

### Step 1: Install plugin jar

To install the custom `ConfigProvider` implementation, add a new subdirectory containing the JAR file to the directory that is on Connect’s plugin.path, and (re)start the Connect workers. 

When the Connect worker starts up it instantiates all `ConfigProvider` implementations specified in the worker configuration. 

All properties prefixed with config.providers.[provider].param. are passed to the `configure()` method of the `ConfigProvider`. When the Connect worker shuts down, it calls the `close()` method of the `ConfigProvider`.

### Step 2: Add configuration provider

Here is an example of how to use `SsmConfigProvider` in the worker configuration:

```
config.providers=ssm
config.providers.ssm.class=com.cultureamp.kafka.config.provider.SsmConfingProvider
```

If you are using the `confluentinc/cp-kafka-connect` Docker image, add the following environment variables instead.

```
CONNECT_CONFIG_PROVIDERS=ssm
CONNECT_CONFIG_PROVIDERS_ENV_CLASS=com.cultureamp.kafka.config.provider.SsmConfingProvider
```

### Step 3: Reference external variables

You can now reference configuration variables in the connector configuration as follows:

```
# Variable references are of the form ${provider:[path:]key} where the path is optional
my.secret=${ssm:/super/secret/password}
```

## How to build

This project is built using [SBT](https://www.scala-sbt.org/). Follow the instructions to [install SBT](https://www.scala-sbt.org/download.html) then run:

```
sbt assembly
```

This will create a jar:

```
./target/scala_{SCALA_VERSION}/jars/com.cultureamp_kafka-aws-config-provider_{SCALA_VERSION}_{PROJECT_VERSION}.jar
```
