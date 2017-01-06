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

package models.des.aboutthebusiness

import models.fe.aboutthebusiness._
import org.scalatestplus.play.PlaySpec

class CorporationTaxRegisteredCbUbLlpSpec extends PlaySpec {

  "CorporationTaxRegisteredCbUbLlp" should {

    "convert correctly for corporate tax Yes" in {
      val regForCorpTax = CorporationTaxRegisteredYes("1234567890")
      val aboutTheBusiness  =  AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
        None,
        None,
        Some(regForCorpTax),
        ContactingYou("019212323222323222323222323222", "abc@test.co.uk"),
        RegisteredOfficeUK("line1", "line2",
          Some("some street"), Some("some city"), "EE1 1EE"),
        None
      )
      CorporationTaxRegisteredCbUbLlp.conv(aboutTheBusiness) must be(Some(CorporationTaxRegisteredCbUbLlp(true, Some("1234567890"))))
    }

    "convert correctly for corporate tax No" in {
      val regForCorpTax = CorporationTaxRegisteredNo
      val aboutTheBusiness  =  AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
        None,
        None,
        Some(regForCorpTax),
        ContactingYou("019212323222323222323222323222", "abc@test.co.uk"),
        RegisteredOfficeUK("line1", "line2",
          Some("some street"), Some("some city"), "EE1 1EE"),
        None
      )
      CorporationTaxRegisteredCbUbLlp.conv(aboutTheBusiness) must be(Some(CorporationTaxRegisteredCbUbLlp(false, None)))
    }

    "convert correctly for corporate tax model is none" in {
      val aboutTheBusiness  =  AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
        None,
        None,
        None,
        ContactingYou("019212323222323222323222323222", "abc@test.co.uk"),
        RegisteredOfficeUK("line1", "line2",
          Some("some street"), Some("some city"), "EE1 1EE"),
        None
      )
      CorporationTaxRegisteredCbUbLlp.conv(aboutTheBusiness) must be(None)
    }
  }
}
