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

import models.fe
import models.fe.businessmatching.BusinessType
import models.fe.responsiblepeople.{SoleProprietor => FeSoleProprietor, _}
import play.api.libs.json.Json

case class PositionInBusiness(soleProprietor: Option[SoleProprietor],
                              partnership: Option[Partnership],
                              corpBodyOrUnInCorpBodyOrLlp: Option[CorpBodyOrUnInCorpBodyOrLlp])

object PositionInBusiness {
  implicit val format = Json.format[PositionInBusiness]

  implicit def conv(positions: Option[Positions], bm:fe.businessmatching.BusinessMatching): Option[PositionInBusiness] = {
    positions match {
      case Some(data) => convPositions(data, bm)
      case _ => None
    }
  }

  implicit def convPositions(positions: Positions, bm: fe.businessmatching.BusinessMatching): Option[PositionInBusiness] = {
    val (beneficialOwner, director, internalAccountant, nominatedOfficer, partner, soleProprietor) = positions.positions.foldLeft(false, false,
      false, false, false, false){
      (pos, p) => p match {
        case BeneficialOwner => pos.copy(_1 = true)
        case Director => pos.copy(_2 = true)
        case InternalAccountant => pos.copy(_3 = true)
        case NominatedOfficer => pos.copy(_4 = true)
        case Partner => pos.copy(_5 = true)
        case FeSoleProprietor => pos.copy(_6 = true)
      }
    }
    bm.reviewDetails.businessType match {
      case BusinessType.SoleProprietor => Some(PositionInBusiness(Some(SoleProprietor(soleProprietor, nominatedOfficer)), None,
        None))
      case BusinessType.Partnership => Some(PositionInBusiness(None, Some(Partnership(partner, nominatedOfficer)),
        None))
      case BusinessType.LPrLLP | BusinessType.LimitedCompany | BusinessType.UnincorporatedBody =>
        Some(PositionInBusiness(None, None,
          Some(CorpBodyOrUnInCorpBodyOrLlp(director, beneficialOwner, nominatedOfficer))))
    }

  }
}
