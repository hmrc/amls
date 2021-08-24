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

package models.fe.asp

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}
import models.des.asp.{Asp => DesAsp}

class OtherBusinessTaxMattersSpec extends PlaySpec with MockitoSugar {

  "Json validation" must {

    "successfully validate given an enum value" in {

      Json.fromJson[OtherBusinessTaxMatters](Json.obj("otherBusinessTaxMatters" -> false)) must
        be(JsSuccess(OtherBusinessTaxMattersNo))
    }

    "successfully validate given an `Yes` value" in {

      Json.fromJson[OtherBusinessTaxMatters](Json.obj("otherBusinessTaxMatters" -> true)) must
        be(JsSuccess(OtherBusinessTaxMattersYes))
    }


    "write the correct value" in {
      Json.toJson(OtherBusinessTaxMattersNo: OtherBusinessTaxMatters) must
        be(Json.obj("otherBusinessTaxMatters" -> false))

      Json.toJson(OtherBusinessTaxMattersYes: OtherBusinessTaxMatters) must
        be(Json.obj("otherBusinessTaxMatters" -> true))
    }
  }

  "conversion" must {
    "return None given desAsp = None" in {
      OtherBusinessTaxMatters.conv(None) must be(None)
    }
    "return OtherBusinessTaxMattersNo given false" in {
      OtherBusinessTaxMatters.conv(Some(DesAsp(false,None))) must be(Some(OtherBusinessTaxMattersNo))
    }
  }

}
