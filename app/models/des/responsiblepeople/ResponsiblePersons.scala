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

import models.des.StatusProvider
import models.fe
import models.fe.responsiblepeople.TimeAtAddress._
import models.fe.responsiblepeople._
import play.api.libs.json.{Reads, Writes}

case class ResponsiblePersons(nameDetails: Option[NameDetails],
                              nationalityDetails: Option[NationalityDetails],
                              contactCommDetails: Option[ContactCommDetails],
                              currentAddressDetails: Option[CurrentAddress],
                              timeAtCurrentAddress: Option[String],
                              addressUnderThreeYears: Option[AddressUnderThreeYears],
                              timeAtAddressUnderThreeYears: Option[String],
                              addressUnderOneYear: Option[AddressUnderThreeYears],
                              timeAtAddressUnderOneYear: Option[String],
                              positionInBusiness: Option[PositionInBusiness],
                              regDetails: Option[RegDetails],
                              previousExperience: Boolean,
                              descOfPrevExperience: Option[String],
                              amlAndCounterTerrFinTraining: Boolean,
                              trainingDetails: Option[String],
                              startDate: Option[String],
                              dateChangeFlag: Option[Boolean] = None,
                              msbOrTcsp: Option[MsbOrTcsp],
                              extra: RPExtra
                             )

object ResponsiblePersons {

