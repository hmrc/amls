package models.fe

import org.joda.time.LocalDate
import play.api.libs.json._

case class DateOfChange (dateOfChange: LocalDate)

object DateOfChange {


  implicit val reads: Reads[DateOfChange] =
    __.read[LocalDate] map {
      DateOfChange(_)
    }

  implicit val writes = Writes[DateOfChange] {
    case DateOfChange(b) => Json.toJson(b)
  }
}


