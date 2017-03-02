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

package models.fe.estateagentbusiness

import models.des.estateagentbusiness.EabResdEstAgncy
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json._

class RedressSchemeSpec extends PlaySpec with MockitoSugar {

  "RedressScheemsSpec" must {
    "JSON validation" must {
      "successfully validate selecting redress option no" in {

        Json.fromJson[RedressScheme](Json.obj("isRedress"-> false)) must
          be(JsSuccess(RedressSchemedNo))

      }

      "successfully validate json Reads" in {
        Json.fromJson[RedressScheme](Json.obj("isRedress"-> true,"propertyRedressScheme" -> "01")) must
          be(JsSuccess(ThePropertyOmbudsman))

        Json.fromJson[RedressScheme](Json.obj("isRedress"-> true,"propertyRedressScheme" -> "02")) must
          be(JsSuccess(OmbudsmanServices))

        Json.fromJson[RedressScheme](Json.obj("isRedress"-> true,"propertyRedressScheme" -> "03")) must
          be(JsSuccess(PropertyRedressScheme))

        val json = Json.obj("isRedress"-> true,
                            "propertyRedressScheme" -> "04",
                            "propertyRedressSchemeOther" -> "test")

        Json.fromJson[RedressScheme](json) must
          be(JsSuccess(Other("test"), JsPath \ "propertyRedressSchemeOther"))
      }

      "fail to validate when given an empty `other` value" in {

        val json = Json.obj("isRedress"-> true,
                             "propertyRedressScheme" -> "04"
                            )

        Json.fromJson[RedressScheme](json) must
          be(JsError((JsPath \ "propertyRedressSchemeOther") -> ValidationError("error.path.missing")))
      }

      "fail to validate when invalid option is passed" in {

        val json = Json.obj("isRedress"-> true,
          "propertyRedressScheme" -> "10"
        )

        Json.fromJson[RedressScheme](json) must
          be(JsError((JsPath \ "propertyRedressScheme") -> ValidationError("error.invalid")))
      }


      "successfully validate json write" in {

        Json.toJson(ThePropertyOmbudsman) must be(Json.obj("isRedress"-> true, "propertyRedressScheme" -> "01"))

        Json.toJson(OmbudsmanServices) must be(Json.obj("isRedress"-> true, "propertyRedressScheme" -> "02"))

        Json.toJson(PropertyRedressScheme) must be(Json.obj("isRedress"-> true, "propertyRedressScheme" -> "03"))

        val json = Json.obj("isRedress"-> true,
          "propertyRedressScheme" -> "04",
          "propertyRedressSchemeOther" -> "test")

        Json.toJson(Other("test")) must be(json)

        Json.toJson(RedressSchemedNo) must be(Json.obj("isRedress"-> false))
      }
    }

    "convert des model to frontend redress model yes and \"Property Redress Scheme\"" in {
      val desEab = EabResdEstAgncy(true, Some("Property Redress Scheme"), None)
      RedressScheme.conv(Some(desEab)) must be(Some(PropertyRedressScheme))
    }
    "convert des model to frontend redress model yes and \"Other\" and None" in {
      val desEab = EabResdEstAgncy(true, Some("Other"), None)
      RedressScheme.conv(Some(desEab)) must be(Some(Other("")))
    }
    "convert des model to frontend redress model yes and \"Other\" and \"test\"" in {
      val desEab = EabResdEstAgncy(true, Some("Other"), Some("test"))
      RedressScheme.conv(Some(desEab)) must be(Some(Other("test")))
    }
    "convert des model to frontend redress model yes and \"Ombudsman Services\"" in {
      val desEab = EabResdEstAgncy(true, Some("Ombudsman Services"), None)
      RedressScheme.conv(Some(desEab)) must be(Some(OmbudsmanServices))
    }

    "convert des model to frontend redress model no" in {
      val desModel = EabResdEstAgncy(false, None, None)
      RedressScheme.conv(Some(desModel)) must be(Some(RedressSchemedNo))
    }

    "convert des model to frontend redress model no when missing" in {
      val desModel = None
      RedressScheme.conv(desModel) must be(Some(RedressSchemedNo))
    }
  }
}
