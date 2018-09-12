/*
 * Copyright 2018 HM Revenue & Customs
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

import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsSuccess, Json}
import play.api.test.FakeApplication

class ResponsiblePersonsSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> false))

  private val nameDtls = Some(NameDetails(PersonName(Some("name"), Some("some"), Some("surname")), Some(OthrNamesOrAliasesDetails(true, Some(Seq("Doc")))),
    Some(PreviousNameDetails(true, Some(PersonName(Some("fname"), Some("mname"), Some("lname"))), Some("1990-02-24")))))
  private val nationalDtls = Some(NationalityDetails(true, Some(IdDetail(Some(UkResident("nino")), None)), Some("GB"), Some("GB")))
  private val contactDtls = Some(ContactCommDetails("test@test.com", "07000001122", None))
  private val currentDesAddress = Some(CurrentAddress(AddressWithChangeDate("ccLine 1", "ccLine 2", None, None, "GB", Some("AA1 1AA"))))
  private val additionalDesAddress = Some(AddressUnderThreeYears(Address("Line 1", "Line 2", None, None, "GB", Some("BB1 1BB"))))
  private val extraAdditional = Some(AddressUnderThreeYears(Address("e Line 1", "e Line 2", Some("e Line 3"), Some("e Line 4"), "GB", Some("CC1 1CC"))))
  private val regDtls = Some(RegDetails(false, None, true, Some("0123456789")))
  private val positionInBusiness = Some(PositionInBusiness(Some(SoleProprietor(true, true)),
    None, None))

  "ResponsiblePersons" should {
    val model = ResponsiblePersons(
      nameDtls,
      nationalDtls,
      contactDtls,
      currentDesAddress,
      Some("0-6 months"),
      additionalDesAddress,
      Some("7-12 months"),
      extraAdditional,
      Some("1-3 years"),
      positionInBusiness,
      regDtls,
      true,
      Some("Some training"),
      true,
      Some("test"),
      None,
      None,
      Some(MsbOrTcsp(true)),
      RPExtra(None)
    )

    val jsonExpectedFromWrite = Json.obj(
      "nameDetails" -> Json.obj(
        "personName" -> Json.obj(
          "firstName" -> "name",
          "middleName" -> "some",
          "lastName" -> "surname"
        ),
        "othrNamesOrAliasesDetails" -> Json.obj(
          "otherNamesOrAliases" -> true,
          "aliases" -> Json.arr(
            "Doc"
          )
        ),
        "previousNameDetails" -> Json.obj(
          "nameEverChanged" -> true,
          "previousName" -> Json.obj(
            "firstName" -> "fname",
            "middleName" -> "mname",
            "lastName" -> "lname"
          ),
          "dateOfChange" -> "1990-02-24"
        )
      ),
      "nationalityDetails" -> Json.obj(
        "areYouUkResident" -> true,
        "idDetails" -> Json.obj(
          "ukResident" -> Json.obj(
            "nino" -> "nino"
          )
        ),
        "countryOfBirth" -> "GB",
        "nationality" -> "GB"
      ),
      "contactCommDetails" -> Json.obj(
        "contactEmailAddress" -> "test@test.com",
        "primaryTeleNo" -> "07000001122"
      ),
      "currentAddressDetails" -> Json.obj(
        "address" -> Json.obj(
          "addressLine1" -> "ccLine 1",
          "addressLine2" -> "ccLine 2",
          "country" -> "GB",
          "postcode" -> "AA1 1AA"
        )
      ),
      "timeAtCurrentAddress" -> "0-6 months",
      "addressUnderThreeYears" -> Json.obj(
        "address" -> Json.obj(
          "addressLine1" -> "Line 1",
          "addressLine2" -> "Line 2",
          "country" -> "GB",
          "postcode" -> "BB1 1BB"
        )
      ),
      "timeAtAddressUnderThreeYears" -> "7-12 months",
      "addressUnderOneYear" -> Json.obj(
        "address" -> Json.obj(
          "addressLine1" -> "e Line 1",
          "addressLine2" -> "e Line 2",
          "addressLine3" -> "e Line 3",
          "addressLine4" -> "e Line 4",
          "country" -> "GB",
          "postcode" -> "CC1 1CC"
        )
      ),
      "timeAtAddressUnderOneYear" -> "1-3 years",
      "positionInBusiness" -> Json.obj(
        "soleProprietor" -> Json.obj(
          "soleProprietor" -> true,
          "nominatedOfficer" -> true
        )),
      "regDetails" -> Json.obj(
        "vatRegistered" -> false,
        "saRegistered" -> true,
        "saUtr" -> "0123456789"
      ),
      "previousExperience" -> true,
      "descOfPrevExperience" -> "Some training",
      "amlAndCounterTerrFinTraining" -> true,
      "trainingDetails" -> "test",
      "msbOrTcsp" -> Json.obj(
        "passedFitAndProperTest" -> true
      )
    )


    "Serialise to json successfully" in {
      ResponsiblePersons.jsonWrites.writes(model) must be(jsonExpectedFromWrite)
    }

    "Deserialise from json successfully" in {

      ResponsiblePersons.jsonReads.reads(jsonExpectedFromWrite) must be (JsSuccess(model))
    }
  }
}

class ResponsiblePersonsPhase2Spec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> true))

  private val nameDtls = Some(NameDetails(PersonName(Some("name"), Some("some"), Some("surname")), Some(OthrNamesOrAliasesDetails(true, Some(Seq("Doc")))),
    Some(PreviousNameDetails(true, Some(PersonName(Some("fname"), Some("mname"), Some("lname"))), Some("1990-02-24")))))
  private val nationalDtls = Some(NationalityDetails(true, Some(IdDetail(Some(UkResident("nino")), None, dateOfBirth = Some("1990-02-24"))), Some("GB"), Some("GB")))
  private val contactDtls = Some(ContactCommDetails("test@test.com", "07000001122", None))
  private val currentDesAddress = Some(CurrentAddress(AddressWithChangeDate("ccLine 1", "ccLine 2", None, None, "GB", Some("AA1 1AA"))))
  private val additionalDesAddress = Some(AddressUnderThreeYears(Address("Line 1", "Line 2", None, None, "GB", Some("BB1 1BB"))))
  private val extraAdditional = Some(AddressUnderThreeYears(Address("e Line 1", "e Line 2", Some("e Line 3"), Some("e Line 4"), "GB", Some("CC1 1CC"))))
  private val regDtls = Some(RegDetails(false, None, true, Some("0123456789")))
  private val positionInBusiness = Some(PositionInBusiness(Some(SoleProprietor(true, true)),
    None, None))

  "ResponsiblePersons" should {
    val model = ResponsiblePersons(
      nameDtls,
      nationalDtls,
      contactDtls,
      currentDesAddress,
      Some("0-6 months"),
      additionalDesAddress,
      Some("7-12 months"),
      extraAdditional,
      Some("1-3 years"),
      positionInBusiness,
      regDtls,
      true,
      Some("Some training"),
      true,
      Some("test"),
      None,
      None,
      Some(MsbOrTcsp(true)),
      RPExtra(None)
    )

    val jsonExpectedFromWrite = Json.obj(
      "nameDetails" -> Json.obj(
        "personName" -> Json.obj(
          "firstName" -> "name",
          "middleName" -> "some",
          "lastName" -> "surname"
        ),
        "othrNamesOrAliasesDetails" -> Json.obj(
          "otherNamesOrAliases" -> true,
          "aliases" -> Json.arr(
            "Doc"
          )
        ),
        "previousNameDetails" -> Json.obj(
          "nameEverChanged" -> true,
          "previousName" -> Json.obj(
            "firstName" -> "fname",
            "middleName" -> "mname",
            "lastName" -> "lname"
          ),
          "dateOfChange" -> "1990-02-24"
        )
      ),
      "nationalityDetails" -> Json.obj(
        "areYouUkResident" -> true,
        "idDetails" -> Json.obj(
          "ukResident" -> Json.obj(
            "nino" -> "nino"
          )
        ),
        "countryOfBirth" -> "GB",
        "nationality" -> "GB"
      ),
      "contactCommDetails" -> Json.obj(
        "contactEmailAddress" -> "test@test.com",
        "primaryTeleNo" -> "07000001122"
      ),
      "currentAddressDetails" -> Json.obj(
        "address" -> Json.obj(
          "addressLine1" -> "ccLine 1",
          "addressLine2" -> "ccLine 2",
          "country" -> "GB",
          "postcode" -> "AA1 1AA"
        )
      ),
      "timeAtCurrentAddress" -> "0-6 months",
      "addressUnderThreeYears" -> Json.obj(
        "address" -> Json.obj(
          "addressLine1" -> "Line 1",
          "addressLine2" -> "Line 2",
          "country" -> "GB",
          "postcode" -> "BB1 1BB"
        )
      ),
      "timeAtAddressUnderThreeYears" -> "7-12 months",
      "addressUnderOneYear" -> Json.obj(
        "address" -> Json.obj(
          "addressLine1" -> "e Line 1",
          "addressLine2" -> "e Line 2",
          "addressLine3" -> "e Line 3",
          "addressLine4" -> "e Line 4",
          "country" -> "GB",
          "postcode" -> "CC1 1CC"
        )
      ),
      "timeAtAddressUnderOneYear" -> "1-3 years",
      "positionInBusiness" -> Json.obj(
        "soleProprietor" -> Json.obj(
          "soleProprietor" -> true,
          "nominatedOfficer" -> true
        )),
      "regDetails" -> Json.obj(
        "vatRegistered" -> false,
        "saRegistered" -> true,
        "saUtr" -> "0123456789"
      ),
      "previousExperience" -> true,
      "descOfPrevExperience" -> "Some training",
      "amlAndCounterTerrFinTraining" -> true,
      "trainingDetails" -> "test",
      "msbOrTcsp" -> Json.obj(
        "passedFitAndProperTest" -> true
      )
    )


    "Serialise to json successfully" in {
      ResponsiblePersons.jsonWrites.writes(model) must be(jsonExpectedFromWrite)
    }

    "Deserialise from json successfully" in {

      ResponsiblePersons.jsonReads.reads(jsonExpectedFromWrite) must be (JsSuccess(model))
    }
  }
}
