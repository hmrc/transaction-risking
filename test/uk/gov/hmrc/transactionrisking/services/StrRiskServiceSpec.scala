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
import uk.gov.hmrc.transactionrisking.connectors.StrRiskConnector
import uk.gov.hmrc.transactionrisking.models.request.StrRiskRequest
import uk.gov.hmrc.transactionrisking.models.response.StrRiskResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

class StrRiskServiceSpec extends AnyWordSpec with Matchers with MockitoSugar:

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val correlationId: String = "test-correlation-id"
  
  private val validVatNumber = "GB123456789"
  private val invalidVatNumber = "INVALID"

  private val strRiskRequest = StrRiskRequest(vatRegistrationNumber = validVatNumber)
  private val invalidStrRiskRequest = StrRiskRequest(vatRegistrationNumber = invalidVatNumber)

  private val strRiskResponse = StrRiskResponse(
    riskScore = 12.46,
    riskCorrelationId = "123e4567-e89b-12d3-a456-426614174000",
    reasons = Seq("VRN 0128925978251 is 3 hops from something risky.")
  )
  
  private def await[T](f: Future[T]): T = Await.result(f, 5.seconds)

  private def rightT(response: StrRiskResponse): EitherT[Future, String, StrRiskResponse] = EitherT.rightT(response)

  private def leftT(error: String): EitherT[Future, String, StrRiskResponse] = EitherT.leftT(error)

  class Test:
    val mockConnector: StrRiskConnector = mock[StrRiskConnector]
    val service = new StrRiskService(mockConnector)

  "StrRiskService" when :

    "assess is called with a valid VAT number" must :
      "return the risk response" in new Test:
        when(mockConnector.getRiskInsights(eqTo(strRiskRequest))(any(), any()))
          .thenReturn(rightT(strRiskResponse))

        val result: Either[String, StrRiskResponse] = await(service.assess(strRiskRequest).value)
        result shouldBe Right(strRiskResponse)

    "assess is called and the connector returns a JSON validation error" must :
      "return a Left with the error message" in new Test:
        when(mockConnector.getRiskInsights(eqTo(strRiskRequest))(any(), any()))
          .thenReturn(leftT("JSON validation failed"))

        val result: Either[String, StrRiskResponse] = await(service.assess(strRiskRequest).value)
        result shouldBe Left("JSON validation failed")

    "assess is called and the connector returns an unexpected status" must :
      "return a Left with the error message" in new Test:
        when(mockConnector.getRiskInsights(eqTo(invalidStrRiskRequest))(any(), any()))
          .thenReturn(leftT("Unexpected status 400 from cip-risk"))

        val result: Either[String, StrRiskResponse] = await(service.assess(invalidStrRiskRequest).value)
        result shouldBe Left("Unexpected status 400 from cip-risk")

    "assess is called and the connector throws an exception" must :
      "return a Left with the exception message" in new Test:
        when(mockConnector.getRiskInsights(eqTo(strRiskRequest))(any(), any()))
          .thenReturn(leftT("Exception calling cip-risk: connection refused"))

        val result: Either[String, StrRiskResponse] = await(service.assess(strRiskRequest).value)
        result shouldBe Left("Exception calling cip-risk: connection refused")