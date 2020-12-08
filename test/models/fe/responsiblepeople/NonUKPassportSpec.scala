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

package models.fe.responsiblepeople

import models.des.responsiblepeople._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

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
        be(JsError((JsPath \ "nonUKPassportNumber") -> JsonValidationError("error.path.missing")))
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

    val basicDesModel = ResponsiblePersons(
      nameDetails = None,
      nationalityDetails = None,
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

    "convert from ResponsiblePersons to NonUKPassport - when field passportDetails is Some with passport number Some" in {
      val desModel = basicDesModel.copy(
        nationalityDetails = Some(
          NationalityDetails(
            areYouUkResident = false,
            Some(IdDetail(
              nonUkResident = Some(
                NonUkResident(
                  dateOfBirth = Some(""),
                  passportHeld = true,
                  passportDetails = Some(
                    PassportDetail(ukPassport = false, PassportNum(
                      nonUkPassportNumber = Some("87654321")
                    ))
                  )
                ))
            )),
            countryOfBirth = None,
            nationality = None
          )
        )
      )

      val expectedPassport = Some(NonUKPassportYes("87654321"))
      NonUKPassport.conv(desModel) must be(expectedPassport)
    }

    "convert from ResponsiblePersons to NonUKPassport - when field passportDetails is Some with passport number None" in {
      val desModel = basicDesModel.copy(
        nationalityDetails = Some(
          NationalityDetails(
            areYouUkResident = false,
            Some(IdDetail(
              nonUkResident = Some(
                NonUkResident(
                  dateOfBirth = Some(""),
                  passportHeld = true,
                  passportDetails = Some(
                    PassportDetail(false, PassportNum(
                      nonUkPassportNumber = None
                    ))
                  )
                ))
            )),
            countryOfBirth = None,
            nationality = None
          )
        )
      )

      val expectedPassport = Some(NoPassport)
      NonUKPassport.conv(desModel) must be(expectedPassport)
    }

    "convert from ResponsiblePersons to NonUKPassport - when field passportDetails None" in {
      val desModel = basicDesModel.copy(
        nationalityDetails = Some(
          NationalityDetails(
            areYouUkResident = false,
            idDetails = Some(IdDetail(
              nonUkResident = Some(
                NonUkResident(
                  dateOfBirth = Some(""),
                  passportHeld = false,
                  passportDetails = None
                ))
            )),
            countryOfBirth = None,
            nationality = None
          )
        )
      )

      val expectedPassport = Some(NoPassport)
      NonUKPassport.conv(desModel) must be(expectedPassport)
    }

    "convert from ResponsiblePersons to NonUKPassport - when uk resident" in {
      val desModel = basicDesModel.copy(
        nationalityDetails = Some(
          NationalityDetails(
            areYouUkResident = true,
            idDetails = Some(IdDetail(
              nonUkResident = Some(
                NonUkResident(
                  dateOfBirth = Some(""),
                  passportHeld = false,
                  passportDetails = None
                ))
            )),
            countryOfBirth = None,
            nationality = None
          )
        )
      )

      NonUKPassport.conv(desModel) must be(None)
    }
  }

}
