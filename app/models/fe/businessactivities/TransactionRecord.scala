/*
 * Copyright 2016 HM Revenue & Customs
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

import models.des.businessactivities.{BusinessActivitiesAll, TransactionRecordingMethod}
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.json.Reads.StringReads
import utils.CommonMethods

sealed trait TransactionRecord

case class TransactionRecordYes(transactionType: Set[TransactionType]) extends TransactionRecord

case object TransactionRecordNo extends TransactionRecord


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

object TransactionRecord {

  import utils.MappingUtils.Implicits._

  implicit val jsonReads: Reads[TransactionRecord] =
    (__ \ "isRecorded").read[Boolean] flatMap {
      case true => (__ \ "transactions").read[Set[String]].flatMap {x:Set[String] =>
        x.map {
            case "01" => Reads(_ => JsSuccess(Paper)) map identity[TransactionType]
            case "02" => Reads(_ => JsSuccess(DigitalSpreadsheet)) map identity[TransactionType]
            case "03" =>
              (JsPath \ "digitalSoftwareName").read[String].map (DigitalSoftware.apply  _) map identity[TransactionType]
            case _ =>
              Reads(_ => JsError((JsPath \ "transactions") -> ValidationError("error.invalid")))
          }.foldLeft[Reads[Set[TransactionType]]](
            Reads[Set[TransactionType]](_ => JsSuccess(Set.empty))
         ){
          (result, data) =>
            data flatMap {m =>
             result.map {n =>
               n + m
             }
           }
        }
      } map TransactionRecordYes.apply
      case false => Reads(_ => JsSuccess(TransactionRecordNo))
    }

  implicit val jsonWrite = Writes[TransactionRecord] {
    case TransactionRecordNo => Json.obj("isRecorded" -> false)
    case TransactionRecordYes(transactions) =>
      Json.obj(
        "isRecorded" -> true,
        "transactions" -> (transactions map {
          _.value
        }).toSeq
      ) ++ transactions.foldLeft[JsObject](Json.obj()) {
        case (m, DigitalSoftware(name)) =>
          m ++ Json.obj("digitalSoftwareName" -> name)
        case (m, _) =>
          m
      }
  }

  implicit def conv(businessActivitiesAll: BusinessActivitiesAll) : Option[TransactionRecord] = {
    businessActivitiesAll.auditableRecordsDetails.transactionRecordingMethod match {
      case Some(data) => Some(TransactionRecordYes(data))
      case None => Some(TransactionRecordNo)
    }
  }

  def getPackageName(commercialPackage: Boolean, commercialPackageName: String): Option[TransactionType] = {
    commercialPackage match {
      case true => Some(DigitalSoftware(commercialPackageName))
      case false => None
    }
  }

  implicit def convTranRecord(method:TransactionRecordingMethod) : Set[TransactionType] = {
    Set(CommonMethods.getSpecificType[TransactionType](method.manual, Paper),
      CommonMethods.getSpecificType[TransactionType](method.spreadsheet, DigitalSpreadsheet),
    getPackageName(method.commercialPackage, method.commercialPackageName.getOrElse(""))).flatten
  }
}
