/*
 * Copyright 2021 HM Revenue & Customs
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

package models.des.aboutyou

import play.api.libs.json.Json

case class Aboutyou(individualDetails: Option[IndividualDetails] = None,
                    employedWithinBusiness: Boolean,
                    roleWithinBusiness: Option[String] = None,
                    specifyOtherRoleInBusiness: Option[String] = None,
                    roleForTheBusiness: Option[String] = None,
                    specifyOtherRoleForBusiness: Option[String] = None) {

}

object Aboutyou {
  implicit val format = Json.format[Aboutyou]

  implicit def convertFromRelease7(aboutYouRelease7: AboutYouRelease7): Aboutyou = {

    val roleWithin: Option[String] = {
      aboutYouRelease7.roleWithinBusiness match {
        case Some(x) if x.beneficialShareholder => Some("Beneficial Shareholder")
        case Some(x) if x.director => Some("Director")
        case Some(x) if x.partner => Some("Partner")
        case Some(x) if x.internalAccountant => Some("Internal Accountant")
        case Some(x) if x.soleProprietor => Some("Sole proprietor")
        case Some(x) if x.nominatedOfficer => Some("Nominated officer")
        case Some(x) if x.designatedMember => Some("Designated Member")
        case Some(x) if x.other => Some("Other")
        case _ => None
      }
    }

    val roleFor: Option[String] = {
      aboutYouRelease7.roleForTheBusiness match {
        case Some(x) if x.externalAccountant => Some("External Accountant")
        case _ => Some("Other")
      }
    }

    Aboutyou(
      aboutYouRelease7.individualDetails,
      aboutYouRelease7.employedWithinBusiness,
      roleWithin,
      aboutYouRelease7.roleWithinBusiness flatMap (x => x.specifyOtherRoleInBusiness),
      roleFor,
      aboutYouRelease7.roleForTheBusiness flatMap (x => x.specifyOtherRoleForBusiness)
    )

  }
}


