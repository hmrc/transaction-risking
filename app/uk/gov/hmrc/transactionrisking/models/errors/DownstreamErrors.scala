package uk.gov.hmrc.transactionrisking.models.errors

import play.api.libs.json.{Json, Reads}

case class DownstreamErrorCode(code: String)

object DownstreamErrorCode {
  implicit val reads: Reads[DownstreamErrorCode] = Json.reads[DownstreamErrorCode]
}

sealed trait DownstreamError

case class DownstreamErrors(errors: Seq[DownstreamErrorCode]) extends DownstreamError

object DownstreamErrors {
  def single(error: DownstreamErrorCode): DownstreamErrors = DownstreamErrors(List(error))
}

case class OutboundError(error: MtdError, errors: Option[Seq[MtdError]] = None) extends DownstreamError