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

package models.des.tradingpremises

import config.AmlsConfig
import models.des.RequestType
import models.des.{StatusProvider, StringOrInt}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import models.fe.tradingpremises.{TradingPremises => FETradingPremises, _}

case class AgentDetails(
                         agentLegalEntity: String,
                         companyRegNo: Option[String] = None,
                         dateOfBirth: Option[String],
                         agentLegalEntityName: Option[String],
                         agentPremises: AgentPremises,
                         startDate: Option[String] = None,
                         status: Option[String] = None,
                         lineId: Option[StringOrInt] = None,
                         agentDetailsChangeDate: Option[String] = None
                       ) {
  override def hashCode = 41 + (41 + agentLegalEntity.hashCode + agentLegalEntityName.hashCode +
    agentPremises.hashCode + status.hashCode)

  override def equals(other: Any): Boolean = other match {
    case (that: AgentDetails) =>
      this.agentLegalEntity.equals(that.agentLegalEntity) &&
        this.agentLegalEntityName.equals(that.agentLegalEntityName) &&
        this.agentPremises.equals(that.agentPremises) &&
        this.status.equals(this.status)
    case _ => false
  }
}

object AgentDetails {

  implicit val jsonReads: Reads[AgentDetails] = {
    (
      (__ \ "agentLegalEntity").read[String] and
        (__ \ "companyRegNo").readNullable[String] and
        (__ \ "dateOfBirth").readNullable[String] and
        (__ \ "agentLegalEntityName").readNullable[String] and
        (__ \ "agentPremises").read[AgentPremises] and
        (__ \ "startDate").readNullable[String] and
        (__ \ "status").readNullable[String] and
        __.read(Reads.optionNoError[StringOrInt]) and
        (__ \ "agentDetailsChgDate").readNullable[String]
      ) (AgentDetails.apply _)
  }

  implicit val jsonWrites: Writes[AgentDetails] = {
    (
      (__ \ "agentLegalEntity").write[String] and
        (__ \ "companyRegNo").writeNullable[String] and
        (__ \ "dateOfBirth").writeNullable[String] and
        (__ \ "agentLegalEntityName").writeNullable[String] and
        (__ \ "agentPremises").write[AgentPremises] and
        (__ \ "startDate").writeNullable[String] and
        (__ \ "status").writeNullable[String] and
        __.writeNullable[StringOrInt] and
        (__ \ "agentDetailsChgDate").writeNullable[String]
      ) (unlift(AgentDetails.unapply _))
  }

  implicit def convert(tradingPremises: FETradingPremises)(implicit requestType: RequestType): AgentDetails = {

    def assignCompanyRegNo = if (AmlsConfig.release7) {
      tradingPremises.agentCompanyDetails.fold[Option[String]](None)(x => x.companyRegistrationNumber)
    } else {
      None
    }
    val startDate = (AmlsConfig.release7, requestType) match {
      case (true, RequestType.Amendment) => Some(tradingPremises.yourTradingPremises.startDate.toString)
      case _ => None
    }

    AgentDetails(
      agentLegalEntity = tradingPremises.businessStructure.fold("")(x => x),
      companyRegNo = assignCompanyRegNo,
      dateOfBirth = for {
        bs <- tradingPremises.businessStructure if bs == BusinessStructure.SoleProprietor
        agentName <- tradingPremises.agentName
        dob <- agentName.agentDateOfBirth
      } yield dob,
      agentLegalEntityName = Some(tradingPremises.businessStructure.fold("")({
        case BusinessStructure.SoleProprietor => tradingPremises.agentName.fold("")(x => x.agentName)
        case BusinessStructure.LimitedLiabilityPartnership | BusinessStructure.IncorporatedBody =>
          tradingPremises.agentCompanyDetails.fold("")(x => x.agentCompanyName)
        case BusinessStructure.Partnership => tradingPremises.agentPartnership.fold("")(x => x.agentPartnership)
        case BusinessStructure.UnincorporatedBody => ""
      })),
      agentPremises = tradingPremises,
      startDate,
      tradingPremises.status,
      tradingPremises.lineId,
      agentDetailsChangeDate = tradingPremises.agentName.fold[Option[String]](None)(_.dateOfChange)
    )
  }

  implicit def convert(tradingPremises: Seq[FETradingPremises])(implicit requestType: RequestType): Seq[AgentDetails] =
    tradingPremises.map(convert)

  implicit def convertBusinessStructure(businessStructure: BusinessStructure): String = {
    businessStructure match {
      case BusinessStructure.SoleProprietor => "Sole Proprietor"
      case BusinessStructure.LimitedLiabilityPartnership => "Limited Liability Partnership"
      case BusinessStructure.Partnership => "Partnership"
      case BusinessStructure.IncorporatedBody => "Corporate Body"
      case BusinessStructure.UnincorporatedBody => "Unincorporated Body"
    }
  }

  implicit object AgentDetailsHasStatus extends StatusProvider[AgentDetails] {
    override def getStatus(ad: AgentDetails): Option[String] = ad.status
  }

}
