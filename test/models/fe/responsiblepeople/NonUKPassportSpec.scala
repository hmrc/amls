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

package models.fe.responsiblepeople

import models.des.responsiblepeople._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json._

class NonUKPassportSpec  extends PlaySpec with MockitoSugar {

  "NonUKPassport" should {

    "validate JSON" must {
      "given an enum value" in {

        Json.fromJson[NonUKPassport](Json.obj("nonUKPassport" -> false)) must
          be(JsSuccess(NoPassport))
      }

      "given an `Yes` value" in {

        val json = Json.obj("nonUKPassport" -> true, "nonUKPassportNumber" -> "0123456789")

        Json.fromJson[NonUKPassport](json) must
          be(JsSuccess(NonUKPassportYes("0123456789"), JsPath \ "nonUKPassportNumber"))
      }

    }

    "fail to validate when given an empty `Yes` value" in {

      val json = Json.obj("nonUKPassport" -> true)

      Json.fromJson[NonUKPassport](json) must
        be(JsError((JsPath \ "nonUKPassportNumber") -> ValidationError("error.path.missing")))
    }

    "write the correct value" in {

      Json.toJson(NoPassport) must
        be(Json.obj("nonUKPassport" -> false))

      Json.toJson(NonUKPassportYes("0123456789")) must
        be(Json.obj(
          "nonUKPassport" -> true,
          "nonUKPassportNumber" -> "0123456789"
        ))
    }

    "convert incoming Des Responsible Person to NoUKPassport when responsible person passportHeld field is false" in {
      val personWithNoUkPassport =
        """{
            "lineId": "000004",
            "nameDetails": {
              "personName": {
                "firstName": "harry",
                "lastName": "kancharla"
              }
            },
            "nationalityDetails": {
              "areYouUkResident": false,
              "idDetails": {
              "nonUkResident": {
                "dateOfBirth": "2004-04-04",
                "passportHeld": false
              }
            },
            "countryOfBirth": "AX",
            "nationality": "IE"
            },
            "contactCommDetails": {
              "contactEmailAddress": "test10@api4.com",
              "primaryTeleNo": "0923489765"
            },
            "currentAddressDetails": {
              "address": {
                "addressLine1": "add1",
                "addressLine2": "add1",
                "addressLine3": "add1",
                "addressLine4": "add1",
                "country": "GB",
                "postcode": "de4 5rf"
              }
            },
            "timeAtCurrentAddress": "3+ years",
            "positionInBusiness": {
              "partnership": {
                "partner": true,
                "nominatedOfficer": true,
                "other": false
              }
            },
            "regDetails": {
              "vatRegistered": false,
              "saRegistered": false
            },
            "previousExperience": false,
            "amlAndCounterTerrFinTraining": true,
            "trainingDetails": "TEST10",
            "startDate": "2004-04-04",
            "passedFitAndProperTest": false,
            "passedApprovalCheck": false
          }""".stripMargin

      Json.fromJson[ResponsiblePersons](Json.parse(personWithNoUkPassport)).map(x =>
        NonUKPassport.conv(x) must be(Some(NoPassport))
      )
    }

    "convert incoming des ResponsiblePerson to NonUKPassportYes" in {

      val desModel = ResponsiblePersons(
        nameDetails = None,
        nationalityDetails = Some(
          NationalityDetails(
            areYouUkResident = false,
            idDetails = Some(
              IdDetail(
                nonUkResident = Some(
                  NonUkResident(
                    dateOfBirth = "",
                    passportHeld = true,
                    passportDetails = Some(
                      PassportDetail(
                        false,
                        PassportNum(
                          nonUkPassportNumber = Some("87654321")
                        )
                      )
                    )
                  )
                )
              )
            ),
            countryOfBirth = None,
            nationality = None
          )
        ),
        contactCommDetails = None,
        currentAddressDetails = None,
        timeAtCurrentAddress = None,
        addressUnderThreeYears = None,
        timeAtAddressUnderThreeYears = None,
        addressUnderOneYear = None,
        timeAtAddressUnderOneYear = None,
        positionInBusiness = None,
        regDetails = None,
        previousExperience = false,
        descOfPrevExperience = None,
        amlAndCounterTerrFinTraining = false,
        trainingDetails = None,
        startDate = None,
        dateChangeFlag = None,
        msbOrTcsp = None,
        extra = RPExtra()
      )

      NonUKPassport.conv(desModel) must be(Some(NonUKPassportYes("87654321")))

    }
  }

}
