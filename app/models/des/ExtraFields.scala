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

package models.des

import config.AmlsConfig
import models.des.aboutyou.{AboutYouRelease7, Aboutyou}
import play.api.libs.json._

case class ExtraFields(declaration: Declaration,
                       filingIndividual: AboutYouRelease7,
                       etmpFields: Option[EtmpFields]) {

  def setEtmpFields(viewEtmpFields: Option[EtmpFields]) : ExtraFields = {
    this.copy(etmpFields = viewEtmpFields)
  }
}

object ExtraFields {
  implicit def format = if (AmlsConfig.release7) {
    Json.format[ExtraFields]
  } else {
    val reads: Reads[ExtraFields] = {
      import play.api.libs.functional.syntax._
      import play.api.libs.json.Reads._
      import play.api.libs.json._

      (
        (__ \ "declaration").read[Declaration] and
          (__ \ "filingIndividual").read[Aboutyou].map{x:Aboutyou => AboutYouRelease7.convertToRelease7(x)} and
          (__ \ "etmpFields").readNullable[EtmpFields]
        ) (ExtraFields.apply _)
    }

    val aboutYouWrites = new Writes[AboutYouRelease7] {
      override def writes(o: AboutYouRelease7): JsValue = {
        Aboutyou.format.writes(Aboutyou.convertFromRelease7(o))
      }
    }

    val writes: Writes[ExtraFields] = {
      import play.api.libs.functional.syntax._
      import play.api.libs.json._

      (

          (__ \ "declaration").write[Declaration] and
          __.write(aboutYouWrites) and
          (__ \ "etmpFields").writeNullable[EtmpFields]
        ) (unlift(ExtraFields.unapply _))
    }

    Format(reads, writes)
  }

  implicit def convert(person: models.fe.declaration.AddPerson): ExtraFields = {
    ExtraFields(Declaration(true), AboutYouRelease7.convert(person), None)
  }
}
