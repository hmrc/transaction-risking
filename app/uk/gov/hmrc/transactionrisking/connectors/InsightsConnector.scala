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

import cats.data.EitherT
import play.api.Logging
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.transactionrisking.config.AppConfig
import uk.gov.hmrc.transactionrisking.models.request.InsightsRequest
import uk.gov.hmrc.transactionrisking.models.response.InsightsResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InsightsConnector @Inject()(
                                   val httpClient: HttpClientV2,
                                   appConfig: AppConfig
                                 )(implicit val ec: ExecutionContext) extends Logging:

  private[connectors] def requiredHeaders(correlationId: String, appName: String): Seq[(String, String)] =
    Seq(
      "User-Agent" -> appName,
      "Content-Type" -> "application/json",
      "X-Correlation-Id" -> correlationId
    )

  def getRiskInsights(
                       request: InsightsRequest
                     )(implicit hc: HeaderCarrier, correlationId: String): EitherT[Future, String, InsightsResponse] =

    logger.info(s"$correlationId::[InsightsConnector:getRiskInsights] calling insights API")

    EitherT(
      httpClient
        .post(url"${appConfig.cipRiskServiceBaseUrl}")
        .withBody(Json.toJson(request))
        .setHeader(requiredHeaders(correlationId, appConfig.appName) *)
        .execute[Either[UpstreamErrorResponse, InsightsResponse]]
        .map {
          case Right(response) =>
            logger.info(s"$correlationId::[InsightsConnector:getRiskInsights] success")
            Right(response)

          case Left(errorResponse) =>
            logger.error(s"$correlationId::[InsightsConnector:getRiskInsights] failed status ${errorResponse.statusCode}: ${errorResponse.message}")
            Left(s"Unexpected status ${errorResponse.statusCode} from insights service")
        }
        .recover { case ex =>
          logger.error(s"$correlationId::[InsightsConnector:getRiskInsights] unexpected exception", ex)
          Left(s"Exception calling insights service: ${ex.getMessage}")
        }
    )