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

import models.ResponsiblePeopleSection
import models.fe.businesscustomer.ReviewDetails
import models.fe.businessmatching.{BusinessActivities, BusinessMatching, BusinessType}
import models.fe.responsiblepeople.TimeAtAddress._
import models.fe.responsiblepeople.{SoleProprietor => RPSoleProprietor, _}
import org.joda.time.LocalDate
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsBoolean, JsString, JsSuccess, Json}
import play.api.test.FakeApplication

class ResponsiblePersonsSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> false))

  "ResponsiblePersons" should {

    "Serialise to json successfully" in {
      ResponsiblePersons.jsonWrites.writes(RPValues.model) must be(RPValues.jsonExpectedFromWrite)
    }

    "Deserialise from json successfully" in {
      ResponsiblePersons.jsonReads.reads(RPValues.jsonExpectedFromWrite) must be (JsSuccess(RPValues.model))
    }

    "convert FE model to DES model" in {

      val respPeople = ResponsiblePeopleSection.model.get.head.copy(
        dateOfBirth = Some(DateOfBirth(LocalDate.parse("1990-02-24"))),
        positions = Some(Positions(Set(RPSoleProprietor, NominatedOfficer), None))
      )

      val responsiblePerson = ResponsiblePersons.convertResponsiblePeopleToResponsiblePerson(
        respPeople,
        RPValues.businessMatching
      )

      responsiblePerson must be (RPValues.model)
    }
  }
}

class ResponsiblePersonsPhase2Spec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> true))

  "ResponsiblePersonsPhase2" should {

    "Serialise to phase2 json successfully" in {
      ResponsiblePersons.jsonWrites.writes(RPValues.modelPhase2) must be(RPValues.jsonExpectedFromWritePhase2)
    }

    "Deserialise from phase2 json successfully" in {
      ResponsiblePersons.jsonReads.reads(RPValues.jsonExpectedFromWritePhase2) must be (JsSuccess(RPValues.modelPhase2))
    }

    "convert FE model to DES model for phase 2" in {

      val respPeoplePhase2 = ResponsiblePeopleSection.model.get.head.copy(
        dateOfBirth = Some(DateOfBirth(LocalDate.parse("1990-02-24"))),
        positions = Some(Positions(Set(RPSoleProprietor, NominatedOfficer), None)),
        hasAlreadyPassedFitAndProper = Some(false),
        hasAlreadyPassedApprovalCheck = Some(true)
      )

      val responsiblePersonPhase2 = ResponsiblePersons.convertResponsiblePeopleToResponsiblePerson(
        respPeoplePhase2,
        RPValues.businessMatching
      )
      val l = RPValues.modelPhase2
      responsiblePersonPhase2 must be (RPValues.modelPhase2)
    }

    "REMOVE WHEN FRONTEND IMPLEMENTED FOR PHASE 2 - F&P should return Some(false)" in {

      val respPeoplePhase2 = ResponsiblePeopleSection.model.get.head.copy(
        dateOfBirth = Some(DateOfBirth(LocalDate.parse("1990-02-24"))),
        positions = Some(Positions(Set(RPSoleProprietor, NominatedOfficer), None)),
        hasAlreadyPassedFitAndProper = None,
        hasAlreadyPassedApprovalCheck = Some(true)
      )

      val responsiblePersonPhase2 = ResponsiblePersons.convertResponsiblePeopleToResponsiblePerson(
        respPeoplePhase2,
        RPValues.businessMatching
      )

      responsiblePersonPhase2.passedFitAndProperTest must be (Some(false))
    }

    "REMOVE WHEN FRONTEND IMPLEMENTED FOR PHASE 2 - Approval should return Some(false)" in {

      val respPeoplePhase2 = ResponsiblePeopleSection.model.get.head.copy(
        dateOfBirth = Some(DateOfBirth(LocalDate.parse("1990-02-24"))),
        positions = Some(Positions(Set(RPSoleProprietor, NominatedOfficer), None)),
        hasAlreadyPassedFitAndProper = Some(false),
        hasAlreadyPassedApprovalCheck = None
      )

      val responsiblePersonPhase2 = ResponsiblePersons.convertResponsiblePeopleToResponsiblePerson(
        respPeoplePhase2,
        RPValues.businessMatching
      )

      responsiblePersonPhase2.passedApprovalCheck must be (Some(false))
    }
  }
}

object RPValues {

