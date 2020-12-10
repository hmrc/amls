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

package models.des.responsiblepeople

import models.fe.responsiblepeople.{SoleProprietor => RPSoleProprietor, _}
import models.{BusinessMatchingSection, DefaultDesValues, ResponsiblePeopleSection}
import org.joda.time.LocalDate
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsBoolean, JsString, JsSuccess, Json}

class ResponsiblePersonsSpec extends PlaySpec with GuiceOneAppPerSuite {

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
        approvalFlags = ApprovalFlags(Some(false), Some(true))
      )

      val responsiblePersonPhase2 = ResponsiblePersons.convertResponsiblePeopleToResponsiblePerson(
        respPeoplePhase2,
        BusinessMatchingSection.emptyModel
      )

      responsiblePersonPhase2 must be (RPValues.modelPhase2)
    }

    "REMOVE WHEN FRONTEND IMPLEMENTED FOR PHASE 2 - F&P should return Some(false)" in {

      val respPeoplePhase2 = ResponsiblePeopleSection.model.get.head.copy(
        dateOfBirth = Some(DateOfBirth(LocalDate.parse("1990-02-24"))),
        positions = Some(Positions(Set(RPSoleProprietor, NominatedOfficer), None)),
        approvalFlags = ApprovalFlags(None, Some(true))
      )

      val responsiblePersonPhase2 = ResponsiblePersons.convertResponsiblePeopleToResponsiblePerson(
        respPeoplePhase2,
        BusinessMatchingSection.emptyModel
      )

      responsiblePersonPhase2.passedFitAndProperTest must be (Some(false))
    }

    "REMOVE WHEN FRONTEND IMPLEMENTED FOR PHASE 2 - Approval should return Some(false)" in {

      val respPeoplePhase2 = ResponsiblePeopleSection.model.get.head.copy(
        dateOfBirth = Some(DateOfBirth(LocalDate.parse("1990-02-24"))),
        positions = Some(Positions(Set(RPSoleProprietor, NominatedOfficer), None)),
        approvalFlags = ApprovalFlags(Some(false), None)
      )

      val responsiblePersonPhase2 =

          ResponsiblePersons.convertResponsiblePeopleToResponsiblePerson(
        respPeoplePhase2,
        BusinessMatchingSection.emptyModel
      )

      responsiblePersonPhase2.passedApprovalCheck must be (Some(false))
    }
  }
}

object RPValues {

  val model = DefaultDesValues.ResponsiblePersonsSectionForRelease7.get.head.copy(startDate = None)

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
