package models.fe.responsiblepeople

import play.api.libs.json._

sealed trait NonUKPassport extends PassportType

case class NonUKPassportYes(nonUKPassportNumber: String) extends NonUKPassport

case object NoPassport extends NonUKPassport

object NonUKPassport {

  implicit val jsonReads: Reads[NonUKPassport] =
    (__ \ "nonUKPassport").read[Boolean] flatMap {
      case true => (__ \ "nonUKPassportNumber").read[String] map NonUKPassportYes.apply
      case false => Reads(_ => JsSuccess(NoPassport))
    }

  implicit val jsonWrites = Writes[NonUKPassport] {
    case NonUKPassportYes(value) => Json.obj(
      "nonUKPassport" -> true,
      "nonUKPassportNumber" -> value
    )
    case NoPassport => Json.obj("nonUKPassport" -> false)
  }

}
