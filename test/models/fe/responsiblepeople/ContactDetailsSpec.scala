/*
 * Copyright 2019 HM Revenue & Customs
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

import models.des.responsiblepeople.ContactCommDetails
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class ContactDetailsSpec extends PlaySpec with MockitoSugar {

  "ContactDetails" should {

    "JSON Read/Write" must {

      "Read the json and return the InKnownByOtherNamesYes domain object successfully" in {

        val json = Json.obj(
          "phoneNumber" -> "07000111222",
          "emailAddress" -> "myname@example.com"
        )

        ContactDetails.formats.reads(json) must
          be(JsSuccess(ContactDetails("07000111222", "myname@example.com")))
      }

      "Write the json successfully from the InKnownByOtherNamesYes domain object created" in {

        val contactDetails = ContactDetails("07000111222", "myname@example.com")

        val json = Json.obj(
          "phoneNumber" -> "07000111222",
          "emailAddress" -> "myname@example.com"
        )

        ContactDetails.formats.writes(contactDetails) must be(json)
      }
    }

    "convert des model to frontend model successfully" in {

      val desModel = ContactCommDetails("adg@gmail.com","123456789",Some("1234567788"))
      ContactDetails.conv(Some(desModel)) must be(Some(ContactDetails("123456789", "adg@gmail.com")))
    }


    "convert des model to frontend model successfully when input is none" in {

      ContactDetails.conv(None) must be(None)
    }
  }
}
