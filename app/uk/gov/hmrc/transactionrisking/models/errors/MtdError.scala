package uk.gov.hmrc.transactionrisking.models.errors

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsObject, JsPath, Json, OWrites}

case class MtdError(code: String, message: String, httpStatus: Int, paths: Option[Seq[String]] = None) {
  val asJson: JsObject = Json.toJson(this).as[JsObject]
}

object MtdError {

  implicit val writes: OWrites[MtdError] = (
    (JsPath \ "code").write[String] and
      (JsPath \ "message").write[String] and
      (JsPath \ "paths").writeNullable[Seq[String]]
    )(unlift(MtdError.unapply))

  // excludes httpStatus
  def unapply(e: MtdError): Option[(String, String, Option[Seq[String]])] = Some((e.code, e.message, e.paths))

  implicit def genericWrites[T <: MtdError]: OWrites[T] =
    writes.contramap[T](c => c: MtdError)

}
