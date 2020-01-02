/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.fe.supervision

import models.des.supervision.SupervisionDetails
import org.joda.time.LocalDate
import play.api.libs.json.{Json, Reads, Writes}
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

sealed trait AnotherBody

case class AnotherBodyYes(supervisorName: String,
                          startDate: LocalDate,
                          endDate: LocalDate,
                          endingReason: String) extends AnotherBody

case object AnotherBodyNo extends AnotherBody


object AnotherBody {

  import utils.MappingUtils.Implicits._

  implicit val jsonReads: Reads[AnotherBody] = {

    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    import play.api.libs.json.JodaReads.DefaultJodaLocalDateReads

    (__ \ "anotherBody").read[Boolean] flatMap {
      case true =>
        (
          (__ \ "supervisorName").read[String] ~
            (__ \ "startDate" \ "supervisionStartDate").read[LocalDate] ~
            (__ \ "endDate" \ "supervisionEndDate").read[LocalDate] ~
            (__ \ "endingReason" \ "supervisionEndingReason").read[String]) (AnotherBodyYes.apply _) map identity[AnotherBody]

      case false => AnotherBodyNo
    }
  }

  implicit val jsonWrites = Writes[AnotherBody] {
    case a : AnotherBodyYes => Json.obj(
      "anotherBody" -> true,
      "supervisorName" -> a.supervisorName,
      "startDate" -> Json.obj("supervisionStartDate" -> a.startDate),
      "endDate" -> Json.obj("supervisionEndDate" -> a.endDate),
      "endingReason" -> Json.obj("supervisionEndingReason" -> a.endingReason)
    )
    case AnotherBodyNo => Json.obj("anotherBody" -> false)
  }

  implicit def conv(supDtls: Option[SupervisionDetails] ): Option[AnotherBody] = {
    supDtls match {
      case Some(sup) => sup.supervisorDetails.fold[Option[AnotherBody]](Some(AnotherBodyNo))(x => Some(AnotherBodyYes(x.nameOfLastSupervisor,
        LocalDate.parse(x.supervisionStartDate),
        LocalDate.parse(x.supervisionEndDate),
        x.supervisionEndingReason)))
      case None => None
    }
  }

}
