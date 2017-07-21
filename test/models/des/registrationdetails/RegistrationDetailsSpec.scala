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

package models.des.registrationdetails

import org.joda.time.LocalDate
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsString, JsSuccess, Json}

class RegistrationDetailsSpec extends PlaySpec with MustMatchers {

  "The RegistrationDetails model" when {

    val organisationJson = Json.obj(
      "isAnIndividual" -> false,
      "organisation" -> Json.obj(
        "organisationName" -> "Test Organisation",
        "isAGroup" -> true,
        "organisationType" -> "LLP"
      )
    )

    val organisationModel = RegistrationDetails(isAnIndividual = false, Organisation("Test Organisation", isAGroup = true, LLP))

    val individualJson = Json.obj(
      "isAnIndividual" -> true,
      "individual" -> Json.obj(
        "firstName" -> "Firstname",
        "middleName" -> "Middlename",
        "lastName" -> "Lastname",
        "dateOfBirth" -> "2002-05-10"
      ))

    //noinspection ScalaStyle
    val individualModel = RegistrationDetails(isAnIndividual = true, Individual("Firstname", Some("Middlename"), "Lastname", new LocalDate(2002, 5, 10)))

    "deserialised" must {
      "produce the correct json" when {
        "the data represents an organisation" in {
          Json.fromJson[RegistrationDetails](organisationJson) mustBe JsSuccess(organisationModel)
        }

        "the data represents an individual" in {
          Json.fromJson[RegistrationDetails](individualJson) mustBe JsSuccess(individualModel)
        }
      }
    }

    "serialised" must {
      "produce the correct json" when {
        "the data represents an organisation" in {
          Json.toJson(organisationModel) mustBe organisationJson
        }

        "the data represents an individual" in {
          Json.toJson(individualModel) mustBe individualJson
        }
      }
    }
  }

  "The Organisation model" when {

    val model = Organisation("Test Organisation", true, Partnership)

    val json = Json.obj(
      "organisationName" -> "Test Organisation",
      "isAGroup" -> true,
      "organisationType" -> "Partnership"
    )

    "deserialised" must {
      "produce the correct model" in {
        Json.fromJson[Organisation](json) mustBe JsSuccess(model)
      }
    }

    "serialised" must {
      "produce the correct json" in {
        Json.toJson(model) mustBe json
      }
    }
  }

  "The Individual model" when {
    //noinspection ScalaStyle
    val model = Individual("Firstname", Some("Middlename"), "Lastname", new LocalDate(2002, 5, 10))

    val json = Json.obj(
      "firstName" -> "Firstname",
      "middleName" -> "Middlename",
      "lastName" -> "Lastname",
      "dateOfBirth" -> "2002-05-10"
    )

    "deserialised" must {
      "produce the correct model" in {
        Json.fromJson[Individual](json) mustBe JsSuccess(model)
      }
    }

    "serialised" must {
      "produce the correct json" in {
        Json.toJson(model) mustBe json
      }
    }
  }

  "The organisation type objects" when {

    val types = Seq(
      (Partnership, "Partnership"),
      (LLP, "LLP"),
      (CorporateBody, "Corporate body"),
      (UnincorporatedBody, "Unincorporated body")
    )

    "deserialised" must {
      "produce the correct values" in {
        types foreach {
          case (t, str) => Json.fromJson[OrganisationType](JsString(str)) mustBe JsSuccess(t)
        }
      }
    }

    "serialised" must {
      "write the correct values" in {
        types foreach {
          case (t, str) => Json.toJson(t) mustBe JsString(str)
        }
      }
    }
  }

}
