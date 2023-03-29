/*
 * Copyright 2023 HM Revenue & Customs
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

class ProfessionalBodyDesMemberSpec extends PlaySpec {


  "ProfessionalBodyDesMember" must {

    "convert front end model to ProfessionalBodyDesMember true" in {
      val from = Supervision(
        professionalBodyMember = Some(ProfessionalBodyMemberYes),
        professionalBodies = Some(BusinessTypes(Set(
          AccountantsIreland,
          CharteredCertifiedAccountants,
          AssociationOfBookkeepers,
          AccountantsScotland,
          FinancialAccountants,
          ManagementAccountants
        )))
      )

      ProfessionalBodyDesMember.conv(from) must
        be(Some(ProfessionalBodyDesMember(true, Some(MemberOfProfessionalBody(false, true, false, false, true, false, false, true, true, false, true, true, false, false, None)))))
    }

    "convert front end model to ProfessionalBodyDesMember false" in {
      val from = Supervision(professionalBodyMember = Some(ProfessionalBodyMemberNo))

      ProfessionalBodyDesMember.conv(from) must be(Some(ProfessionalBodyDesMember(false, None)))
    }
  }
}
