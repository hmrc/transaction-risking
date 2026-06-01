//package uk.gov.hmrc.transactionrisking.models.request
//
//import play.api.libs.json.{Json, OWrites}
//
//case class RdsRequest (
//                        serviceRegime: String,
//                        eventName: String,
//                        feedbackId: String,
//                        eventTimestamp: String,
//                        metadata: List[Metadata],
//                        payload: Payload
//                      )
//
//object RdsRequest:
//  implicit val writes: OWrites[RdsRequest] = Json.writes[RdsRequest]
//
//
//
//
//case class Metadata(
//                     vrn: String,
//                     start: String,
//                     end: String,
//                     additionalProperties: AdditionalProperties
//                   )
//
//object Metadata:
//  implicit val writes: OWrites[Metadata] = Json.writes[Metadata]
//
//
//
//
//
//
//case class AdditionalProperties(
//                                 presentedDateTime: String
//                               )
//
//object AdditionalProperties:
//  implicit val writes: OWrites[AdditionalProperties] = Json.writes[AdditionalProperties]
//
//
//
//
//
//case class Payload(
//                    reportId: String
//                  )
//
//object Payload:
//  implicit val writes: OWrites[Payload] = Json.writes[Payload]
//


package uk.gov.hmrc.transactionrisking.models.request.rsd

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.transactionrisking.models.request.Rsd.RsdRequestPayload

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
