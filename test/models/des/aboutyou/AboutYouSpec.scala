/*
 * Copyright 2016 HM Revenue & Customs
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

package models.des.aboutyou

import models.fe.declaration.{Other, ExternalAccountant, BeneficialShareholder}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class AboutYouSpec extends PlaySpec {
  "AboutYouDetails" must {
    "be serialisable for roleWithinBusiness" in {
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), true, Some("Beneficial Shareholder"))

      Aboutyou.format.writes(aboutyouModel) must be(Json.obj("individualDetails" -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> true, "roleWithinBusiness" -> "Beneficial Shareholder"))
    }

    "be serialisable for roleForTheBusiness" in {
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("External Accountant"))

      Aboutyou.format.writes(aboutyouModel) must be(Json.obj("individualDetails" -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> false, "roleForTheBusiness" -> "External Accountant"))
    }

    "be convertible from FE request to DES for roleWithinBusiness" in {
      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", Some("middlename"), "lName", BeneficialShareholder)
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", Some("middlename"), "lName")), true, Some("Beneficial Shareholder"), None, Some("Other"), None)
      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    }

    "be convertible from FE request to DES for roleForTheBusiness" in {
      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", None, "lName", ExternalAccountant)
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("External Accountant"), None)
      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    }

    "be convertible from FE request to DES for OtherroleWithinBusiness" in {
      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", None, "lName", Other("Agent"))
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("Other"), Some("Agent"))
      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    }
  }
}
