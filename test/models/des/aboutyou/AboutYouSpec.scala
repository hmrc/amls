/*
 * Copyright 2023 HM Revenue & Customs
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

    "Convert from new release 7 model to old model" in {

      val individualDetails = Some(IndividualDetails("fName", None, "lName"))

      val employedWithinBusiness = false
      val oldModel = Aboutyou(individualDetails, employedWithinBusiness, Some("Beneficial Shareholder"), None, Some("External Accountant"), None)

      val release7Model = AboutYouRelease7(individualDetails,
        employedWithinBusiness,
        Some(RolesWithinBusiness(beneficialShareholder = true, false, false, false, false, false, false, false, None)),
        Some(RoleForTheBusiness(externalAccountant = true, false, None))
      )

      Aboutyou.convertFromRelease7(release7Model) must be(oldModel)
    }
  }
}

