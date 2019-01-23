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

package models.des.aboutthebusiness

import models.fe.aboutthebusiness.{AboutTheBusiness, VATRegisteredNo, VATRegisteredYes}
import play.api.libs.json._

case class VATRegistration(vatRegistered: Boolean, vrnNumber : Option[String])

object VATRegistration {

  implicit val format = Json.format[VATRegistration]

  implicit def convert(aboutTheBusiness: AboutTheBusiness): Option[VATRegistration] = {

    aboutTheBusiness.vatRegistered match {
      case Some(VATRegisteredYes(value)) => Some(VATRegistration(true, Some(value)))
      case Some(VATRegisteredNo) => Some(VATRegistration(false, None))
      case _ => None
    }
  }
}
