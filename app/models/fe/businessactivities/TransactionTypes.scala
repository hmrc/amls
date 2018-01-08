/*
 * Copyright 2018 HM Revenue & Customs
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
  import play.api.data.validation.{ValidationError => VE}

  implicit val typesReader = new Reads[Set[TransactionType]] {
    override def reads(json: JsValue) = {
      val t = (json \ "types").asOpt[Set[String]]
      val n = (json \ "software").asOpt[String]
      val validValues = Set("01", "02", "03")

      (t, n) match {
        case (None, _) => JsError(__ \ "types" -> VE("error.missing"))
        case (Some(types), None) if types.contains("03") => JsError(__ \ "software" -> VE("error.missing"))
        case (Some(types), _) if types.diff(validValues).nonEmpty => JsError(__ \ "types" -> VE("error.invalid"))
        case (Some(types), maybeName) => JsSuccess(types map {
          case "01" => Paper
          case "02" => DigitalSpreadsheet
          case "03" => DigitalSoftware(maybeName.getOrElse(""))
        })
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
