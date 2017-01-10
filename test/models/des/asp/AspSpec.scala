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

package models.des.asp

import models.fe.asp.{Asp => FEAsp,_}
import org.scalatestplus.play.PlaySpec

class AspSpec extends PlaySpec {

  "Asp" should {

    "When given otherBusinessTaxMattersYes convert to frontend model with value true" in {
      val otherBusinessTax = OtherBusinessTaxMattersYes

      val model = FEAsp(
        None,
        Some(otherBusinessTax)
      )
      Asp.conv(model) must be(Asp(true,None))
    }

    "When given otherBusinessTaxMattersNo convert to frontend model with value false" in {
      val otherBusinessTax = OtherBusinessTaxMattersNo

      val model = FEAsp(
        None,
        Some(otherBusinessTax)
      )
      Asp.conv(model) must be(Asp(false, None))
    }
  }
}
