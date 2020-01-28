package com.cultureamp.kafka.config.provider;

import org.scalatest._
import scala.collection.JavaConverters._
import org.scalamock.scalatest.MockFactory
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.{
  GetParameterRequest,
  GetParameterResponse,
  Parameter
}

class SsmConfigProviderSpec extends FlatSpec with Matchers with MockFactory {
  "SsmConfigProvider" should "throw an error if no key is provided" in {
    val configProvider = new SsmConfigProvider()

    assertThrows[NotImplementedError] {
      configProvider.get("duh")
    }
  }

  it should "return a String parameter when a key is provided" in {
    val paramKey = "foo/password"
    val paramValue = "bar"

    val request =
      GetParameterRequest
        .builder
        .name(paramKey)
        .withDecryption(true)
        .build

    val result =
      GetParameterResponse
        .builder
        .parameter(Parameter.builder.value(paramValue).build)
        .build

    val mockSsmClient = mock[SsmClient]

    (mockSsmClient.getParameter (_: GetParameterRequest))
      .expects(request)
      .returning(result)
      .once()

    val configProvider = new SsmConfigProvider(mockSsmClient)

    val configData = configProvider.get("", Set(paramKey).asJava)

    (configData.data()) should be (Map(paramKey -> paramValue).asJava)
  }
}
