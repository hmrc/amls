/*
 * Copyright 2024 HM Revenue & Customs
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

package models.fe.businessmatching

import models.des.businessdetails.BusinessDetails
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}
import utils.AmlsBaseSpec

class CompanyRegistrationNumberSpec extends PlaySpec with AmlsBaseSpec {

  "CompanyRegistrationNumber" must {

    "Json validation" must {

      "READ the JSON successfully and return the domain Object" in {
        val companyRegistrationNumber     = CompanyRegistrationNumber("12345678")
        val jsonCompanyRegistrationNumber = Json.obj("companyRegistrationNumber" -> "12345678")
        val fromJson                      = Json.fromJson[CompanyRegistrationNumber](jsonCompanyRegistrationNumber)
        fromJson must be(JsSuccess(companyRegistrationNumber))
      }
    }

    "Convert des model to frontend CompanyRegistrationNumber" in {
      val desBusinessDetails = BusinessDetails(BusinessType.SoleProprietor, None, None)

      CompanyRegistrationNumber.conv(desBusinessDetails) must be(None)
    }
  }
}
