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

import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.transactionrisking.config.AppConfig
import uk.gov.hmrc.transactionrisking.models.request.StrRiskRequest
import uk.gov.hmrc.transactionrisking.models.response.StrRiskResponse

import java.util.Base64
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StrRiskConnector @Inject()(
                                  val httpClient: HttpClientV2,
                                  appConfig: AppConfig
                                )(implicit val ec: ExecutionContext) extends Logging {

  private[connectors] def authHeaders(): Seq[(String, String)] = {
    val encoded = Base64.getEncoder.encodeToString(
      s"${appConfig.cipRiskUsername}:${appConfig.cipRiskToken}".getBytes
    )
    Seq("Authorization" -> s"Basic $encoded")
  }

  def getRiskInsights(
                       request: StrRiskRequest
                     )(implicit hc: HeaderCarrier, correlationId: String): Future[Either[String, StrRiskResponse]] = {

    logger.info(s"$correlationId::[StrRiskConnector:getRiskInsights] calling STR risk API")

    httpClient
      .post(url"${appConfig.cipRiskServiceBaseUrl}")
      .withBody(Json.toJson(request))
      .setHeader(authHeaders() *)
      .execute[HttpResponse]
      .map { response =>
        logger.info(s"$correlationId::[StrRiskConnector:getRiskInsights] response status ${response.status}")
        response.status match {
          case OK =>
            response.json
              .validate[StrRiskResponse]
              .fold(
                e => {
                  logger.error(s"$correlationId::[StrRiskConnector:getRiskInsights] JSON validation failed: $e")
                  Left(s"JSON validation failed: $e")
                },
                report => Right(report)
              )
          case status =>
            logger.error(s"$correlationId::[StrRiskConnector:getRiskInsights] unexpected status $status body: ${response.body}")
            Left(s"Unexpected status $status from cip-risk")
        }
      }
      .recover { case ex =>
        logger.error(s"$correlationId::[StrRiskConnector:getRiskInsights] unexpected exception", ex)
        Left(s"Exception calling cip-risk: ${ex.getMessage}")
      }
  }
}