/*
 * Copyright 2017 HM Revenue & Customs
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

package models.fe.tcsp

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class ProvidedServicesSpec extends PlaySpec with MockitoSugar {

  "Json read and writes" must {

    "Serialise single service as expected" in {
      Json.toJson(ProvidedServices(Set(EmailServer))) must be(Json.obj("services" -> Seq("03")))
    }

    "Serialise multiple services as expected" in {
      val allServices = Set[TcspService](PhonecallHandling, EmailHandling, EmailServer,
        SelfCollectMailboxes, MailForwarding, Receptionist, ConferenceRooms)
      val result = Json.toJson(ProvidedServices(allServices)).\("services").as[Seq[String]]
      result must contain allOf ("01", "02", "03", "04", "05", "06", "07")
    }

    "Serialise 'other' service as expected" in {
      Json.toJson(ProvidedServices(Set(Other("other service")))) must be(Json.obj("services" -> Seq("08"), "details" -> "other service"))
    }

    "Deserialise single service as expected" in {
      val json = Json.obj("services" -> Set("01"))
      val expected = JsSuccess(ProvidedServices(Set(PhonecallHandling)))
      Json.fromJson[ProvidedServices](json) must be (expected)
    }

    "Deserialise multiple service as expected" in {
      val json = Json.obj("services" -> Seq("01", "02", "03", "04", "05", "06", "07"))
      val allServices = Set[TcspService](PhonecallHandling, EmailHandling, EmailServer,
                                         SelfCollectMailboxes, MailForwarding, Receptionist, ConferenceRooms)
      val expected = JsSuccess(ProvidedServices(allServices))
      Json.fromJson[ProvidedServices](json) must be (expected)
    }

    "Deserialise 'other' service as expected" in {
      val json = Json.obj("services" -> Set("08"), "details" -> "other service")
      val expected = JsSuccess(ProvidedServices(Set(Other("other service"))))
      Json.fromJson[ProvidedServices](json) must be (expected)
    }

    "fail when on invalid data" in {
      Json.fromJson[ProvidedServices](Json.obj("services" -> Set("40"))) must
        be(JsError((JsPath \ "services") -> ValidationError("error.invalid")))
    }

    "fail when on missing details data" in {
      Json.fromJson[ProvidedServices](Json.obj("transactions" -> Set("08"))) must
        be(JsError((JsPath \ "services") -> ValidationError("error.path.missing")))
    }

    "fail when on missing all data" in {
      Json.fromJson[ProvidedServices](Json.obj()) must
        be(JsError((JsPath \ "services") -> ValidationError("error.path.missing")))
    }
  }
}
