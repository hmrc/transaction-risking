package uk.gov.hmrc.transactionrisking.models.request.rsd

import play.api.libs.json.{Format, Json}


case class RsdRequestPayload(
                             messageId: String,
                             englishAction: RsdRequestPayloadAction,
                             welshAction: RsdRequestPayloadAction
                           )

object RsdRequestPayload {
  implicit val formats: Format[RsdRequestPayload] = Json.format[RsdRequestPayload]
}