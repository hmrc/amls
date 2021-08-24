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

package models.fe.bankdetails

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class BankAccountTypeSpec extends PlaySpec with MockitoSugar {

  "BankAccountType" must {

    "validate Json read" in {
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" -> "01")) must
        be (JsSuccess(PersonalAccount))
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" -> "02")) must
        be (JsSuccess(BelongsToBusiness))
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" -> "03")) must
        be (JsSuccess(BelongsToOtherBusiness))

    }

    "fail Json read on invalid data" in  {
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" ->"10")) must
        be (JsError(JsPath \ "bankAccountType", JsonValidationError("error.invalid")))
    }

    "write correct Json value" in  {
      Json.toJson(PersonalAccount: BankAccountType) must be (Json.obj("bankAccountType" -> "01"))
      Json.toJson(BelongsToBusiness: BankAccountType) must be (Json.obj("bankAccountType" -> "02"))
      Json.toJson(BelongsToOtherBusiness: BankAccountType) must be (Json.obj("bankAccountType" -> "03"))
    }
  }

}
