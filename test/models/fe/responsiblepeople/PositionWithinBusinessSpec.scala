/*
 * Copyright 2022 HM Revenue & Customs
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

import models.des.responsiblepeople.{CorpBodyOrUnInCorpBodyOrLlp, Partnership, PositionInBusiness, RPExtra, ResponsiblePersons, SoleProprietor => DesSoleProprietor}
import org.joda.time.LocalDate
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class PositionWithinBusinessSpec extends PlaySpec with MockitoSugar {

  private val today: LocalDate = new LocalDate()
  "JSON validation" must {

    "successfully validate given a BeneficialOwner value" in {
      Json.fromJson[PositionWithinBusiness](JsString("01")) must
        be(JsSuccess(BeneficialOwner))
    }

    "successfully validate given a Director value" in {
      Json.fromJson[PositionWithinBusiness](JsString("02")) must
        be(JsSuccess(Director))
    }

    "successfully validate given a InternalAccountant value" in {
      Json.fromJson[PositionWithinBusiness](JsString("03")) must
        be(JsSuccess(InternalAccountant))
    }

    "successfully validate given a NominatedOfficer value" in {
      Json.fromJson[PositionWithinBusiness](JsString("04")) must
        be(JsSuccess(NominatedOfficer))
    }

    "successfully validate given a Partner value" in {
      Json.fromJson[PositionWithinBusiness](JsString("05")) must
        be(JsSuccess(Partner))
    }

    "successfully validate given a SoleProprietor value" in {
      Json.fromJson[PositionWithinBusiness](JsString("06")) must
        be(JsSuccess(SoleProprietor))
    }

    "successfully validate given an Other value" in {
      Json.fromJson[PositionWithinBusiness](Json.obj("other" -> "some other role")) mustBe JsSuccess(Other("some other role"))
    }

    "fail to validate when given an empty value" in {
      Json.fromJson[PositionWithinBusiness](JsString("")) must
        be(JsError((JsPath \ "positions") -> JsonValidationError("error.invalid")))
    }

    "write the correct value for BeneficialOwner" in {
      Json.toJson(BeneficialOwner: PositionWithinBusiness) must be(JsString("01"))
    }

    "write the correct value for Director" in {
      Json.toJson(Director: PositionWithinBusiness) must be(JsString("02"))
    }

    "write the correct value for InternalAccountant" in {
      Json.toJson(InternalAccountant: PositionWithinBusiness) must be(JsString("03"))
    }

    "write the correct value for NominatedOfficer" in {
      Json.toJson(NominatedOfficer: PositionWithinBusiness) must be(JsString("04"))
    }

    "write the correct value for Partner" in {
      Json.toJson(Partner: PositionWithinBusiness) must be(JsString("05"))
    }

    "write the correct value for SoleProprietor" in {
      Json.toJson(SoleProprietor: PositionWithinBusiness) must be(JsString("06"))
    }

    "write the correct value for Other" in {
      Json.toJson(Other("some other role"): PositionWithinBusiness) mustBe Json.obj("other" -> "some other role")
    }

    "convert des model to frontend model successfully" in {

      val position = Some(PositionInBusiness(
        Some(DesSoleProprietor(true, true, Some(false), Some("texty text text"))),
        Some(Partnership(true, true)),
        Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true, Some(true)))
      ))

      val desModel = ResponsiblePersons(None,None,None,None,None,None,None,None,None,position,None,false,None,false,None,Some(today.toString()),Some(false),None,extra = RPExtra())

      Positions.conv(desModel) must be(Some(Positions(Set(Partner, SoleProprietor, NominatedOfficer, Director, BeneficialOwner, DesignatedMember), Some(today))))
    }

    "convert des model to frontend model successfully with other details" in {

      val positions = Seq(
        PositionInBusiness(Some(DesSoleProprietor(true, false, Some(true), Some("another sp role"))), None, None),
        PositionInBusiness(None, Some(Partnership(true, false, Some(true), Some("another partnership role"))), None),
        PositionInBusiness(None, None, Some(CorpBodyOrUnInCorpBodyOrLlp(false, true, false, None, Some(true), Some("another corp role"))))
      )

      val expectedResults = Seq(
        Positions(Set(SoleProprietor, Other("another sp role")), Some(today)),
        Positions(Set(Partner, Other("another partnership role")), Some(today)),
        Positions(Set(BeneficialOwner, Other("another corp role")), Some(today))
      )

      positions.zip(expectedResults) foreach {
        case (pos, result) =>
          //noinspection ScalaStyle
          val desModel = ResponsiblePersons(None,None,None,None,None,None,None,None,None,Some(pos),None,false,None,false,None,Some(today.toString()),Some(false),None,extra = RPExtra())
          Positions.conv(desModel) mustBe Some(result)
      }
    }

    "convert des model to frontend model successfully when user has no data selected" in {
      val position = Some(PositionInBusiness(
        Some(DesSoleProprietor(false, false)),
        Some(Partnership(false, false)),
        Some(CorpBodyOrUnInCorpBodyOrLlp(false, false, false))
      ))

      val desModel = ResponsiblePersons(None,None,None,None,None,None,None,None,None,position,None,false,None,false,None,None,Some(false),None,extra = RPExtra())

      Positions.conv(desModel) must be(None)
    }

    "convert des model to frontend model successfully when input is none" in {
      Positions.conv(None) must be(None)
    }
  }

}
