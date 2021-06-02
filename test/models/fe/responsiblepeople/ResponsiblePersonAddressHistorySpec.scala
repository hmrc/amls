/*
 * Copyright 2021 HM Revenue & Customs
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

import models.des.DesConstants
import models.des.responsiblepeople.{SoleProprietor => DesSoleProprietor, _}
import models.fe.responsiblepeople.TimeAtAddress.{OneToThreeYears, SixToElevenMonths, ThreeYearsPlus, ZeroToFiveMonths}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class ResponsiblePersonAddressHistorySpec extends PlaySpec with MockitoSugar {




  "ResponsiblePersonAddressHistory" must {

    "update the model with current address" in {
      val updated = DefaultAddressHistory.currentAddress(NewCurrentAddress)
      updated.currentAddress must be(Some(NewCurrentAddress))
    }

    "update the model with new additionalAddress" in {
      val updated = DefaultAddressHistory.additionalAddress(NewAdditionalAddress)
      updated.additionalAddress must be(Some(NewAdditionalAddress))
    }

    "update the model with new additionalExtraAddress" in {
      val updated = DefaultAddressHistory.additionalExtraAddress(NewAdditionalExtraAddress)
      updated.additionalExtraAddress must be(Some(NewAdditionalExtraAddress))
    }

    "validate complete json" must {

      val completeJson = Json.obj(
        "currentAddress" -> Json.obj(
          "personAddress" -> Json.obj(
            "personAddressLine1" -> "Line 1",
            "personAddressLine2" -> "Line 2",
            "personAddressPostCode" -> "AA1 1AA"
          ),
          "timeAtAddress" -> Json.obj(
            "timeAtAddress" -> "01"
          )
        ),
        "additionalAddress" -> Json.obj(
          "personAddress" -> Json.obj(
            "personAddressLine1" -> "Line 1",
            "personAddressLine2" -> "Line 2",
            "personAddressCountry" -> "ES"
          ),
          "timeAtAddress" -> Json.obj(
            "timeAtAddress" -> "02"
          )
        ),
        "additionalExtraAddress" -> Json.obj(
          "personAddress" -> Json.obj(
            "personAddressLine1" -> "Line 1",
            "personAddressLine2" -> "Line 2",
            "personAddressPostCode" -> "NE1234"
          ),
          "timeAtAddress" -> Json.obj(
            "timeAtAddress" -> "03"
          )
        ))

      "Serialise as expected" in {
        Json.toJson(DefaultAddressHistory) must be(completeJson)
      }

      "Deserialise as expected" in {
        completeJson.as[ResponsiblePersonAddressHistory] must be(DefaultAddressHistory)
      }

    }
  }

  "Merge with existing model" when {

    lazy val initial = ResponsiblePersonAddressHistory(
      Some(DefaultCurrentAddress),
      Some(DefaultAdditionalAddress),
      Some(DefaultAdditionalExtraAddress))

    "Merged with add person" must {
      "return ResponsiblePeople with correct add person" in {

        val result = initial.currentAddress(NewCurrentAddress)

        result must be(ResponsiblePersonAddressHistory(Some(NewCurrentAddress), Some(DefaultAdditionalAddress), Some(DefaultAdditionalExtraAddress)))
      }
    }

    "Merged with DefaultPersonResidenceType" must {
      "return ResponsiblePeople with correct DefaultPersonResidenceType" in {
        val result = initial.additionalAddress(NewAdditionalAddress)
        result must be(ResponsiblePersonAddressHistory(Some(DefaultCurrentAddress), Some(NewAdditionalAddress), Some(DefaultAdditionalExtraAddress)))
      }
    }

    "Merged with DefaultPreviousHomeAddress" must {
      "return ResponsiblePeople with correct DefaultPreviousHomeAddress" in {
        val result = initial.additionalExtraAddress(NewAdditionalExtraAddress)
        result must be(ResponsiblePersonAddressHistory(Some(DefaultCurrentAddress), Some(DefaultAdditionalAddress), Some(NewAdditionalExtraAddress)))
      }
    }
  }

  "current address:convert des model to frontend model for a UK address" in {

    val convertedModel = ResponsiblePersonAddressHistory(Some(ResponsiblePersonAddress(
      PersonAddressUK("CurrentAddressLine1", "CurrentAddressLine2",
        Some("CurrentAddressLine3"), Some("CurrentAddressLine4"),
        "AA1 1AA"), ThreeYearsPlus)), None, None)
    ResponsiblePersonAddressHistory.conv(responsiblePersonsCurrent) must be(Some(convertedModel))
  }
  "current address:convert des model to frontend model for a non-UK address" in {

    val convertedModel = ResponsiblePersonAddressHistory(Some(ResponsiblePersonAddress(
      PersonAddressNonUK("CurrentAddressLine1", "CurrentAddressLine2",
        Some("CurrentAddressLine3"), Some("CurrentAddressLine4"),
        "AD"), ThreeYearsPlus)), None, None)
    ResponsiblePersonAddressHistory.conv(responsiblePersonsCurrentNonUK) must be(Some(convertedModel))
  }

  "additional address:convert des model to frontend model" in {

    val convertedModel = ResponsiblePersonAddressHistory(Some(ResponsiblePersonAddress(
      PersonAddressUK("CurrentAddressLine1", "CurrentAddressLine2",
        Some("CurrentAddressLine3"), Some("CurrentAddressLine4"),
        "AA1 1AA"), SixToElevenMonths)), Some(ResponsiblePersonAddress(
      PersonAddressUK("AdditionalAddressLine1", "AdditionalAddressLine2",
        Some("AdditionalAddressLine3"), Some("AdditionalAddressLine4"),
        "AdditionalAddressPostcode"), ZeroToFiveMonths)), Some(ResponsiblePersonAddress(
      PersonAddressNonUK("AdditionalExtraAddressLine1", "AdditionalExtraAddressLine2",
        Some("AdditionalExtraAddressLine3"), Some("AdditionalExtraAddressLine4"),
        "AD"), OneToThreeYears)))

    ResponsiblePersonAddressHistory.conv(responsiblePersonsExtra) must be(Some(convertedModel))
  }

  "convert des model to frontend model when input is none" in {

    ResponsiblePersonAddressHistory.conv(responsiblePersonsNone) must be(None)
  }

  val DefaultCurrentAddress = ResponsiblePersonCurrentAddress(PersonAddressUK("Line 1", "Line 2", None, None, "AA1 1AA"), ZeroToFiveMonths)
  val DefaultAdditionalAddress = ResponsiblePersonAddress(PersonAddressNonUK("Line 1", "Line 2", None, None, "ES"), SixToElevenMonths)
  val DefaultAdditionalExtraAddress = ResponsiblePersonAddress(PersonAddressUK("Line 1", "Line 2", None, None, "NE1234"), OneToThreeYears)

  val NewCurrentAddress = ResponsiblePersonCurrentAddress(PersonAddressNonUK("Line 1", "Line 2", None, None, "ES"), ZeroToFiveMonths, None)
  val NewAdditionalAddress = ResponsiblePersonAddress(PersonAddressNonUK("Line 1", "Line 2", None, None, "FR"), ZeroToFiveMonths)
  val NewAdditionalExtraAddress = ResponsiblePersonAddress(PersonAddressNonUK("Line 1", "Line 2", None, None, "UK"), SixToElevenMonths)

  val currentAddressDetails = CurrentAddress(
    AddressWithChangeDate(
      "CurrentAddressLine1",
      "CurrentAddressLine2",
      Some("CurrentAddressLine3"),
      Some("CurrentAddressLine4"),
      "AD",
      Some("AA1 1AA")
    )
  )
  val currentAddressDetailsNonUK = CurrentAddress(
    AddressWithChangeDate(
      "CurrentAddressLine1",
      "CurrentAddressLine2",
      Some("CurrentAddressLine3"),
      Some("CurrentAddressLine4"),
      "AD",
      None
    )
  )

  val AdditionalAddressDetails = AddressUnderThreeYears(
    Address(
      "AdditionalAddressLine1",
      "AdditionalAddressLine2",
      Some("AdditionalAddressLine3"),
      Some("AdditionalAddressLine4"),
      "AD",
      Some("AdditionalAddressPostcode")
    )
  )

  val AdditionalExtraAddressDetails = AddressUnderThreeYears(
    Address(
      "AdditionalExtraAddressLine1",
      "AdditionalExtraAddressLine2",
      Some("AdditionalExtraAddressLine3"),
      Some("AdditionalExtraAddressLine4"),
      "AD",
      None
    )
  )

  val responsiblePersonsCurrentNonUK = ResponsiblePersons(
    Some(DesConstants.nameDetails),
    Some(DesConstants.nationalityDetails),
    None,
    Some(currentAddressDetailsNonUK),
    Some("3+ years"),
    None,
    None,
    None,
    None,
    Some(PositionInBusiness(
      Some(DesSoleProprietor(true, true)),
      Some(Partnership(true, true)),
      Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true))
    )),
    Some(DesConstants.regDetails),
    true,
    Some("DescOfPrevExperience"),
    true,
    Some("TrainingDetails"),
    None,
    None,
    Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )

  val responsiblePersonsCurrent = ResponsiblePersons(
    Some(DesConstants.nameDetails),
    Some(DesConstants.nationalityDetails),
    None,
    Some(currentAddressDetails),
    Some("3+ years"),
    None,
    None,
    None,
    None,
    Some(PositionInBusiness(
      Some(DesSoleProprietor(true, true)),
      Some(Partnership(true, true)),
      Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true))
    )),
    Some(DesConstants.regDetails),
    true,
    Some("DescOfPrevExperience"),
    true,
    Some("TrainingDetails"),
    None,
    None,
    Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )

  val responsiblePersonsAdditional = ResponsiblePersons(
    Some(DesConstants.nameDetails),
    Some(DesConstants.nationalityDetails),
    None,
    Some(currentAddressDetails),
    Some("7-12 months"),
    Some(AdditionalAddressDetails),
    Some("1-3 years"),
    None,
    None,
    Some(PositionInBusiness(
      Some(DesSoleProprietor(true, true)),
      Some(Partnership(true, true)),
      Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true))
    )),
    Some(DesConstants.regDetails),
    true,
    Some("DescOfPrevExperience"),
    true,
    Some("TrainingDetails"),
    None,
    None,
    Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )

  val responsiblePersonsExtra = ResponsiblePersons(
    Some(DesConstants.nameDetails),
    Some(DesConstants.nationalityDetails),
    None,
    Some(currentAddressDetails),
    Some("7-12 months"),
    Some(AdditionalAddressDetails),
    Some("0-6 months"),
    Some(AdditionalExtraAddressDetails),
    Some("1-3 years"),
    Some(PositionInBusiness(
      Some(DesSoleProprietor(true, true)),
      Some(Partnership(true, true)),
      Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true))
    )),
    Some(DesConstants.regDetails),
    true,
    Some("DescOfPrevExperience"),
    true,
    Some("TrainingDetails"),
    None,
    None,
    Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )

  val responsiblePersonsNone = ResponsiblePersons(
    Some(DesConstants.nameDetails),
    Some(DesConstants.nationalityDetails),
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    Some(PositionInBusiness(
      Some(DesSoleProprietor(true, true)),
      Some(Partnership(true, true)),
      Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true))
    )),
    Some(DesConstants.regDetails),
    true,
    Some("DescOfPrevExperience"),
    true,
    Some("TrainingDetails"),
    None,
    None,
    Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )

  val DefaultAddressHistory = ResponsiblePersonAddressHistory(
    currentAddress = Some(DefaultCurrentAddress),
    additionalAddress = Some(DefaultAdditionalAddress),
    additionalExtraAddress = Some(DefaultAdditionalExtraAddress)
  )

  val NewAddressHistory = ResponsiblePersonAddressHistory(
    currentAddress = Some(NewCurrentAddress),
    additionalAddress = Some(NewAdditionalAddress),
    additionalExtraAddress = Some(NewAdditionalExtraAddress)
  )

}