  implicit val jsonReads: Reads[ResponsiblePersons] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (
      (__ \ "nameDetails").readNullable[NameDetails] and
        (__ \ "nationalityDetails").readNullable[NationalityDetails] and
        (__ \ "contactCommDetails").readNullable[ContactCommDetails] and
        (__ \ "currentAddressDetails").readNullable[CurrentAddress] and
        (__ \ "timeAtCurrentAddress").readNullable[String] and
        (__ \ "addressUnderThreeYears").readNullable[AddressUnderThreeYears] and
        (__ \ "timeAtAddressUnderThreeYears").readNullable[String] and
        (__ \ "addressUnderOneYear").readNullable[AddressUnderThreeYears] and
        (__ \ "timeAtAddressUnderOneYear").readNullable[String] and
        (__ \ "positionInBusiness").readNullable[PositionInBusiness] and
        (__ \ "regDetails").readNullable[RegDetails] and
        (__ \ "previousExperience").read[Boolean] and
        (__ \ "descOfPrevExperience").readNullable[String] and
        (__ \ "amlAndCounterTerrFinTraining").read[Boolean] and
        (__ \ "trainingDetails").readNullable[String] and
        (__ \ "startDate").readNullable[String] and
        (__ \ "dateChangeFlag").readNullable[Boolean] and
        (__ \ "msbOrTcsp").readNullable[MsbOrTcsp]and
        __.read[RPExtra]
      ) (ResponsiblePersons.apply _)
  }

  implicit val jsonWrites: Writes[ResponsiblePersons] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Writes._
    import play.api.libs.json._
    (
      (__ \ "nameDetails").writeNullable[NameDetails] and
        (__ \ "nationalityDetails").writeNullable[NationalityDetails] and
        (__ \ "contactCommDetails").writeNullable[ContactCommDetails] and
        (__ \ "currentAddressDetails").writeNullable[CurrentAddress] and
        (__ \ "timeAtCurrentAddress").writeNullable[String] and
        (__ \ "addressUnderThreeYears").writeNullable[AddressUnderThreeYears] and
        (__ \ "timeAtAddressUnderThreeYears").writeNullable[String] and
        (__ \ "addressUnderOneYear").writeNullable[AddressUnderThreeYears] and
        (__ \ "timeAtAddressUnderOneYear").writeNullable[String] and
        (__ \ "positionInBusiness").writeNullable[PositionInBusiness] and
        (__ \ "regDetails").writeNullable[RegDetails] and
        (__ \ "previousExperience").write[Boolean] and
        (__ \ "descOfPrevExperience").writeNullable[String] and
        (__ \ "amlAndCounterTerrFinTraining").write[Boolean] and
        (__ \ "trainingDetails").writeNullable[String] and
        (__ \ "startDate").writeNullable[String] and
        (__ \ "dateChangeFlag").writeNullable[Boolean] and
        (__ \ "msbOrTcsp").writeNullable[MsbOrTcsp] and
        __.write[RPExtra]
      ) (unlift(ResponsiblePersons.unapply))
  }

  implicit def default(responsiblePeople: Option[ResponsiblePersons]): ResponsiblePersons =
    responsiblePeople.getOrElse(ResponsiblePersons(None, None, None, None, None, None, None, None, None, None, None, false, None, false, None, None, None, None,
      RPExtra(None)))

  implicit def convert(responsiblePeople: Option[Seq[ResponsiblePeople]], bm: fe.businessmatching.BusinessMatching): Option[Seq[ResponsiblePersons]] = {
    responsiblePeople match {
      case Some(data) =>
        Some(data.map(x => convertRP(x, bm)))
      case _ => None
    }
  }

  implicit def convStartDate(startDate: Option[Positions]): Option[String] = {
    startDate match {
      case Some(data) => data.startDate map {
        date => date.toString("yyyy-MM-dd")
      }
      case _ => None
    }
  }

  implicit def convertRP(rp: ResponsiblePeople, bm: fe.businessmatching.BusinessMatching): ResponsiblePersons = {
    val (training, trainingDesc) = convTraining(rp.training)
    val (expTraining, expTrainingDesc) = convExpTraining(rp.experienceTraining)

    ResponsiblePersons(
      rp.personName,
      rp.personResidenceType,
      rp.contactDetails,
      rp.addressHistory.fold[Option[ResponsiblePersonCurrentAddress]](None) { x => x.currentAddress },
      rp.addressHistory.fold[Option[ResponsiblePersonCurrentAddress]](None) { x => x.currentAddress },
      rp.addressHistory.fold[Option[ResponsiblePersonAddress]](None) { x => x.additionalAddress },
      rp.addressHistory.fold[Option[ResponsiblePersonAddress]](None) { x => x.additionalAddress },
      rp.addressHistory.fold[Option[ResponsiblePersonAddress]](None) { x => x.additionalExtraAddress },
      rp.addressHistory.fold[Option[ResponsiblePersonAddress]](None) { x => x.additionalExtraAddress },
      PositionInBusiness.conv(rp.positions, bm),
      rp,
      expTraining,
      expTrainingDesc,
      training,
      trainingDesc,
      rp.positions,
      None,
      rp.hasAlreadyPassedFitAndProper.fold[Option[MsbOrTcsp]](None) { x => Some(MsbOrTcsp(x)) },
      rp
    )
  }

  def convTraining(training: Option[Training]): (Boolean, Option[String]) = {
    training match {
      case Some(data) => data match {
        case TrainingYes(desc) => (true, Some(desc))
        case TrainingNo => (false, None)
      }
      case _ => (false, None)
    }
  }

  def convExpTraining(vat: Option[ExperienceTraining]): (Boolean, Option[String]) = {
    vat match {
      case Some(data) => data match {
        case ExperienceTrainingYes(desc) => (true, Some(desc))
        case ExperienceTrainingNo => (false, None)
      }
      case _ => (false, None)
    }
  }

  implicit def convDurationOption(addrHistory: Option[ResponsiblePersonAddress]): Option[String] = {
    addrHistory match {
      case Some(data) => data
      case _ => None
    }
  }

  implicit def convDurationOptionCurrent(addrHistory: Option[ResponsiblePersonCurrentAddress]): Option[String] = {
    addrHistory match {
      case Some(data) => data
      case _ => None
    }
  }

  implicit def convDuration(addrHistory: ResponsiblePersonAddress): Option[String] = {
    Some(addrHistory.timeAtAddress)
  }

  implicit def convDuration(addrHistory: ResponsiblePersonCurrentAddress): Option[String] = {
    Some(addrHistory.timeAtAddress)
  }

  implicit def covnTimeAtAddrToString(time: TimeAtAddress): String = {
    time match {
      case ZeroToFiveMonths => "0-6 months"
      case SixToElevenMonths => "7-12 months"
      case OneToThreeYears => "1-3 years"
      case ThreeYearsPlus => "3+ years"
      case Empty => ""
    }
  }

  implicit object RpExtraHasStatus extends StatusProvider[ResponsiblePersons]{
    override def getStatus(rp:ResponsiblePersons): Option[String] = rp.extra.status
  }
}
