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

package models.des.aboutthebusiness

import models.fe.businessdetails._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class VATRegistrationSpec extends PlaySpec {

    "VATRegistration" must {

      val Contact = ContactingYou("123456789", "afa@test.com")
      val Office = RegisteredOfficeUK("1", "2", None, None, "NE3 0QQ")

      "serialise VATRegistration model with option yes" in {
        val vat = VATRegistration(true, Some("12345678"))
        VATRegistration.format.writes(vat) must be(Json.obj("vatRegistered" -> true,
          "vrnNumber" -> "12345678"))
      }

      "serialise VATRegistration model with option no" in {
        val vat = VATRegistration(false, None)
        VATRegistration.format.writes(vat) must be(Json.obj("vatRegistered" -> false))
      }

      "convert front end Vat model to VATRegistrationYes" in {
        val from = BusinessDetails(PreviouslyRegisteredNo, None, Some(VATRegisteredYes("12345678")), None, Contact, Office, false)

        VATRegistration.convert(from) must be (Some(VATRegistration(true, Some("12345678"))))
      }

      "convert front end Vat model to VATRegistrationNo" in {
        val from = BusinessDetails(PreviouslyRegisteredNo, None, Some(VATRegisteredNo), None, Contact, Office, false)

        VATRegistration.convert(from) must be (Some(VATRegistration(false, None)))
      }
    }
}
