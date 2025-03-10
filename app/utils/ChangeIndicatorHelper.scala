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

package utils

import models.des.businessactivities.MlrActivitiesAppliedFor
import models.des.{AmendVariationRequest, SubscriptionView}
import models.fe.amp.Amp
import models.fe.asp.Asp
import models.fe.businessactivities.BusinessActivities
import models.fe.businessdetails.BusinessDetails
import models.fe.businessmatching.BusinessMatching
import models.fe.eab.Eab
import models.fe.hvd.Hvd
import models.fe.moneyservicebusiness.MoneyServiceBusiness
import models.fe.supervision.Supervision
import models.fe.tcsp.Tcsp
import play.api.Logging

trait ChangeIndicatorHelper extends Logging {

  def businessActivitiesChangeIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    convAndCompareBusinessActivities(response, desRequest)

  def msbChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    if (hasMsbSector(response)) {
      convAndcompareMsb(response, desRequest)
    } else {
      isMsbChanged(response, desRequest)
    }

  def hvdChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    if (hasHvdSector(response)) {
      convAndcompareHvd(response, desRequest)
    } else {
      isHvdChanged(response, desRequest)
    }

  def aspChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    if (hasAspSector(response)) {
      convAndcompareAsp(response, desRequest)
    } else {
      isAspChanged(response, desRequest)
    }

  def tcspChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    if (hasTcspSector(response)) {
      convAndcompareTcsp(response, desRequest)
    } else {
      isTcspChanged(desRequest, response)
    }

  def eabChangedIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    if (hasEabSector(response)) {
      convAndcompareEab(response, desRequest)
    } else {
      isEABChanged(desRequest, response)
    }

  def aspOrTcspChangeIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    if (hasAspSector(response) || hasTcspSector(response)) {
      convAndCompareAspOrTcsp(response, desRequest)
    } else {
      isAspOrTcspChanged(response, desRequest)
    }

  def ampChangeIndicator(response: SubscriptionView, desRequest: AmendVariationRequest) =
    !response.amp.equals(desRequest.amp)

