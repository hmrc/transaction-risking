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

package uk.gov.hmrc.transactionrisking.stubs

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.*
import play.api.libs.json.Json

object StrRiskStub:

  private val cipRiskUrl = "/str/risk/insights"

  def successResponse(vrn: String): StubMapping =
    stubFor(
      post(urlEqualTo(cipRiskUrl))
        .withRequestBody(equalToJson(
          Json.obj("vatRegistrationNumber" -> vrn).toString(),
          true,
          false
        ))
        .willReturn(
          aResponse()
            .withStatus(OK)
            .withHeader("Content-Type", "application/json")
            .withBody(CommonTestData.simpleStrRiskResponseJson.toString())
        )
    )

  def serverErrorResponse(): StubMapping =
    stubFor(
      post(urlEqualTo(cipRiskUrl))
        .willReturn(
          aResponse()
            .withStatus(INTERNAL_SERVER_ERROR)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"reason": "Internal server error"}""")
        )
    )

  def serviceUnavailableResponse(): StubMapping =
    stubFor(
      post(urlEqualTo(cipRiskUrl))
        .willReturn(
          aResponse()
            .withStatus(SERVICE_UNAVAILABLE)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"reason": "Service unavailable"}""")
        )
    )

  def malformedJsonResponse(): StubMapping =
    stubFor(
      post(urlEqualTo(cipRiskUrl))
        .willReturn(
          aResponse()
            .withStatus(OK)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"unexpected": "shape"}""")
        )
    )