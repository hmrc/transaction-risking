//package uk.gov.hmrc.transactionrisking.models.response
//
//import play.api.libs.json.{Json, OFormat}
//
//
//case class RDSResponse(
//                            riskScore: Double,
//                            riskCorrelationId: String,
//                            reasons: Seq[String]
//                      )
//
//object RDSResponse:
//  implicit val reads: OFormat[RDSResponse] = Json.format[RDSResponse]






//package uk.gov.hmrc.transactionrisking.models.response.rds
//
//import play.api.libs.json.{Json, OFormat}
//
//case class ErrorMessage(error: String)
//object ErrorMessage:
//  implicit val format: OFormat[ErrorMessage] = Json.format[ErrorMessage]
//
//case class ErrorMessages(
//                          timestamp: String,
//                          status: Int,
//                          error: String,
//                          path: String,
//                          errors: Seq[ErrorMessage] = Seq.empty
//                        )
//object ErrorMessages:
//  implicit val format: OFormat[ErrorMessages] = Json.format[ErrorMessages]



package uk.gov.hmrc.transactionrisking.models.response.rsd

case class RsdResponse()