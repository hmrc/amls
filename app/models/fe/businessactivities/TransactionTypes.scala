/*
 * Copyright 2017 HM Revenue & Customs
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

package models.fe.businessactivities

import models.des.businessactivities.{AuditableRecordsDetails, BusinessActivitiesAll}
import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait TransactionType {
  val value: String =
    this match {
      case Paper => "01"
      case DigitalSpreadsheet => "02"
      case DigitalSoftware(_) => "03"
    }
}

case object Paper extends TransactionType

case object DigitalSpreadsheet extends TransactionType

case class DigitalSoftware(name: String) extends TransactionType

case class TransactionTypes(types: Set[TransactionType])

object TransactionTypes {

  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  implicit val typesReader: Reads[Set[TransactionType]] =
    (__ \ "types").read[Set[String]].flatMap { x: Set[String] =>
      x.map {
        case "01" => Reads(_ => JsSuccess(Paper)) map identity[TransactionType]
        case "02" => Reads(_ => JsSuccess(DigitalSpreadsheet)) map identity[TransactionType]
        case "03" =>
          (JsPath \ "software").read[String].map(DigitalSoftware.apply _) map identity[TransactionType]
        case _ =>
          Reads(_ => JsError((JsPath \ "transactions") -> ValidationError("error.invalid")))
      }.foldLeft[Reads[Set[TransactionType]]](
        Reads[Set[TransactionType]](_ => JsSuccess(Set.empty))
      ) {
        (result, data) =>
          data flatMap { m =>
            result.map { n =>
              n + m
            }
          }
      }
    }

  implicit val jsonWrites = Writes[TransactionTypes] { t =>
    val softwareName = t.types.collectFirst {
      case DigitalSoftware(name) => Json.obj("software" -> name)
    }.getOrElse(Json.obj())

    Json.obj("types" -> t.types.map(_.value)) ++ softwareName
  }

  implicit val jsonReads: Reads[TransactionTypes] = __.read[Set[TransactionType]] map TransactionTypes.apply

  def convertRecordsKept(ba: BusinessActivitiesAll): Option[Boolean] = {
    ba.auditableRecordsDetails.detailedRecordsKept match {
      case "Yes" => Some(true)
      case "No" => Some(false)
    }
  }

  implicit def convert(details: AuditableRecordsDetails): Option[TransactionTypes] = {
    details.transactionRecordingMethod map { method =>
      val typeMap = Seq[(Boolean, TransactionType)](
        method.manual -> Paper,
        method.spreadsheet -> DigitalSpreadsheet,
        method.commercialPackage -> DigitalSoftware(method.commercialPackageName.getOrElse(""))
      )

      TransactionTypes((typeMap collect {
        case (true, t) => t
      }).toSet)
    }
  }
}