  private val nameDtls = Some(NameDetails(PersonName(Some("name"), Some("some"), Some("surname")), Some(OthrNamesOrAliasesDetails(true, Some(Seq("Doc")))),
    Some(PreviousNameDetails(true, Some(PersonName(Some("fname"), Some("mname"), Some("lname"))), Some("1990-02-24")))))
  private val nationalDtls = Some(NationalityDetails(true, Some(IdDetail(Some(UkResident("nino")), None)), Some("GB"), Some("GB")))
  private val contactDtls = Some(ContactCommDetails("test@test.com", "07000001122", None))
  private val currentDesAddress = Some(CurrentAddress(AddressWithChangeDate("ccLine 1", "ccLine 2", None, None, "GB", Some("AA1 1AA"))))
  private val additionalDesAddress = Some(AddressUnderThreeYears(Address("Line 1", "Line 2", None, None, "GB", Some("BB1 1BB"))))
  private val extraAdditional = Some(AddressUnderThreeYears(Address("e Line 1", "e Line 2", Some("e Line 3"), Some("e Line 4"), "GB", Some("CC1 1CC"))))
  private val regDtls = Some(RegDetails(false, None, true, Some("0123456789")))
  private val positionInBusiness = Some(PositionInBusiness(Some(SoleProprietor(true, true, Some(false))),
    None, None))

  val responsiblePeople = ResponsiblePeople(
    Some(models.fe.responsiblepeople.PersonName("name", Some("some"), "surname")),
    Some(PreviousName(true, Some("fname"), Some("mname"), Some("lname"))),
    Some(LocalDate.parse("1990-02-24")),
    Some(KnownBy(true, Some("Doc"))),
    Some(PersonResidenceType(UKResidence("nino"), "GB", "GB")),
    None,
    None,
    None,
    Some(ContactDetails("07000001122", "test@test.com")),
    Some(ResponsiblePersonAddressHistory(
      Some(ResponsiblePersonCurrentAddress(PersonAddressUK("ccLine 1", "ccLine 2", None, None, "AA1 1AA"), ZeroToFiveMonths, None)),
      Some(ResponsiblePersonAddress(PersonAddressUK("Line 1", "Line 2", None, None, "BB1 1BB"),SixToElevenMonths)),
      Some(ResponsiblePersonAddress(PersonAddressUK("e Line 1", "e Line 2", Some("e Line 3"), Some("e Line 4"), "CC1 1CC"), OneToThreeYears)))
    ),
    Some(Positions(Set(models.fe.responsiblepeople.NominatedOfficer, models.fe.responsiblepeople.SoleProprietor), None)),
    Some(SaRegisteredYes("0123456789")),
    Some(VATRegisteredNo),
    Some(ExperienceTrainingYes("Some training")),
    Some(TrainingYes("test")),
    Some(true),
    None,
    None,
    None,
    false
  )

  val businessMatching = BusinessMatching(
    activities = BusinessActivities(Set.empty),
    reviewDetails = ReviewDetails(
      "",
      BusinessType.SoleProprietor,
      models.fe.businesscustomer.Address(
        line_1 = "",
        line_2 = "",
        line_3 = None,
        line_4 = None,
        postcode = None,
        country = ""
      ),
      ""
    )
  )

  val responsiblePeoplePhase2 = responsiblePeople.copy(
    hasAlreadyPassedFitAndProper = Some(false),
    hasAlreadyPassedApprovalCheck = Some(true)
  )

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
    extra = RPExtra(None)
  )

  val modelPhase2 = model.copy(
    msbOrTcsp = None,
    passedFitAndProperTest = Some(false),
    passedApprovalCheck = Some(true),
    nationalityDetails = Some(NationalityDetails(true, Some(IdDetail(Some(UkResident("nino")), None, Some("1990-02-24"))), Some("GB"), Some("GB")))
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
        "nominatedOfficer" -> true,
        "other" -> false
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

  val ukResident = Json.obj(
    "nino" -> "nino"
  )
  val idDetails = Json.obj()
    .+("ukResident", ukResident)
    .+("dateOfBirth", JsString("1990-02-24"))
  val nationalityDetails = Json.obj()
    .+("areYouUkResident", JsBoolean(true))
    .+("idDetails", idDetails)
    .+("countryOfBirth", JsString("GB"))
    .+("nationality", JsString("GB"))

  val jsonExpectedFromWritePhase2 = jsonExpectedFromWrite
    .-("nationalityDetails")
    .+("nationalityDetails", nationalityDetails)
    .-("msbOrTcsp")
    .+("passedFitAndProperTest", JsBoolean(false))
    .+("passedApprovalCheck", JsBoolean(true))
}
