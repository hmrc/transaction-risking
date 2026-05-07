/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.transactionrisking.connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.{MimeTypes, Status}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.transactionrisking.config.AppConfig
import uk.gov.hmrc.transactionrisking.models.request.StrRiskRequest
import uk.gov.hmrc.transactionrisking.models.response.StrRiskResponse
import uk.gov.hmrc.transactionrisking.support.WireMockSupport

import java.util.Base64
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*

class StrRiskConnectorISpec
  extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with WireMockSupport {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val correlationId: String = "test-correlation-id"

  private def await[T](f: scala.concurrent.Future[T]): T = Await.result(f, 5.seconds)

  private val url = "/str/risk/insights"
  private val request = StrRiskRequest(vatRegistrationNumber = "GB123456789")
  private val requestJson = Json.toJson(request).toString()

  private val successJson: JsValue = Json.parse(
    """{
      |  "riskScore": 12.46,
      |  "riskCorrelationId": "123e4567-e89b-12d3-a456-426614174000",
      |  "reasons": ["VRN is 3 hops from something risky."]
      |}""".stripMargin
  )

  private val malformedJson: JsValue = Json.parse("""{"crazyJuice": "sass"}""")
  private val expectedResponse = successJson.validate[StrRiskResponse].get

  class Test {
    val username = "transaction-risking"
    val token = "local-dev-token"

    val mockAppConfig: AppConfig = mock[AppConfig]
    when(mockAppConfig.cipRiskServiceBaseUrl).thenReturn(s"http://localhost:$wireMockPort$url")
    when(mockAppConfig.cipRiskUsername).thenReturn(username)
    when(mockAppConfig.cipRiskToken).thenReturn(token)

    val connector = new StrRiskConnector(httpClient, mockAppConfig)

    def stub(body: Option[String], status: Int): StubMapping =
      wireMockServer.stubFor(
        post(urlPathEqualTo(url))
          .withHeader("Content-Type", equalTo(MimeTypes.JSON))
          .withRequestBody(equalToJson(requestJson, true, false))
          .willReturn(body.fold(aResponse().withStatus(status))(b => aResponse().withBody(b).withStatus(status)))
      )
  }

  "StrRiskConnector" when {

    "authHeaders" must {
      "return correct Base64 encoded Authorization header" in new Test {
        val expected: String = Base64.getEncoder.encodeToString(s"$username:$token".getBytes)
        connector.authHeaders() shouldBe Seq("Authorization" -> s"Basic $expected")
      }
    }

    "200 with valid JSON" must {
      "return Right with parsed response" in new Test {
        stub(Some(successJson.toString), Status.OK)
        await(connector.getRiskInsights(request)) shouldBe Right(expectedResponse)
      }
    }

    "200 with malformed JSON" must {
      "return Left" in new Test {
        stub(Some(malformedJson.toString), Status.OK)
        await(connector.getRiskInsights(request)).isLeft shouldBe true
      }
    }

    "400" must {
      "return Left" in new Test {
        stub(None, Status.BAD_REQUEST)
        await(connector.getRiskInsights(request)) shouldBe Left("Unexpected status 400 from cip-risk")
      }
    }

    "404" must {
      "return Left" in new Test {
        stub(None, Status.NOT_FOUND)
        await(connector.getRiskInsights(request)) shouldBe Left("Unexpected status 404 from cip-risk")
      }
    }

    "500" must {
      "return Left" in new Test {
        stub(None, Status.INTERNAL_SERVER_ERROR)
        await(connector.getRiskInsights(request)) shouldBe Left("Unexpected status 500 from cip-risk")
      }
    }
  }
}