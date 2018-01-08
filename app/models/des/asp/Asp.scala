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

package models.des.asp

import models.fe.asp.{OtherBusinessTaxMattersNo, OtherBusinessTaxMattersYes}
import play.api.libs.json.Json

case class Asp (regHmrcAgtRegSchTax: Boolean = false,
                hmrcAgentRegNo: Option[String])

object Asp {

  implicit val format = Json.format[Asp]

  implicit def conv(asp: Option[models.fe.asp.Asp]) : Option[Asp] = {
    asp.otherBusinessTaxMatters match {
      case Some(OtherBusinessTaxMattersYes) => Some(Asp(true, None))
      case Some(OtherBusinessTaxMattersNo) => Some(Asp(false, None))
      case _ => None
    }
  }
}
