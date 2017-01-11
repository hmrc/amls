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

import org.joda.time.{DateTimeZone, DateTime, DateTimeUtils}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec


class FeeResponseSpec extends PlaySpec with MockitoSugar with BeforeAndAfterAll {

  override def beforeAll {
    DateTimeUtils.setCurrentMillisFixed(1000000)
  }

  override def afterAll: Unit = {
    DateTimeUtils.setCurrentMillisSystem()
  }

  "FeeResponse" when {

    "return fee response successfully" when {
      "Amend variation response do not hold any fee details" in {
        val response = AmendVariationResponse(
          processingDate = "2016-09-17T09:30:47Z",
          etmpFormBundleNumber = "111111"
        )

        FeeResponse.convert2(response, "test") must be (FeeResponse(AmendOrVariationResponseType,"test",0,None,
          0,0,None,None, DateTime.now(DateTimeZone.UTC)))
      }
    }

    "return successful fee response for the valid variation response" in {
      val response = AmendVariationResponse(
        processingDate = "2016-09-17T09:30:47Z",
        etmpFormBundleNumber = "111111",
        Some(1301737.96d),
        Some(231.42d),
        Some(870458d),
        Some(2172427.38),
        Some("string"),
        Some(3456.12)
      )

      FeeResponse.convert2(response, "test") must be (FeeResponse(AmendOrVariationResponseType,"test",1301737.96,Some(231.42),
        870458.0,2172427.38,Some("string"),Some(3456.12), DateTime.now(DateTimeZone.UTC)))

    }

  }

}
