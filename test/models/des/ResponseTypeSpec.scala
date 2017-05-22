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

package models.des

import models.{AmendOrVariationResponseType, ResponseType, SubscriptionResponseType}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsSuccess, Json}

class ResponseTypeSpec extends PlaySpec {

  "A Response type" when {
    "Subscription response" must {
      "serialize/deserialize" in {
        ResponseType.jsonReads.reads(ResponseType.jsonWrites.writes(SubscriptionResponseType)) must be(JsSuccess(SubscriptionResponseType))
      }
    }

    "Amend or Variation response" must {
      "serialize/deserialize" in {
        ResponseType.jsonReads.reads(ResponseType.jsonWrites.writes(AmendOrVariationResponseType)) must be(JsSuccess(AmendOrVariationResponseType))
      }
    }

    "invalid will throw validation error" in {
      ResponseType.jsonReads.reads(Json.obj()) mustBe a[JsError]
    }
  }

}
