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

package uk.gov.hmrc.transactionrisking.controllers

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.ws.{WSRequest, WSResponse, writeableOf_String}
import play.api.test.Helpers.{AUTHORIZATION, await, defaultAwaitTimeout}
import uk.gov.hmrc.transactionrisking.stubs.{CommonTestData, StrRiskStub}
import uk.gov.hmrc.transactionrisking.support.IntegrationBaseSpec


class GenerateFeedbackControllerISpec extends IntegrationBaseSpec:

  "GenerateFeedbackController" when :

    "POST /feedback/:vrn" should :

      "return 200 with risk response and correlation ID header" when :
        "a valid VRN is provided and cip-risk responds successfully" in new Test:
          override def setupStubs(): StubMapping = StrRiskStub.successResponse(vrn)

          val response: WSResponse = await(request().post(""))

          response.status shouldBe OK
          response.header("X-CorrelationId") shouldBe defined
          response.header("Content-Type") shouldBe Some("application/json")

      "return 500" when :
        "cip-risk returns 500" in new Test:
          override def setupStubs(): StubMapping = StrRiskStub.serverErrorResponse()

          val response: WSResponse = await(request().post(""))
          response.status shouldBe INTERNAL_SERVER_ERROR

        "cip-risk returns 503" in new Test:
          override def setupStubs(): StubMapping = StrRiskStub.serviceUnavailableResponse()

          val response: WSResponse = await(request().post(""))
          response.status shouldBe INTERNAL_SERVER_ERROR

        "cip-risk returns malformed JSON" in new Test:
          override def setupStubs(): StubMapping = StrRiskStub.malformedJsonResponse()

          val response: WSResponse = await(request().post(""))
          response.status shouldBe INTERNAL_SERVER_ERROR

  private trait Test:

    def vrn: String = CommonTestData.simpleVrn

    def setupStubs(): StubMapping

    def request(): WSRequest =
      setupStubs()
      buildRequest(s"/feedback/$vrn")
        .withHttpHeaders(AUTHORIZATION -> "Bearer test-token")