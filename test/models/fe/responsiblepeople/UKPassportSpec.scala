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
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class UKPassportSpec  extends PlaySpec with MockitoSugar {

  "UKPassport" should {

    "validate JSON" must {
      "given an enum value" in {

        Json.fromJson[UKPassport](Json.obj("ukPassport" -> false)) must
          be(JsSuccess(UKPassportNo))
      }

      "given an `Yes` value" in {

        val json = Json.obj("ukPassport" -> true, "ukPassportNumber" -> "0123456789")

        Json.fromJson[UKPassport](json) must
          be(JsSuccess(UKPassportYes("0123456789"), JsPath \ "ukPassportNumber"))
      }

    }

    "fail to validate when given an empty `Yes` value" in {

      val json = Json.obj("ukPassport" -> true)

      Json.fromJson[UKPassport](json) must
        be(JsError((JsPath \ "ukPassportNumber") -> ValidationError("error.path.missing")))
    }

    "write the correct value" in {

      Json.toJson(UKPassportNo) must
        be(Json.obj("ukPassport" -> false))

      Json.toJson(UKPassportYes("0123456789")) must
        be(Json.obj(
          "ukPassport" -> true,
          "ukPassportNumber" -> "0123456789"
        ))
    }

    "convert from ResponsiblePersons to UKPassport" in {

      val desModel = ResponsiblePersons(
        None,
        Some(
          NationalityDetails(
            false,
            Some(IdDetail(
              nonUkResident = Some(
                NonUkResident(
                  "",
                  true,
                  Some(
                    PassportDetail(true, PassportNum(Some("87654321")))
                  )
                ))
            )), None, None
          )
        ),
        None,None,None,None,None,None,None,None,None,false,None,false,None,None,None,None,RPExtra()
      )

      UKPassport.conv(desModel) must be(Some(UKPassportYes("87654321")))

    }
  }


}
