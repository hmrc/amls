/*
 * Copyright 2019 HM Revenue & Customs
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
import models.fe.businessmatching.{BusinessType => FE}
import models.des.businessdetails.{BusinessType => DES}
import models.fe.businessmatching.{BusinessActivities, BusinessMatching, CompanyRegistrationNumber, TypeOfBusiness}
import org.scalatestplus.play.PlaySpec

class BusinessDetailsSpec extends PlaySpec {

  "BusinessMatching with review details, type of business and company registration number" must {

    "be convertible to BusinessDetails DES record" in {

      val reviewDetails = ReviewDetails("businessName",
        FE.LPrLLP,
        Address("line_1", "line_2", None, None, None, "UK"),
        "safeId"
      )

      val tp = TypeOfBusiness("LP")

      val companyRegistrationNumber = CompanyRegistrationNumber("123456789")

      val businessMatching = BusinessMatching(
        reviewDetails,
        BusinessActivities(Set.empty),
        None,
        Some(tp),
        Some(companyRegistrationNumber),
        None
      )

      BusinessDetails.convert(businessMatching) must be(
        BusinessDetails(
          DES.LPrLLP,
          Some(CorpAndBodyLlps("businessName", "123456789")),
          Some(UnincorpBody("businessName", "LP")))
      )
    }

    "successfully evaluate equals" in {
      val testBusinessDetails = BusinessDetails(BusinessType.SoleProprietor,
        Some(CorpAndBodyLlps("CompanyName", "12345678")),
        Some(UnincorpBody("CompanyName", "TypeOfBusiness")))

      val testBusinessDetails1 = BusinessDetails(BusinessType.SoleProprietor,
        Some(CorpAndBodyLlps("CompanyName", "12345678")),
        Some(UnincorpBody("CompanyName1", "TypeOfBusiness")))

      testBusinessDetails.equals(testBusinessDetails1) must be(false)
    }

    "successfully evaluate equals1" in {
      val testBusinessDetails = BusinessDetails(BusinessType.SoleProprietor,
        None,
        Some(UnincorpBody("CompanyName", "TypeOfBusiness")))

      val testBusinessDetails1 = BusinessDetails(BusinessType.SoleProprietor,
        Some(CorpAndBodyLlps("CompanyName", "12345678")),
        Some(UnincorpBody("CompanyName1", "TypeOfBusiness")))

      testBusinessDetails.equals(testBusinessDetails1) must be(false)
    }


    "successfully evaluate equals2" in {
      val testBusinessDetails = BusinessDetails(BusinessType.SoleProprietor,
        None,
        Some(UnincorpBody("CompanyName", "TypeOfBusiness")))

      val testBusinessDetails1 = BusinessDetails(BusinessType.SoleProprietor,
        Some(CorpAndBodyLlps("CompanyName", "12345678")),
        None)

      testBusinessDetails.equals(testBusinessDetails1) must be(false)
    }

    "successfully evaluate equals3" in {
      val testBusinessDetails = BusinessDetails(BusinessType.SoleProprietor,
        None,
        None)

      val testBusinessDetails1 = BusinessDetails(BusinessType.SoleProprietor,
        None,
        None)

      testBusinessDetails.equals(testBusinessDetails1) must be(true)
    }

    "successfully evaluate equals4" in {
      val testBusinessDetails = BusinessDetails(BusinessType.LimitedCompany,
        None,
        None)

      val testBusinessDetails1 = BusinessDetails(BusinessType.SoleProprietor,
        None,
        None)

      testBusinessDetails.equals(testBusinessDetails1) must be(false)
    }
  }
}
