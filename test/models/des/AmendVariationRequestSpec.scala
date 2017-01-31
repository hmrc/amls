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

package models.des

import models._
import models.des.aboutthebusiness.PreviouslyRegisteredMLRView
import models.des.aboutyou.{Aboutyou, IndividualDetails}
import models.des.businessactivities.{ExpectedAMLSTurnover, BusinessActivityDetails}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsSuccess, JsUndefined, Json}
import play.api.test.FakeApplication
import utils.AckRefGenerator

class AmendVariationRequestSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "SubscriptionRequest serialisation" when {
    "businessReferencesAllButSp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          AmendVariationRequest(
            acknowledgementReference = ackref.ackRef,
            DesConstants.testChangeIndicators,
            "Amendment",
            DesConstants.testBusinessDetails,
            DesConstants.testViewBusinessContactDetails,
            DesConstants.testBusinessReferencesAll,
            None,
            Some(DesConstants.testBusinessReferencesCbUbLlp),
            DesConstants.testBusinessActivities,
            DesConstants.testTradingPremisesAPI6,
            DesConstants.testBankDetails,
            Some(DesConstants.testMsb),
            Some(DesConstants.testHvd),
            Some(DesConstants.testAsp),
            Some(DesConstants.testAspOrTcsp),
            Some(DesConstants.testTcspAll),
            Some(DesConstants.testTcspTrustCompFormationAgt),
            Some(DesConstants.testEabAll),
            Some(DesConstants.testEabResdEstAgncy),
            Some(DesConstants.testResponsiblePersonsForRpAPI6),
            DesConstants.extraFields
          )
        ) \ "businessReferencesAllButSp") must be (a[JsUndefined])
      }
    }

    "businessReferencesCbUbLlp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          AmendVariationRequest(
            acknowledgementReference = ackref.ackRef,
            DesConstants.testChangeIndicators,
            "Amendment",
            DesConstants.testBusinessDetails,
            DesConstants.testViewBusinessContactDetails,
            DesConstants.testBusinessReferencesAll,
            Some(DesConstants.testbusinessReferencesAllButSp),
            None,
            DesConstants.testBusinessActivities,
            DesConstants.testTradingPremisesAPI6,
            DesConstants.testBankDetails,
            Some(DesConstants.testMsb),
            Some(DesConstants.testHvd),
            Some(DesConstants.testAsp),
            Some(DesConstants.testAspOrTcsp),
            Some(DesConstants.testTcspAll),
            Some(DesConstants.testTcspTrustCompFormationAgt),
            Some(DesConstants.testEabAll),
            Some(DesConstants.testEabResdEstAgncy),
            Some(DesConstants.testResponsiblePersonsForRpAPI6),
            DesConstants.extraFields
          )
        ) \ "businessReferencesCbUbLlp") must be (a[JsUndefined])
      }
    }
  }


  "AmendVariationRequest" should {
    "de serialise the subscription json" when {
      "given valid json" in {
        val json = Json.toJson(convertedDesModel)
        val amendVariationRequestModel = convertedDesModel

        json.as[AmendVariationRequest] must be(amendVariationRequestModel)

        AmendVariationRequest.jsonReads.reads(
          AmendVariationRequest.jsonWrites.writes(convertedDesModel)) must
          be(JsSuccess(convertedDesModel))

        Json.toJson(convertedDesModel) must be(json)
      }
    }


    "convert frontend model to des model for amendment" in {
      implicit val mt = Amendment

      println(Console.BLUE + AmendVariationRequest.convert(feSubscriptionReq) + Console.WHITE)
      AmendVariationRequest.convert(feSubscriptionReq) must be(convertedDesModel.copy(amlsMessageType = "Amendment"))
    }

    "convert frontend model to des model for variation" in {
      implicit val mt = Variation
      AmendVariationRequest.convert(feSubscriptionReq) must be(convertedDesModel.copy(amlsMessageType = "Variation"))
    }

    "update extra field with data from view model[API 5]" in {

      val aboutYou = Aboutyou(Some(IndividualDetails("FirstName", Some("MiddleName"), "LastName")),
        true, Some("Beneficial Shareholder"), None, Some("Other"), None)

      val extraField = ExtraFields(Declaration(true), aboutYou, None)
      val newEtmpField = Some(EtmpFields(Some("2016-09-17T09:30:47Z"), Some("2016-10-17T09:30:47Z"), Some("2016-11-17T09:30:47Z"), Some("2016-12-17T09:30:47Z")))

      val updatedModel = ExtraFields(Declaration(true), aboutYou, newEtmpField)

      convertedDesModel.extraFields must be(extraField)

      convertedDesModel.extraFields.setEtmpFields(newEtmpField) must be(updatedModel)

    }

    "update ChangeIndicator  and extra Fields with data from view model[API 5]" in {

      val des = convertedDesModel.setChangeIndicator(newChangeIndicator)
      val updatedExtraFields = des.extraFields.setEtmpFields(newEtmpField)
      des.setExtraFields(updatedExtraFields) must be(updateAmendVariationRequest)

    }
  }

  val feSubscriptionReq = {
    import models.fe.SubscriptionRequest
    SubscriptionRequest(
      BusinessMatchingSection.modelForView,
      EabSection.modelForView,
      TradingPremisesSection.modelForView,
      AboutTheBusinessSection.modelForView,
      BankDetailsSection.modelForView,
      AboutYouSection.modelforView,
      BusinessActivitiesSection.modelForView,
      ResponsiblePeopleSection.modelForView,
      ASPTCSPSection.TcspModelForView,
      ASPTCSPSection.AspModelForView,
      MsbSection.modelForView,
      HvdSection.modelForView,
      SupervisionSection.modelForView
    )
  }

  val convertedDesModel = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
      None)),
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpAPI6),
    DesConstants.extraFields
  )
  val newEtmpField = Some(EtmpFields(Some("2016-09-17T09:30:47Z"), Some("2016-10-17T09:30:47Z"), Some("2016-11-17T09:30:47Z"), Some("2016-12-17T09:30:47Z")))
  val newChangeIndicator = ChangeIndicators(true, true, true, false, false)
  val newExtraFields = ExtraFields(DesConstants.testDeclaration, DesConstants.testFilingIndividual, newEtmpField)

  val updateAmendVariationRequest = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    newChangeIndicator,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
      None)),
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpAPI6),
    newExtraFields
  )

}

