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

import models.des.aboutyou.Aboutyou
import play.api.libs.json.Json

case class ExtraFields(declaration: Declaration,
                       filingIndividual: Aboutyou,
                       etmpFields: Option[EtmpFields]) {

  def setEtmpFields(viewEtmpFields: Option[EtmpFields]) : ExtraFields = {
    this.copy(etmpFields = viewEtmpFields)
  }
}

object ExtraFields {
  implicit val format = Json.format[ExtraFields]

  implicit def convert(person: models.fe.declaration.AddPerson): ExtraFields = {
    ExtraFields(Declaration(true), person, None)
  }
}