  private def hasMsbSector(response: SubscriptionView) =
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor(true, _, _, _, _, _, _, _)) => true
      case _                                                        => false
    }

  private def hasHvdSector(response: SubscriptionView) =
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor(_, true, _, _, _, _, _, _)) => true
      case _                                                        => false
    }

  private def hasAspSector(response: SubscriptionView) =
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor(_, _, true, _, _, _, _, _)) => true
      case _                                                        => false
    }

  private def hasTcspSector(response: SubscriptionView) =
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor(_, _, _, true, _, _, _, _)) => true
      case _                                                        => false
    }

  private def hasEabSector(response: SubscriptionView) =
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor(_, _, _, _, true, _, _, _)) => true
      case _                                                        => false
    }

  private def convAndCompareBusinessActivities(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feBM   = BusinessMatching.conv(viewResponse)
    val feHvd  = Hvd.conv(viewResponse)
    val feAsp  = Asp.conv(viewResponse)
    val feTcsp = Tcsp.conv(viewResponse)
    val feEab  = Eab.conv(viewResponse)
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
    val desBusinessActivitiesAll   = models.des.businessactivities.BusinessActivitiesAll
      .convtoActivitiesALLChangeFlags(feBD, feBA, feAsp, feEab, feHvd, feBM)

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desMlrActivitiesAppliedFor: $desMlrActivitiesAppliedFor"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.mlrActivitiesAppliedFor: ${desRequest.businessActivities.mlrActivitiesAppliedFor}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desMsbServicesCarriedOut: $desMsbServicesCarriedOut"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.msbServicesCarriedOut: ${desRequest.businessActivities.msbServicesCarriedOut}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desHvdGoodsSold: $desHvdGoodsSold"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.hvdGoodsSold: ${desRequest.businessActivities.hvdGoodsSold}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desHvdAlcoholTobacco: $desHvdAlcoholTobacco"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.hvdAlcoholTobacco: ${desRequest.businessActivities.hvdAlcoholTobacco}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desAspServicesOffered: $desAspServicesOffered"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.aspServicesOffered: ${desRequest.businessActivities.aspServicesOffered}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desTcspServicesOffered: $desTcspServicesOffered"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.tcspServicesOffered: ${desRequest.businessActivities.tcspServicesOffered}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desServicesforRegOff: $desServicesforRegOff"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.tcspServicesforRegOffBusinessAddrVirtualOff: ${desRequest.businessActivities.tcspServicesforRegOffBusinessAddrVirtualOff}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desEabServices: $desEabServices"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.eabServicesCarriedOut: ${desRequest.businessActivities.eabServicesCarriedOut}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desAmpServices: $desAmpServices"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.ampServicesCarriedOut: ${desRequest.businessActivities.ampServicesCarriedOut}"
    )

    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desBusinessActivitiesAll: $desBusinessActivitiesAll"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareBusinessActivities - desRequest.businessActivities.all: ${desRequest.businessActivities.all}"
    )

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

  private def convAndcompareMsb(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feBM   = BusinessMatching.conv(viewResponse)
    val feMsb  = MoneyServiceBusiness.conv(viewResponse)
    val desMsb = models.des.msb.MoneyServiceBusiness.conv(feMsb, feBM, amendVariation = true)

    logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareMsb - desMsb: $desMsb")
    logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareMsb - desRequest.msb: ${desRequest.msb}")

    !desMsb.equals(desRequest.msb)
  }

  private def convAndcompareHvd(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feHvd  = Hvd.conv(viewResponse)
    val desHvd = models.des.hvd.Hvd.conv(feHvd)

    logger.debug(s"[AmendVariationService][compareAndUpdate] convAndCompareAspOrTcsp - desHvd: $desHvd")
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareAspOrTcsp - desRequest.hvd: ${desRequest.hvd}"
    )

    !desHvd.equals(desRequest.hvd)
  }

  private def convAndcompareAsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feAsp  = Asp.conv(viewResponse)
    val desAsp = models.des.asp.Asp.conv(feAsp)

    logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareAsp - desAsp: $desAsp")
    logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareAsp - desRequest.tcspAll: ${desRequest.asp}")

    !desAsp.equals(desRequest.asp)
  }

  private def convAndcompareTcsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feTcsp              = Tcsp.conv(viewResponse)
    val desTcsp             = Some(models.des.tcsp.TcspAll.conv(feTcsp))
    val desTcspFormationAgt =
      if (
        viewResponse.businessActivities.tcspServicesOffered.isDefined &&
        viewResponse.businessActivities.tcspServicesOffered.get.trustOrCompFormAgent
      ) {
        Some(models.des.tcsp.TcspTrustCompFormationAgt.conv(feTcsp))
      } else {
        None
      }

    logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - desTcsp: $desTcsp")
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - desRequest.tcspAll: ${desRequest.tcspAll}"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - desTcspFormationAgt: $desTcspFormationAgt"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndcompareTcsp - desRequest.tcspTrustCompFormationAgt:${desRequest.tcspTrustCompFormationAgt}"
    )

    !(desTcsp.equals(desRequest.tcspAll) &&
      desTcspFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private def convAndcompareEab(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feEab = Eab.conv(viewResponse)

    feEab match {
      case None           => true
      case Some(eabModel) =>
        val desEab             = Some(models.des.estateagentbusiness.EabAll.convert(eabModel))
        val desEabResdEstAgncy = models.des.estateagentbusiness.EabResdEstAgncy.conv(feEab)
        val desLettingAgents   = models.des.estateagentbusiness.LettingAgents.conv(feEab)

        logger.debug(s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desEab: $desEab")
        logger.debug(
          s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desRequest.eabAll: ${desRequest.eabAll}"
        )
        logger.debug(
          s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desEabResdEstAgncy: $desEabResdEstAgncy"
        )
        logger.debug(
          s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desRequest.eabResdEstAgncy: ${desRequest.eabResdEstAgncy}"
        )
        logger.debug(
          s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desLettingAgents: $desLettingAgents"
        )
        logger.debug(
          s"[AmendVariationService][compareAndUpdate] convAndcompareEab - desRequest.lettingAgents: ${desRequest.lettingAgents}"
        )

        !(desEab.equals(desRequest.eabAll) &&
          desEabResdEstAgncy.equals(desRequest.eabResdEstAgncy) &&
          desLettingAgents.equals(desRequest.lettingAgents))
    }
  }

  private def convAndCompareAspOrTcsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val feAspOrTcsp  =
      Supervision.convertFrom(viewResponse.aspOrTcsp, viewResponse.businessActivities.mlrActivitiesAppliedFor)
    val desAspOrTcsp = models.des.supervision.AspOrTcsp.conv(feAspOrTcsp)

    logger.debug(s"[AmendVariationService][compareAndUpdate] convAndCompareAspOrTcsp - desAspOrTcsp: $desAspOrTcsp")
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] convAndCompareAspOrTcsp - desRequest.aspOrTcsp: ${desRequest.aspOrTcsp}"
    )

    !desAspOrTcsp.equals(desRequest.aspOrTcsp)
  }

  private def isMsbChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    logger.debug(s"[AmendVariationService][compareAndUpdate] isMsbChanged - response.msb: ${response.msb}")
    logger.debug(s"[AmendVariationService][compareAndUpdate] isMsbChanged - desRequest.tcspAll: ${desRequest.msb}")

    !response.msb.equals(desRequest.msb)
  }

  private def isHvdChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    logger.debug(s"[AmendVariationService][compareAndUpdate] isHvdChanged - response.hvd: ${response.hvd}")
    logger.debug(s"[AmendVariationService][compareAndUpdate] isHvdChanged - desRequest.hvd: ${desRequest.hvd}")

    !response.hvd.equals(desRequest.hvd)
  }

  private def isAspChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    logger.debug(s"[AmendVariationService][compareAndUpdate] isAspChanged - response.hvd: ${response.asp}")
    logger.debug(s"[AmendVariationService][compareAndUpdate] isAspChanged - desRequest.asp: ${desRequest.asp}")

    !response.asp.equals(desRequest.asp)
  }

  private def isTcspChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - response.tcspAll: ${response.tcspAll}")
    logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - desRequest.tcspAll: ${desRequest.tcspAll}")
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] isTcspChanged - response.tcspTrustCompFormationAgt: ${response.tcspTrustCompFormationAgt}"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] isTcspChanged - desRequest.tcspTrustCompFormationAgt: ${desRequest.tcspTrustCompFormationAgt}"
    )

    !(response.tcspAll.equals(desRequest.tcspAll) &&
      response.tcspTrustCompFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private def isEABChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - response.eabAll: ${response.eabAll}")
    logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - desRequest.eabAll: ${desRequest.eabAll}")
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] isEABChanged - response.eabResdEstAgncy: ${response.eabResdEstAgncy}"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] isEABChanged - desRequest.eabResdEstAgncy: ${desRequest.eabResdEstAgncy}"
    )

    !(response.eabAll.equals(desRequest.eabAll) &&
      response.eabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }

  private def isAspOrTcspChanged(response: SubscriptionView, desRequest: AmendVariationRequest) = {
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] isAspOrTcspChanged - response.aspOrTcsp: ${response.aspOrTcsp}"
    )
    logger.debug(
      s"[AmendVariationService][compareAndUpdate] isAspOrTcspChanged - desRequest.aspOrTcsp: ${desRequest.aspOrTcsp}"
    )

    !response.aspOrTcsp.equals(desRequest.aspOrTcsp)
  }
}
