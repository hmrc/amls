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

package models.des.aboutthebusiness

import models.fe.businessdetails._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class PreviouslyRegisteredSpec extends PlaySpec{

    "PreviouslyRegistered" must {

      val Contact = ContactingYou("123456789", "afa@test.com")
      val Office = RegisteredOfficeUK("1", "2", None, None, "NE3 0QQ")

      "serialise PreviouslyRegistered model with option yes" in {
        val mlr = PreviouslyRegisteredMLR(true, Some("12345678"), false, None)
        PreviouslyRegisteredMLR.format.writes(mlr) must be(Json.obj("amlsRegistered"->true,
          "mlrRegNumber8Long" -> "12345678", "prevRegForMlr" -> false))
      }

      "serialise PreviouslyRegistered model with option no" in {
        val mlr = PreviouslyRegisteredMLR(false, None, true, Some("123456789123654"))
        PreviouslyRegisteredMLR.format.writes(mlr) must be(Json.obj("amlsRegistered"->false,
         "prevRegForMlr" -> true,
         "prevMlrRegNumber" -> "123456789123654"))
      }

      "convert front end model to PreviouslyRegisteredMLRYes8" in {
        val from = BusinessDetails(PreviouslyRegisteredYes(Some("12345678")), None, Some(VATRegisteredYes("12345678")), None, Contact, Office, false)

        PreviouslyRegisteredMLR.convert(from) must be (Some(PreviouslyRegisteredMLR(true, Some("12345678"), false, None)))
      }

      "convert front end model to PreviouslyRegisteredMLR15" in {
        val from = BusinessDetails(PreviouslyRegisteredYes(Some("123456789123456")), None, Some(VATRegisteredYes("12345678")), None, Contact, Office, false)

        PreviouslyRegisteredMLR.convert(from) must be (Some(PreviouslyRegisteredMLR(false, None, true, Some("123456789123456"))))
      }

      "convert front end model to PreviouslyRegisteredNo" in {
        val from = BusinessDetails(PreviouslyRegisteredNo, None, Some(VATRegisteredYes("12345678")), None, Contact, Office, false)

        PreviouslyRegisteredMLR.convert(from) must be (Some(PreviouslyRegisteredMLR(false,None,false,None)))
      }
    }
}
