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

package models.fe.responsiblepeople

import models.des.responsiblepeople.{CorpBodyOrUnInCorpBodyOrLlp, Partnership, PositionInBusiness, RPExtra, ResponsiblePersons, SoleProprietor => DesSoleProprietor}
import org.joda.time.LocalDate
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
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

    "fail to validate when given an empty value" in {
      Json.fromJson[PositionWithinBusiness](JsString("")) must
        be(JsError((JsPath \ "positions") -> ValidationError("error.invalid")))
    }

    "write the correct value for BeneficialOwner" in {
      Json.toJson(BeneficialOwner) must be(JsString("01"))
    }

    "write the correct value for Director" in {
      Json.toJson(Director) must be(JsString("02"))
    }

    "write the correct value for InternalAccountant" in {
      Json.toJson(InternalAccountant) must be(JsString("03"))
    }

    "write the correct value for NominatedOfficer" in {
      Json.toJson(NominatedOfficer) must be(JsString("04"))
    }

    "write the correct value for Partner" in {
      Json.toJson(Partner) must be(JsString("05"))
    }

    "write the correct value for SoleProprietor" in {
      Json.toJson(SoleProprietor) must be(JsString("06"))
    }

    "convert des model to frontend model successfully" in {

      val position = Some(PositionInBusiness(
        Some(DesSoleProprietor(true, true)),
        Some(Partnership(true, true)),
        Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true))
      ))

      val desModel = ResponsiblePersons(None,None,None,None,None,None,None,None,None,position,None,false,None,false,None,Some(today.toString()),None,None,RPExtra())


      Positions.conv(desModel) must be(Some(Positions(Set(Partner, SoleProprietor, NominatedOfficer, Director, BeneficialOwner), Some(today))))
    }

    "convert des model to frontend model successfully1" in {
      val position = Some(PositionInBusiness(
        Some(DesSoleProprietor(true, false)),
        Some(Partnership(true, false)),
        Some(CorpBodyOrUnInCorpBodyOrLlp(false, true, false))
      ))

      val desModel = ResponsiblePersons(None,None,None,None,None,None,None,None,None,position,None,false,None,false,None,Some(today.toString()),None,None,RPExtra())


      Positions.conv(desModel) must be(Some(Positions(Set(SoleProprietor, Partner, BeneficialOwner),Some(today))))
    }

    "convert des model to frontend model successfully when user has no data selected" in {
      val position = Some(PositionInBusiness(
        Some(DesSoleProprietor(false, false)),
        Some(Partnership(false, false)),
        Some(CorpBodyOrUnInCorpBodyOrLlp(false, false, false))
      ))

      val desModel = ResponsiblePersons(None,None,None,None,None,None,None,None,None,position,None,false,None,false,None,None,None,None,RPExtra())

      Positions.conv(desModel) must be(None)
    }

    "convert des model to frontend model successfully when input is none" in {
      Positions.conv(None) must be(None)
    }
  }

}