class AmendVariationRequestSpecWithRelease7 extends PlaySpec with OneAppPerSuite {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  val release7BusinessActivities = DesConstants.testBusinessActivities.copy(
    all = Some(DesConstants.testBusinessActivitiesAll.copy(
      businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
    ))
  )

  "SubscriptionRequest serialisation" when {
    "businessReferencesAllButSp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          AmendVariationRequest(
            acknowledgementReference = ackref.ackRef,
            DesConstants.testChangeIndicators,
            "Amendment",
            DesConstants.testBusinessDetails,
            DesConstants.testViewBusinessContactDetails,
            DesConstants.testBusinessReferencesAll,
            None,
            Some(DesConstants.testBusinessReferencesCbUbLlp),
            release7BusinessActivities,
            DesConstants.testTradingPremisesAPI6,
            DesConstants.testBankDetails,
            Some(DesConstants.testMsb),
            Some(DesConstants.testHvd),
            Some(DesConstants.testAsp),
            Some(DesConstants.testAspOrTcsp),
            Some(DesConstants.testTcspAll),
            Some(DesConstants.testTcspTrustCompFormationAgt),
            Some(DesConstants.testEabAll),
            Some(DesConstants.testEabResdEstAgncy),
            Some(DesConstants.testResponsiblePersonsForRpAPI6),
            DesConstants.extraFields
          )
        ) \ "businessReferencesAllButSp") must be (a[JsUndefined])
      }
    }

    "businessReferencesCbUbLlp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          AmendVariationRequest(
            acknowledgementReference = ackref.ackRef,
            DesConstants.testChangeIndicators,
            "Amendment",
            DesConstants.testBusinessDetails,
            DesConstants.testViewBusinessContactDetails,
            DesConstants.testBusinessReferencesAll,
            Some(DesConstants.testbusinessReferencesAllButSp),
            None,
            release7BusinessActivities,
            DesConstants.testTradingPremisesAPI6,
            DesConstants.testBankDetails,
            Some(DesConstants.testMsb),
            Some(DesConstants.testHvd),
            Some(DesConstants.testAsp),
            Some(DesConstants.testAspOrTcsp),
            Some(DesConstants.testTcspAll),
            Some(DesConstants.testTcspTrustCompFormationAgt),
            Some(DesConstants.testEabAll),
            Some(DesConstants.testEabResdEstAgncy),
            Some(DesConstants.testResponsiblePersonsForRpAPI6),
            DesConstants.extraFields
          )
        ) \ "businessReferencesCbUbLlp") must be (a[JsUndefined])
      }
    }
  }


  "AmendVariationRequest" should {
    "de serialise the subscription json" when {
      "given valid json" in {
        val json = Json.toJson(convertedDesModelRelease7)
        val amendVariationRequestModel = convertedDesModelRelease7

        json.as[AmendVariationRequest] must be(amendVariationRequestModel)

        AmendVariationRequest.jsonReads.reads(
          AmendVariationRequest.jsonWrites.writes(convertedDesModelRelease7)) must
          be(JsSuccess(convertedDesModelRelease7))

        Json.toJson(convertedDesModelRelease7) must be(json)
      }
    }


    "convert frontend model to des model for amendment" in {
      implicit val mt = Amendment
      AmendVariationRequest.convert(feSubscriptionReq) must be(convertedDesModelRelease7.copy(amlsMessageType = "Amendment"))
    }

    "convert frontend model to des model for variation" in {
      implicit val mt = Variation
      AmendVariationRequest.convert(feSubscriptionReq) must be(convertedDesModelRelease7.copy(amlsMessageType = "Variation"))
    }

    "update extra field with data from view model[API 5]" in {

      val aboutYou = Aboutyou(Some(IndividualDetails("FirstName", Some("MiddleName"), "LastName")),
        true, Some("Beneficial Shareholder"), None, Some("Other"), None)

      val extraField = ExtraFields(Declaration(true), aboutYou, None)
      val newEtmpField = Some(EtmpFields(Some("2016-09-17T09:30:47Z"), Some("2016-10-17T09:30:47Z"), Some("2016-11-17T09:30:47Z"), Some("2016-12-17T09:30:47Z")))

      val updatedModel = ExtraFields(Declaration(true), aboutYou, newEtmpField)

      convertedDesModelRelease7.extraFields must be(extraField)

      convertedDesModelRelease7.extraFields.setEtmpFields(newEtmpField) must be(updatedModel)

    }

    "update ChangeIndicator  and extra Fields with data from view model[API 5]" in {

      val des = convertedDesModel.setChangeIndicator(newChangeIndicator)
      val updatedExtraFields = des.extraFields.setEtmpFields(newEtmpField)
      des.setExtraFields(updatedExtraFields) must be(updateAmendVariationRequest)

    }
  }

  val feSubscriptionReq = {
    import models.fe.SubscriptionRequest
    SubscriptionRequest(
      BusinessMatchingSection.modelForView,
      EabSection.modelForView,
      TradingPremisesSection.modelForView,
      AboutTheBusinessSection.modelForView,
      BankDetailsSection.modelForView,
      AboutYouSection.modelforView,
      BusinessActivitiesSection.modelForView,
      ResponsiblePeopleSection.modelForView,
      ASPTCSPSection.TcspModelForView,
      ASPTCSPSection.AspModelForView,
      MsbSection.modelForView,
      HvdSection.modelForView,
      SupervisionSection.modelForView
    )
  }

  val convertedDesModel = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
      None)),
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpAPI6),
    DesConstants.extraFields
  )
  val convertedDesModelRelease7 = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
      None)),
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    release7BusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpAPI6),
    DesConstants.extraFields
  )
  val newEtmpField = Some(EtmpFields(Some("2016-09-17T09:30:47Z"), Some("2016-10-17T09:30:47Z"), Some("2016-11-17T09:30:47Z"), Some("2016-12-17T09:30:47Z")))
  val newChangeIndicator = ChangeIndicators(true, true, true, false, false)
  val newExtraFields = ExtraFields(DesConstants.testDeclaration, DesConstants.testFilingIndividual, newEtmpField)

  val updateAmendVariationRequest = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    newChangeIndicator,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
      None)),
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpAPI6),
    newExtraFields
  )

}


