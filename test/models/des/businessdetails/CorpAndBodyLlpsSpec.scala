/*
 * Copyright 2024 HM Revenue & Customs
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

package models.des.businessdetails

import models.fe.businesscustomer.{Address, ReviewDetails}
import models.fe.businessmatching.BusinessType.SoleProprietor
import models.fe.businessmatching.{BusinessActivities, BusinessMatching, CompanyRegistrationNumber}
import org.scalatestplus.play.PlaySpec

class CorpAndBodyLlpsSpec extends PlaySpec {

  "BusinessMatching with review details and company registration number" must {

    "be convertible to CorpAndBodyLlps DES record" in {

      val reviewDetails = ReviewDetails("businessName", SoleProprietor, Address("line_1", Some("line_2"), None, None, None, "UK"), "safeId")

      val companyRegistrationNumber = CompanyRegistrationNumber("123456789")

      val businessMatching = BusinessMatching(reviewDetails, BusinessActivities(Set.empty), None, None, Some(companyRegistrationNumber))

      CorpAndBodyLlps.convert(businessMatching) must be(Some(CorpAndBodyLlps("businessName", "123456789")))

    }
  }
}
