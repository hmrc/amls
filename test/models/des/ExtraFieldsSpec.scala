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

package models.des

import models.des.aboutyou.{AboutYouRelease7, IndividualDetails, RoleForTheBusiness, RolesWithinBusiness}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class ExtraFieldsSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  "ExtraFields" must {
    "given release 6 structure of filingIndividual" must {
      "read Json correctly" in {

        val release7FilingIndividualModel = AboutYouRelease7(
          Some(IndividualDetails("fname", None, "lname")),
          false,
          Some(RolesWithinBusiness(false, false, false, false, false, false, false, false, None)),
          Some(RoleForTheBusiness(true, false, None))
        )

        val release6Json = Json.parse(
          """{"filingIndividual": {
    "individualDetails": {
      "firstName": "fname",
      "lastName": "lname"
    },
    "employedWithinBusiness": false,
    "roleForTheBusiness": "External Accountant"
  },
  "declaration": {
    "declarationFlag": true
  }}"""
        )

        ExtraFields.format.reads(release6Json).asOpt.get.filingIndividual must be(release7FilingIndividualModel)

      }
    }

  }
}
