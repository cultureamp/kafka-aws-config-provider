package com.cultureamp.kafka.config.provider;

import org.apache.kafka.common.config.provider.ConfigProvider
import org.apache.kafka.common.config.ConfigData
import java.util.{Set => JSet, Map => JMap}
import scala.collection.JavaConverters._
import software.amazon.awssdk.services.ssm.SsmClient

import software.amazon.awssdk.services.ssm.model.{
  GetParameterRequest,
  GetParameterResponse
}


/** An implementation of ConfigProvider that retreives configurations from AWS
  * Systems Manager Parameter Store.
  *
  * To refer to a configuration from this provider use:
  * config.providers=ssm
  * config.providers.file.class=com.cultureamp.kafka.config.provider.SsmConfigProvider
  *
  * @param ssmClient the AWS Systems Manager client to be used
  * @param ttl the TTL in seconds for retrieved configurations
  */
class SsmConfigProvider extends ConfigProvider {

  val ssmClient: SsmClient = SsmClient.builder.build
  val configDataTtl: Long = 3600

  /** We don't need to configure anything */
  override def configure(configs: JMap[String, _]): Unit = ()

  /** Retrieves the data with the given keys at the given path.
    *
    * We will use the path reference the parameter store identifier. The key
    * is not used.
    * 
    * @param path the path where the data resides.
    * @param keys the keys whose values will be retrieved.
    * @return the configuration data.
    */
  override def get(path: String, keys: JSet[String]): ConfigData = {
    val data =
      keys.asScala.foldLeft(Map.empty: Map[String, String])((acc, key) => {
        acc + (key -> getKey(key))
      })

    new ConfigData(data.asJava, configDataTtl)
  }

  /** Retreives the parameter for a single key */
  def getKey(key: String): String = {
    val request: GetParameterRequest =
      GetParameterRequest
        .builder
        .name(key)
        // withDecryption is ignored for String and StringList parameter types
        .withDecryption(true)
        .build

    val response: GetParameterResponse =
      ssmClient.getParameter(request)

    response
      .parameter
      .value
  }

  /** Retrieves the data with the given keys at the given path.
    *
    * The path is not used. A key should always be provided. If a path is not
    * provided, this will throw a scala.NotImplementedError.
    * 
    * @param path the path where the data resides.
    * @return the configuration data.
    */
  @throws(classOf[NotImplementedError])
  override def get(path: String): ConfigData = {
    throw new NotImplementedError(
      """A key should always be provided to the SsmConfigProvider.

         Note that the variable reference is of the form:
         `${provider:[path:]key}`"""
    )
  }

  /** We could also implement subscribe to listen for changes to credentials. I
    * will skip this for now, but it might become important down the track.
    *
    * override def subscribe(...)
    * override def unsubscribe(...)
    */

  /** We don't need to close anything */
  override def close(): Unit = ()
}
