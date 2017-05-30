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

package models.fe.responsiblepeople

import models.des.responsiblepeople.PassportDetail
import play.api.data.validation.ValidationError
import play.api.libs.json._

trait PassportType

object PassportType {

  implicit def convToUKPassport(passportType: PassportType): Option[UKPassport] = {
    passportType match {
      case UKPassportYes(ukPassportNumber) => Some(UKPassportYes(ukPassportNumber))
      case UKPassportNo => Some(UKPassportNo)
      case _ => None
    }
  }

  implicit def convToNonUKPassport(passportType: PassportType): Option[NonUKPassport] = {
    passportType match {
      case NonUKPassportYes(nonUKPassportNumber) => Some(NonUKPassportYes(nonUKPassportNumber))
      case NoPassport => Some(NoPassport)
      case _ => None
    }
  }

  implicit def conv(passportDetails: Option[PassportDetail]): PassportType = {
    passportDetails match {
      case Some(details) => details.ukPassport match {
        case true => UKPassportYes(details.passportNumber.ukPassportNumber.getOrElse(""))
        case false => NonUKPassportYes(details.passportNumber.nonUkPassportNumber.getOrElse(""))
      }
      case None => NoPassport
    }
  }

}
