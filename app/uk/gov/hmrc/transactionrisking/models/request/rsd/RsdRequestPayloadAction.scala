package uk.gov.hmrc.transactionrisking.models.request.Rsd

import play.api.libs.json.{Format, Json}

case class RsdRequestPayloadAction(
                                   title: String,
                                   message: String,
                                   action: String,
                                   path: String,
                                   links: Option[Seq[RsdRequestPayloadActionLinks]]
                                 ) {}

object RsdRequestPayloadAction {
  implicit val formats: Format[RsdRequestPayloadAction] = Json.format[RsdRequestPayloadAction]
}