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

import models.fe.responsiblepeople._
import play.api.libs.json.Json

case class NonUkResident (dateOfBirth: String,
                          passportHeld: Boolean,
                          passportDetails: Option[PassportDetail]
                         )

object NonUkResident {
  implicit val format = Json.format[NonUkResident]

  implicit def convert(rp: ResponsiblePeople): Option[IdDetail] = {
    rp.personResidenceType map { rt =>
      rp.ukPassport.fold[Option[IdDetail]](None){
        case UKPassportYes(num) => rt.isUKResidence match {
          case dtls@NonUKResidence(_) =>
            Some(IdDetail(
              None, Some(NonUkResident(dtls.dateOfBirth.toString, true, Some(PassportDetail(true, PassportNum(ukPassportNumber = Some(num))))))
            ))
        }
      } getOrElse {
        (rp.nonUKPassport, rt.isUKResidence) match {
          case (Some(NonUKPassportYes(num)), dtls@NonUKResidence(_)) => IdDetail(None, Some(NonUkResident(dtls.dateOfBirth.toString, true,
            Some(PassportDetail(false, PassportNum(nonUkPassportNumber = Some(num)))))))
          case (Some(NoPassport), dtls@NonUKResidence(_)) => IdDetail(None, Some(NonUkResident(dtls.dateOfBirth.toString, false, None)))
        }
      }
    }
  }
}
