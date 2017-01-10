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

package models.fe.responsiblepeople

import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsSuccess, Json}

class PersonAddressSpec extends PlaySpec {

  val DefaultAddressLine1 = "Default Line 1"
  val DefaultAddressLine2 = "Default Line 2"
  val DefaultAddressLine3 = Some("Default Line 3")
  val DefaultAddressLine4 = Some("Default Line 4")
  val DefaultPostcode = "AA1 1AA"
  val DefaultCountry =  "GB"

  val NewAddressLine1 = "New Line 1"
  val NewAddressLine2 = "New Line 2"
  val NewAddressLine3 = Some("New Line 3")
  val NewAddressLine4 = Some("New Line 4")
  val NewPostcode = "BB1 1BB"
  val NewCountry = "AB"

  val DefaultUKAddress = PersonAddressUK(
    DefaultAddressLine1,
    DefaultAddressLine2,
    DefaultAddressLine3,
    DefaultAddressLine4,
    DefaultPostcode)

  val DefaultNonUKAddress = PersonAddressNonUK(
    DefaultAddressLine1,
    DefaultAddressLine2,
    DefaultAddressLine3,
    DefaultAddressLine4,
    DefaultCountry)

  val DefaultUKModel = Map(
    "isUK" -> Seq("true"),
    "addressLine1" -> Seq(DefaultAddressLine1),
    "addressLine2" -> Seq(DefaultAddressLine2),
    "addressLine3" -> Seq("Default Line 3"),
    "addressLine4" -> Seq("Default Line 4"),
    "postCode" -> Seq(DefaultPostcode)
  )

  val DefaultNonUKModel = Map(
    "isUK" -> Seq("false"),
    "addressLineNonUK1" -> Seq(DefaultAddressLine1),
    "addressLineNonUK2" -> Seq(DefaultAddressLine2),
    "addressLineNonUK3" -> Seq("Default Line 3"),
    "addressLineNonUK4" -> Seq("Default Line 4"),
    "country" -> Seq(DefaultCountry)
  )


  val DefaultUKJson = Json.obj(
    "personAddressLine1" -> DefaultAddressLine1,
    "personAddressLine2" -> DefaultAddressLine2,
    "personAddressLine3" -> DefaultAddressLine3,
    "personAddressLine4" -> DefaultAddressLine4,
    "personAddressPostCode" -> DefaultPostcode
  )

  val DefaultNonUKJson = Json.obj(
    "personAddressLine1" -> DefaultAddressLine1,
    "personAddressLine2" -> DefaultAddressLine2,
    "personAddressLine3" -> DefaultAddressLine3,
    "personAddressLine4" -> DefaultAddressLine4,
    "personAddressCountry" -> DefaultCountry
  )

  "personAddress" must {

    "JSON validation" must {

      "Round trip a UK Address correctly through serialisation" in {
        PersonAddress.jsonReads.reads(
          PersonAddress.jsonWrites.writes(DefaultUKAddress)
        ) must be (JsSuccess(DefaultUKAddress))
      }

      "Round trip a Non UK Address correctly through serialisation" in {
        PersonAddress.jsonReads.reads(
          PersonAddress.jsonWrites.writes(DefaultNonUKAddress)
        ) must be (JsSuccess(DefaultNonUKAddress))
      }

      "Serialise UK address as expected" in {
        Json.toJson(DefaultUKAddress) must be(DefaultUKJson)
      }

      "Serialise non-UK address as expected" in {
        Json.toJson(DefaultNonUKAddress) must be(DefaultNonUKJson)
      }

      "Deserialise UK address as expected" in {
        DefaultUKJson.as[PersonAddress] must be(DefaultUKAddress)
      }

      "Deserialise non-UK address as expected" in {
        DefaultNonUKJson.as[PersonAddress] must be(DefaultNonUKAddress)
      }

    }

  }

}
