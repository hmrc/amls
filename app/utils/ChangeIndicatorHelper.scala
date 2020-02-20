/*
 * Copyright 2020 HM Revenue & Customs
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
import models.fe.amp.Amp
import models.fe.asp.Asp
import models.fe.businessactivities.BusinessActivities
import models.fe.businessdetails.BusinessDetails
import models.fe.businessmatching.BusinessMatching
import models.fe.estateagentbusiness.EstateAgentBusiness
import models.fe.hvd.Hvd
import models.fe.moneyservicebusiness.MoneyServiceBusiness
import models.fe.supervision.Supervision
import models.fe.tcsp.Tcsp
import play.api.Logger

trait ChangeIndicatorHelper {

  def businessActivitiesChangeIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    convAndCompareBusinessActivities(response, desRequest)
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

  def aspOrTcspChangeIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    if(hasAspSector(response) || hasTcspSector(response)) {
      convAndCompareAspOrTcsp(response, desRequest)
    } else {
      isAspOrTcspChanged(response, desRequest)
    }
  }

  def ampChangeIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    !(response.amp.equals(desRequest.amp) &&
      response.businessActivities.ampServicesCarriedOut.equals(desRequest.businessActivities.ampServicesCarriedOut))
  }

  private def hasMsbSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (true, _, _, _, _, _, _, _)) => true
      case _ => false
    }
  }

  private def hasHvdSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, true, _, _, _, _, _, _)) => true
      case _ => false
    }
  }

  private def hasAspSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, _, true, _, _, _, _, _)) => true
      case _ => false
    }
  }

  private def hasTcspSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, _, _, true, _, _, _, _)) => true
      case _ => false
    }
  }

  private def hasEabSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor (_, _, _, _, true, _, _, _)) => true
      case _ => false
    }
  }

  private def convAndCompareBusinessActivities(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feBM   = BusinessMatching.conv(viewResponse)
    val feHvd  = Hvd.conv(viewResponse)
    val feAsp  = Asp.conv(viewResponse)
    val feTcsp = Tcsp.conv(viewResponse)
    val feEab  = EstateAgentBusiness.conv(viewResponse)
    val feAmp  = Amp.conv(viewResponse)
    val feBD   = BusinessDetails.conv(viewResponse)
    val feBA   = BusinessActivities.convertBusinessActivities(
      viewResponse.businessActivities.all,
      viewResponse.businessActivities.mlrActivitiesAppliedFor
    )

    val desMlrActivitiesAppliedFor = models.des.businessactivities.MlrActivitiesAppliedFor.conv(feBM)
    val desMsbServicesCarriedOut   = models.des.businessactivities.MsbServicesCarriedOut.conv(feBM)
    val desHvdGoodsSold            = models.des.businessactivities.HvdGoodsSold.conv(feHvd)
    val desHvdAlcoholTobacco       = models.des.businessactivities.HvdAlcoholTobacco.covn(feHvd)
    val desAspServicesOffered      = models.des.businessactivities.AspServicesOffered.conv(feAsp)
    val desTcspServicesOffered     = models.des.businessactivities.TcspServicesOffered.covn(feTcsp)
    val desServicesforRegOff       = models.des.businessactivities.ServicesforRegOff.conv(feTcsp)
    val desEabServices             = models.des.businessactivities.EabServices.convert(feEab)
    val desAmpServices             = models.des.businessactivities.AmpServices.conv(feAmp)
    val desBusinessActivitiesAll   = models.des.businessactivities.BusinessActivitiesAll.
      convtoActivitiesALLChangeFlags(feBD, feBA, feAsp, feEab, feHvd, feBM)

    !desMlrActivitiesAppliedFor.equals(desRequest.businessActivities.mlrActivitiesAppliedFor) ||
    !desMsbServicesCarriedOut.equals(desRequest.businessActivities.msbServicesCarriedOut) ||
    !desHvdGoodsSold.equals(desRequest.businessActivities.hvdGoodsSold) ||
    !desHvdAlcoholTobacco.equals(desRequest.businessActivities.hvdAlcoholTobacco) ||
    !desAspServicesOffered.equals(desRequest.businessActivities.aspServicesOffered) ||
    !desTcspServicesOffered.equals(desRequest.businessActivities.tcspServicesOffered) ||
    !desServicesforRegOff.equals(desRequest.businessActivities.tcspServicesforRegOffBusinessAddrVirtualOff) ||
    !desEabServices.equals(desRequest.businessActivities.eabServicesCarriedOut) ||
    !desAmpServices.equals(desRequest.businessActivities.ampServicesCarriedOut) ||
    !desBusinessActivitiesAll.equals(desRequest.businessActivities.all)
  }

  private def convAndcompareMsb(viewResponse: SubscriptionView, desRequest: AmendVariationRequest)  = {
    val feBM   = BusinessMatching.conv(viewResponse)
    val feMsb  = MoneyServiceBusiness.conv(viewResponse)
    val desMsb = models.des.msb.MoneyServiceBusiness.conv(feMsb, feBM, amendVariation = true)

    !desMsb.equals(desRequest.msb)
  }

  private def convAndcompareHvd(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feHvd  = Hvd.conv(viewResponse)
    val desHvd = models.des.hvd.Hvd.conv(feHvd)

    !desHvd.equals(desRequest.hvd)
  }

  private def convAndcompareAsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feAsp  = Asp.conv(viewResponse)
    val desAsp = models.des.asp.Asp.conv(feAsp)

    !desAsp.equals(desRequest.asp)
  }

  private def convAndcompareTcsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feTcsp              = Tcsp.conv(viewResponse)
    val desTcsp             = Some(models.des.tcsp.TcspAll.conv(feTcsp))
    val desTcspFormationAgt = Some(models.des.tcsp.TcspTrustCompFormationAgt.conv(feTcsp))

    !(desTcsp.equals(desRequest.tcspAll) &&
      desTcspFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private def convAndcompareEab(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feEab              = EstateAgentBusiness.conv(viewResponse)
    val desEab             = Some(models.des.estateagentbusiness.EabAll.convert(feEab.getOrElse(EstateAgentBusiness())))
    val desEabResdEstAgncy =  models.des.estateagentbusiness.EabResdEstAgncy.convert(feEab)

    !(desEab.equals(desRequest.eabAll) &&
      desEabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }

  private def convAndCompareAspOrTcsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feAspOrTcsp  = Supervision.convertFrom(viewResponse.aspOrTcsp, viewResponse.businessActivities.mlrActivitiesAppliedFor)
    val desAspOrTcsp = Some(models.des.supervision.AspOrTcsp.conv(feAspOrTcsp))

    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndCompareAspOrTcsp - desAspOrTcsp: ${desAspOrTcsp}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] convAndCompareAspOrTcsp - desRequest.aspOrTcsp: ${desRequest.aspOrTcsp}")

    !desAspOrTcsp.equals(desRequest.aspOrTcsp)
  }

  private def isMsbChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    !response.msb.equals(desRequest.msb)
  }

  private def isHvdChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    !response.hvd.equals(desRequest.hvd)
  }

  private def isAspChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    !response.asp.equals(desRequest.asp)
  }

  private def isTcspChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    !(response.tcspAll.equals(desRequest.tcspAll) &&
      response.tcspTrustCompFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private def isEABChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    !(response.eabAll.equals(desRequest.eabAll) &&
      response.eabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }

  private def isAspOrTcspChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isAspOrTcspChanged - response.aspOrTcsp: ${response.aspOrTcsp}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isAspOrTcspChanged - desRequest.aspOrTcsp: ${desRequest.aspOrTcsp}")

    !response.aspOrTcsp.equals(desRequest.aspOrTcsp)
  }
}