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

package utils

import models.des.{AmendVariationRequest, SubscriptionView}
import models.des.businessactivities.MlrActivitiesAppliedFor
import models.des.tcsp.TcspTrustCompFormationAgt
import models.fe.asp.Asp
import models.fe.businessmatching.BusinessMatching
import models.fe.estateagentbusiness.EstateAgentBusiness
import models.fe.hvd.Hvd
import models.fe.moneyservicebusiness.MoneyServiceBusiness
import models.fe.tcsp.Tcsp
import play.api.Logger

// TODO - this was done as part of an investigate spike. Add full unit test coverage to this helper trait.
trait AmendVariationHelper {
  def hasMsbSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (true, _, _, _, _, _, _)) => true
      case _ => false
    }
  }

  def hasHvdSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, true, _, _, _, _, _)) => true
      case _ => false
    }
  }

  def hasAspSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, _, true, _, _, _, _)) => true
      case _ => false
    }
  }

  def hasTcspSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, _, _, true, _, _, _)) => true
      case _ => false
    }
  }

  def hasEabSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, _, _, _, true, _, _)) => true
      case _ => false
    }
  }

  def hasFormationAgent(response: SubscriptionView) = {
    response.tcspTrustCompFormationAgt match {
      case Some(TcspTrustCompFormationAgt(_, _)) => true
      case _ => false
    }
  }

  def convAndcompareMsb(viewResponse: SubscriptionView, desRequest: AmendVariationRequest)  = {
    val api5BM = BusinessMatching.conv(viewResponse)
    val api5Msb = MoneyServiceBusiness.conv(viewResponse)
    val convApi5Msb = models.des.msb.MoneyServiceBusiness.conv(api5Msb, api5BM, amendVariation = true)

    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareMsb - convApi5Msb: ${convApi5Msb}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareMsb - desRequest.msb: ${desRequest.msb}")

    !convApi5Msb.equals(desRequest.msb)
  }

  def convAndcompareHvd(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Hvd = Hvd.conv(viewResponse)
    val convApi5Hvd = models.des.hvd.Hvd.conv(api5Hvd)

    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareHvd - convApi5Hvd: ${convApi5Hvd}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareHvd - desRequest.hvd: ${desRequest.hvd}")

    !convApi5Hvd.equals(desRequest.hvd)
  }

  def convAndcompareAsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Asp = Asp.conv(viewResponse)
    val convApi5Asp = models.des.asp.Asp.conv(api5Asp)

    Logger.debug(s"[AmendVariationService][compareAndUpdate] isASPChanged - convApi5Asp: ${convApi5Asp}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isHChanged - desRequest.asp: ${desRequest.asp}")

    !convApi5Asp.equals(desRequest.asp)
  }

  def formationAgent(viewResponse: SubscriptionView, api5Tcsp: Option[Tcsp]) = {
    if(hasFormationAgent(viewResponse)) {
      Some(models.des.tcsp.TcspTrustCompFormationAgt.conv(api5Tcsp))
    } else {
      viewResponse.tcspTrustCompFormationAgt
    }
  }

  def convAndcompareTcsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Tcsp = Tcsp.conv(viewResponse)
    val convApi5Tcsp = Some(models.des.tcsp.TcspAll.conv(api5Tcsp))
    val convApi5TcspFormationAgt = formationAgent(viewResponse, api5Tcsp)

    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - convApi5Tcsp: ${convApi5Tcsp}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - desRequest.tcspAll: ${desRequest.tcspAll}")

    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - convApi5TcspFormationAgt: ${convApi5TcspFormationAgt}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - desRequest.tcspTrustCompFormationAgt: ${desRequest.tcspTrustCompFormationAgt}")

    !(convApi5Tcsp.equals(desRequest.tcspAll) &&
      convApi5TcspFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  def convAndcompareEab(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Eab = EstateAgentBusiness.conv(viewResponse)
    val convApi5Eab = Some(models.des.estateagentbusiness.EabAll.convert(api5Eab.getOrElse(EstateAgentBusiness())))
    val convApi5EabResdEstAgncy =  models.des.estateagentbusiness.EabResdEstAgncy.convert(api5Eab)

    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareEab - convApi5Eabl: ${convApi5Eab}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desRequest.eabAll: ${desRequest.eabAll}")

    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareEab - convApi5EabResdEstAgncy: ${convApi5EabResdEstAgncy}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desRequest.eabResdEstAgncy: ${desRequest.eabResdEstAgncy}")

    !(convApi5Eab.equals(desRequest.eabAll) &&
      convApi5EabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }

  def isMsbChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isMsbChanged - viewResponse.msb: ${response.msb}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isMsbChanged - desRequest.msb: ${desRequest.msb}")

    !response.msb.equals(desRequest.msb)
  }


  def isHvdChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isHvdChanged - viewResponse.hvd: ${response.hvd}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isHvdChanged - desRequest.hvd: ${desRequest.hvd}")

    !response.hvd.equals(desRequest.hvd)
  }

  def isAspChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isAspChanged - viewResponse.asp: ${response.asp}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isAspChanged - desRequest.asp: ${desRequest.asp}")

    !response.asp.equals(desRequest.asp)
  }

  def isTcspChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - response.tcspAll: ${response.tcspAll}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - desRequest.tcspAll: ${desRequest.tcspAll}")

    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - response.tcspTrustCompFormationAgt: ${response.tcspTrustCompFormationAgt}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - desRequest.tcspTrustCompFormationAgt: ${desRequest.tcspTrustCompFormationAgt}")
    !(response.tcspAll.equals(desRequest.tcspAll) &&
      response.tcspTrustCompFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  def isEABChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - response.eabAll: ${response.eabAll}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - desRequest.eabAll: ${desRequest.eabAll}")

    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - response.eabResdEstAgncy: ${response.eabResdEstAgncy}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - desRequest.eabResdEstAgncy: ${desRequest.eabResdEstAgncy}")

    !(response.eabAll.equals(desRequest.eabAll) &&
      response.eabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }

  def msbChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    if(hasMsbSector(response)) {
      convAndcompareMsb(response, desRequest)
    } else {
      isMsbChanged(response, desRequest)
    }
  }

  def hvdChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    if(hasHvdSector(response)) {
      convAndcompareHvd(response, desRequest)
    } else {
      isHvdChanged(response, desRequest)
    }
  }

  def aspChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    if(hasAspSector(response)) {
      convAndcompareAsp(response, desRequest)
    } else {
      isAspChanged(response, desRequest)
    }
  }

  def tcspChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    if(hasTcspSector(response)) {
      convAndcompareTcsp(response, desRequest)
    } else {
      isTcspChanged(desRequest, response)
    }
  }

  def eabChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    if(hasEabSector(response)) {
      convAndcompareEab(response, desRequest)
    } else {
      isEABChanged(desRequest, response)
    }
  }
}