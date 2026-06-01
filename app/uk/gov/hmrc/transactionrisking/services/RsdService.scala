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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.transactionrisking.connectors.RsdConnector
import uk.gov.hmrc.transactionrisking.models.errors.ErrorWrapper
import uk.gov.hmrc.transactionrisking.models.request.rsd.RsdRequest
import uk.gov.hmrc.transactionrisking.models.response.rsd.RsdResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RsdService @Inject()(rsdConnector: RsdConnector)(implicit ec: ExecutionContext) {

  def submit(request: RsdRequest)(implicit hc: HeaderCarrier, correlationId: String): Future[Either[ErrorWrapper,RsdResponse]] =
    rsdConnector.submit(request)
}