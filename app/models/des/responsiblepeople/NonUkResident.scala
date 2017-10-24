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

package models.des.responsiblepeople

import models.fe.responsiblepeople.{NonUKResidence, _}
import play.api.libs.json.Json

case class NonUkResident(dateOfBirth: String,
                         passportHeld: Boolean,
                         passportDetails: Option[PassportDetail]
                        )

object NonUkResident {
  implicit val format = Json.format[NonUkResident]

  implicit def convert(rp: ResponsiblePeople): Option[IdDetail] = {
    rp.personResidenceType map { rt =>
      rp.ukPassport flatMap {
        case UKPassportYes(num) => rt.isUKResidence match {
          case NonUKResidence =>
            rp.dateOfBirth map { dob =>
              IdDetail(
                None, Some(NonUkResident(dob.dateOfBirth.toString, true, Some(PassportDetail(true, PassportNum(ukPassportNumber = Some(num))))))
              )
            }
        }
        case _ => None
      } getOrElse {
        (rp.nonUKPassport, rt.isUKResidence) match {
          case (Some(NonUKPassportYes(num)), NonUKResidence) => {
            IdDetail(
              None,
              rp.dateOfBirth map { dob =>
                NonUkResident(dob.dateOfBirth.toString, true,
                  Some(PassportDetail(false, PassportNum(nonUkPassportNumber = Some(num)))))
              })
          }
          case (Some(NoPassport) | None, NonUKResidence) => {
            IdDetail(
              None,
              rp.dateOfBirth map { dob =>
                NonUkResident(dob.dateOfBirth.toString, false, None)
              })
          }
        }
      }
    }
  }
}
