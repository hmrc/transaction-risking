package uk.gov.hmrc.transactionrisking.models.request.rsd

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.transactionrisking.models.request.rsd.RsdRequestPayload

import java.time.OffsetDateTime

case class RsdRequest(
                      serviceRegime: String,
                      eventName: String,
                      eventTimestamp: OffsetDateTime,
                      feedbackId: String,
                      metaData: List[Map[String, String]],
                      payload: Option[Messages]
                    )

case class Messages(messages: Option[Seq[RsdRequestPayload]])

object RsdRequest {
  implicit val messageFormats: Format[Messages] = Json.format[Messages]
  implicit val formats: Format[RsdRequest]       = Json.format[RsdRequest]
}
