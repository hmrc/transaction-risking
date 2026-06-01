package uk.gov.hmrc.transactionrisking.models.response.rsd

sealed trait RsdFailure {}

case object RsdFailure {
  case class ErrorResponse(status: Int) extends RsdFailure {}
  case class Exception(reason: String)  extends RsdFailure {}
}
