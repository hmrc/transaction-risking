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

package uk.gov.hmrc.transactionrisking.services

import cats.data.EitherT
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.transactionrisking.connectors.InsightsConnector
import uk.gov.hmrc.transactionrisking.models.request.InsightsRequest
import uk.gov.hmrc.transactionrisking.models.response.{Insights, InsightsResponse, StrategicRisk}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

class InsightsServiceSpec extends AnyWordSpec with Matchers with MockitoSugar:

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val correlationId: String = "test-correlation-id"

  private val validVatNumber = "GB123456789"
  private val invalidVatNumber = "INVALID"

  private val insightsRequest = InsightsRequest(vatRegistrationNumber = validVatNumber)
  private val invalidInsightsRiskRequest = InsightsRequest(vatRegistrationNumber = invalidVatNumber)

  private val insightsResponse: InsightsResponse = {
    InsightsResponse(
      Insights(
        StrategicRisk(
          riskScore = 12.46,
          riskCorrelationId = "123e4567-e89b-12d3-a456-426614174000",
        )
      )
    )
  }

  private def await[T](f: Future[T]): T = Await.result(f, 5.seconds)

  private def rightT(response: InsightsResponse): EitherT[Future, String, InsightsResponse] = EitherT.rightT(response)

  private def leftT(error: String): EitherT[Future, String, InsightsResponse] = EitherT.leftT(error)

  class Test:
    val mockConnector: InsightsConnector = mock[InsightsConnector]
    val service = new InsightsService(mockConnector)

  "StrRiskService" when :

    "assess is called with a valid VAT number" must :
      "return the risk response" in new Test:
        when(mockConnector.getRiskInsights(eqTo(insightsRequest))(any(), any()))
          .thenReturn(rightT(insightsResponse))

        val result: Either[String, InsightsResponse] = await(service.assess(insightsRequest).value)
        result shouldBe Right(insightsResponse)

    "assess is called and the connector returns a JSON validation error" must :
      "return a Left with the error message" in new Test:
        when(mockConnector.getRiskInsights(eqTo(insightsRequest))(any(), any()))
          .thenReturn(leftT("JSON validation failed"))

        val result: Either[String, InsightsResponse] = await(service.assess(insightsRequest).value)
        result shouldBe Left("JSON validation failed")

    "assess is called and the connector returns an unexpected status" must :
      "return a Left with the error message" in new Test:
        when(mockConnector.getRiskInsights(eqTo(invalidInsightsRiskRequest))(any(), any()))
          .thenReturn(leftT("Unexpected status 400 from cip-risk"))

        val result: Either[String, InsightsResponse] = await(service.assess(invalidInsightsRiskRequest).value)
        result shouldBe Left("Unexpected status 400 from cip-risk")

    "assess is called and the connector throws an exception" must :
      "return a Left with the exception message" in new Test:
        when(mockConnector.getRiskInsights(eqTo(insightsRequest))(any(), any()))
          .thenReturn(leftT("Exception calling cip-risk: connection refused"))

        val result: Either[String, InsightsResponse] = await(service.assess(insightsRequest).value)
        result shouldBe Left("Exception calling cip-risk: connection refused")