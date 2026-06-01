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