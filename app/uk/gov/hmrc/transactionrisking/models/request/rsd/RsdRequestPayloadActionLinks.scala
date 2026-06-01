package uk.gov.hmrc.transactionrisking.models.request.rsd

import play.api.libs.json.{Format, Json}


case class RsdRequestPayloadActionLinks(linkTitle: String, linkUrl: String) {}

object RsdRequestPayloadActionLinks {
  implicit val formats: Format[RsdRequestPayloadActionLinks] = Json.format[RsdRequestPayloadActionLinks]
}