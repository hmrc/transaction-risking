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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.transactionrisking.models.request.StrRiskRequest
import uk.gov.hmrc.transactionrisking.models.response.StrRiskResponse

object CommonTestData:

  val simpleVrn: String           = "123456789"
  val invalidVrn: String          = "INVALID"
  val simpleCorrelationId: String = "test-correlation-id"

  val simpleStrRiskRequest: StrRiskRequest = StrRiskRequest(vatRegistrationNumber = simpleVrn)

  val simpleStrRiskResponse: StrRiskResponse = StrRiskResponse(
    riskScore         = 12.33,
    riskCorrelationId = simpleCorrelationId,
    reasons           = Seq(s"$simpleCorrelationId is 3 hops away from risky")
  )

  val simpleStrRiskResponseJson: JsValue = Json.toJson(simpleStrRiskResponse)