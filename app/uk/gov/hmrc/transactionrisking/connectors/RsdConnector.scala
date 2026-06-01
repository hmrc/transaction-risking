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
import play.api.http.Status.NO_CONTENT
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.Json
import play.api.libs.ws.*
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, HttpResponse, StringContextOps}
import uk.gov.hmrc.transactionrisking.config.AppConfig
import uk.gov.hmrc.transactionrisking.models.errors.ErrorWrapper
import uk.gov.hmrc.transactionrisking.models.request.rsd.RsdRequest
import uk.gov.hmrc.transactionrisking.models.response.rsd.RsdResponse
import uk.gov.hmrc.transactionrisking.services.RsdService
import uk.gov.hmrc.transactionrisking.models.errors.*

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RsdConnector @Inject() (val httpClient: HttpClientV2, appConfig: AppConfig)(implicit val ec: ExecutionContext) extends Logging {

  private lazy val url: String = appConfig.rsdBaseUrl

  def submit(rsdRequest: RsdRequest)(implicit hc: HeaderCarrier, correlationId: String): Future[Either[ErrorWrapper,RsdResponse]] = {

    logger.info(s"$correlationId::[RsdConnector:submit] submitting store interaction for action ${rsdRequest.eventName}")

    httpClient
      .post(url"$url")
      .setHeader(Seq(
        "Environment"            -> appConfig.rsdEnv,
        "CorrelationId"          -> correlationId,
        HeaderNames.CONTENT_TYPE -> s"${MimeTypes.JSON};charset=UTF-8",
        "accept"                 -> "*/*",
        "Authorization"          -> s"Bearer ${appConfig.rsdToken}"
      )*)
      .withBody(Json.toJson(rsdRequest))
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case NO_CONTENT =>
            logger.info(s"$correlationId::[RsdConnector:submit]  ${rsdRequest.eventName} interaction stored successfully")
            Right(RsdResponse())
          case unexpectedStatus @ _ =>
            logger.error(
              s"$correlationId::[RsdConnector:submit]Unable to submit the report due to unexpected status code returned $unexpectedStatus with body: ${response.body}")
            Left(ErrorWrapper(correlationId, InternalError))
        }
      }
      .recover { case e: HttpException =>
        logger.error(s"$correlationId::[RsdConnector:submit] Rsd response : failed with exception", e)
        Left(ErrorWrapper(correlationId, InternalError))
      }
  }

}

