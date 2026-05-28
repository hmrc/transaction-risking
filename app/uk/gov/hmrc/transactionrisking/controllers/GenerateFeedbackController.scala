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

import play.api.libs.json.Json
import play.api.mvc.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.transactionrisking.models.request.InsightsRequest
import uk.gov.hmrc.transactionrisking.models.response.InsightsResponse
import uk.gov.hmrc.transactionrisking.services.InsightsService
import uk.gov.hmrc.transactionrisking.utils.IdGenerator

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class GenerateFeedbackController @Inject()(
                                            cc: ControllerComponents,
                                            insightsService: InsightsService,
                                            idGenerator: IdGenerator
                                          )(implicit ec: ExecutionContext)
  extends BackendController(cc) {

  def generateFeedback(vrn: String): Action[AnyContent] = Action.async { implicit request =>
    implicit val correlationId: String = idGenerator.generateId()
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val pipeline = for
      riskResponse <- insightsService.assess(InsightsRequest(vrn))
      
    // nextResponse <- nextService.call(riskResponse.riskScore)

    yield riskResponse

    pipeline.value.map {
      case Right(response: InsightsResponse) =>
        Ok(Json.toJson(response))
          .withHeaders("X-CorrelationId" -> correlationId)

      case Left(error: String) =>
        InternalServerError(Json.obj("message" -> error))
          .withHeaders("X-CorrelationId" -> correlationId)
    }
  }

}