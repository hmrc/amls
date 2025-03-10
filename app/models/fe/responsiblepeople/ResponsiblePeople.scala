/*
 * Copyright 2024 HM Revenue & Customs
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

import models.des.responsiblepeople.{NameDetails, OthrNamesOrAliasesDetails, ResponsiblePersons}

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}

case class ResponsiblePeople(
  personName: Option[PersonName] = None,
  legalName: Option[PreviousName] = None,
  legalNameChangeDate: Option[LocalDate] = None,
  knownBy: Option[KnownBy] = None,
  personResidenceType: Option[PersonResidenceType] = None,
  ukPassport: Option[UKPassport] = None,
  nonUKPassport: Option[NonUKPassport] = None,
  dateOfBirth: Option[DateOfBirth] = None,
  contactDetails: Option[ContactDetails] = None,
  addressHistory: Option[ResponsiblePersonAddressHistory] = None,
  positions: Option[Positions] = None,
  saRegistered: Option[SaRegistered] = None,
  vatRegistered: Option[VATRegistered] = None,
  experienceTraining: Option[ExperienceTraining] = None,
  training: Option[Training] = None,
  approvalFlags: ApprovalFlags = ApprovalFlags(hasAlreadyPassedFitAndProper = None, hasAlreadyPaidApprovalCheck = None),
  lineId: Option[Int] = None,
  status: Option[String] = None,
  hasChanged: Boolean = false
)

object ResponsiblePeople {

  implicit val format: OFormat[ResponsiblePeople] = Json.format[ResponsiblePeople]

  def legalNameChangeDate(maybeDetails: Option[NameDetails]): Option[LocalDate] = for {
    details <- maybeDetails
    name    <- details.previousNameDetails
    date    <- name.dateOfChange
  } yield LocalDate.parse(date)

  implicit def convOtherNames(otherNames: Option[OthrNamesOrAliasesDetails]): Option[String] =
    otherNames match {
      case Some(names) => names.aliases.fold[Option[String]](None)(x => x.headOption)
      case None        => None
    }

  def convertResponsiblePersonToResponsiblePeople(desRp: ResponsiblePersons): ResponsiblePeople = {
    val passedFitAndProper: Option[Boolean] = desRp.passedFitAndProperTest

    val paidApproval: Option[Boolean] = desRp.passedApprovalCheck

    ResponsiblePeople(
      desRp.nameDetails,
      desRp.nameDetails flatMap { n => n.previousNameDetails },
      legalNameChangeDate(desRp.nameDetails) orElse None,
      desRp.nameDetails flatMap { n => n.othrNamesOrAliasesDetails },
      desRp.nationalityDetails,
      desRp,
      desRp,
      desRp,
      desRp.contactCommDetails,
      desRp,
      desRp,
      desRp.regDetails,
      desRp.regDetails,
      desRp,
      desRp,
      ApprovalFlags(passedFitAndProper, paidApproval),
      desRp.extra.lineId,
      desRp.extra.status
    )
  }

  implicit def convert(rp: Option[Seq[ResponsiblePersons]]): Option[Seq[ResponsiblePeople]] =
    rp match {
      case Some(data) =>
        Some(data.map(x => convertResponsiblePersonToResponsiblePeople(x)))
      case _          => None
    }

}
