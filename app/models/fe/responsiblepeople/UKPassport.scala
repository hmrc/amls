package models.fe.responsiblepeople

import play.api.libs.json._

sealed trait UKPassport extends PassportType

case class UKPassportYes(ukPassportNumber: String) extends UKPassport

case object UKPassportNo extends UKPassport

object UKPassport {

  implicit val jsonReads: Reads[UKPassport] =
    (__ \ "ukPassport").read[Boolean] flatMap {
      case true => (__ \ "ukPassportNumber").read[String] map UKPassportYes.apply
      case false => Reads(_ => JsSuccess(UKPassportNo))
    }

  implicit val jsonWrites = Writes[UKPassport] {
    case UKPassportYes(value) => Json.obj(
      "ukPassport" -> true,
      "ukPassportNumber" -> value
    )
    case UKPassportNo => Json.obj("ukPassport" -> false)
  }

}
