/*
 * Copyright 2019 HM Revenue & Customs
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

package models.fe.amp

import java.time.LocalDateTime

import play.api.libs.json._

final case class Amp(_id: String,
                     data: AmpData,
                     lastUpdated: LocalDateTime,
                     hasChanged: Boolean = false,
                     hasAccepted: Boolean = false) {
}

object Amp  {

  implicit lazy val reads: Reads[Amp] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[AmpData] and
        (__ \ "lastUpdated").read(MongoDateTimeFormats.localDateTimeRead) and
        (__ \ "hasChanged").readNullable[Boolean].map(_.getOrElse(false)) and
        (__ \ "hasAccepted").readNullable[Boolean].map(_.getOrElse(false))
      ) (Amp.apply _)
  }

  implicit lazy val writes: OWrites[Amp] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[AmpData] and
        (__ \ "lastUpdated").write(MongoDateTimeFormats.localDateTimeWrite) and
        (__ \ "hasChanged").write[Boolean] and
        (__ \ "hasAccepted").write[Boolean]
      ) (unlift(Amp.unapply))
  }

}
