package uk.gov.hmrc.transactionrisking.models.errors


import play.api.libs.json.{Json, Writes}

case class ErrorWrapper(correlationId: String, error: MtdError, errors: Option[Seq[MtdError]] = None) {

  private def allErrors: Seq[MtdError] = errors match {
    case Some(seq) => seq
    case None      => Seq(error)
  }

  def auditErrors: Seq[String] =
    allErrors.map(error => String(error.code))

}

object ErrorWrapper {

  implicit val writes: Writes[ErrorWrapper] = (errorResponse: ErrorWrapper) => {

    val json = errorResponse.error.asJson

    errorResponse.errors match {
      case Some(errors) if errors.nonEmpty => json + ("errors" -> Json.toJson(errors))
      case _                               => json
    }

  }

}
