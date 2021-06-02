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

package models.des.supervision

import models.fe.supervision._
import org.scalatestplus.play.PlaySpec

class MemberOfProfessionalBodySpec extends PlaySpec {

  "MemberOfProfessionalBody" should {

    "convert frontend model to des MemberOfProfessionalBody" in {

      val businessTypes = BusinessTypes(Set(AccountingTechnicians, CharteredCertifiedAccountants, Other("test")))

      MemberOfProfessionalBody.convBusinessTypes(businessTypes) must
        be(Some(MemberOfProfessionalBody(true, true, false, false, false, false, false, false, false, false, false, false, false, true, Some("test"))))
    }

    "return default des model when front end model is empty" in {
      MemberOfProfessionalBody.convBusinessTypes(BusinessTypes(Set.empty)) must
        be(Some(MemberOfProfessionalBody(false, false, false, false, false, false, false, false, false, false, false, false, false, false, None)))
    }

    "convert frontend model to des MemberOfProfessionalBody2" in {
      val feModel = BusinessTypes(Set(
        AccountantsIreland,
        CharteredCertifiedAccountants,
        AssociationOfBookkeepers,
        AccountantsEnglandandWales,
        Bookkeepers,
        AccountingTechnicians,
        TaxationTechnicians,
        InternationalAccountants,
        LawSociety,
        InstituteOfTaxation,
        AccountantsScotland,
        FinancialAccountants,
        ManagementAccountants
      ))

      MemberOfProfessionalBody.convBusinessTypes(feModel) must
        be(Some(MemberOfProfessionalBody(true,true, true,true,true,true,true,true,true,true,true,true,true,false,None)))
    }
  }

}
