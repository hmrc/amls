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

package models.des.aboutyou

import models.fe.declaration._
import org.scalatestplus.play.PlaySpec

class RolesWithinTheBusinessSpec extends PlaySpec {

  "Des release 7 model" must {
    "convert from frontend model when given an empty set" in {

      val frontendModel = RoleWithinBusiness(Set.empty)

      val desModel = RolesWithinBusiness(false, false, false, false, false, false, false, false, None)

      RolesWithinBusiness.convertWithinBusiness(frontendModel) must be(desModel)

    }

    "convert from frontend model when other not present" in {

      val frontendModel = RoleWithinBusiness(Set(BeneficialShareholder))

      val desModel = RolesWithinBusiness(true, false, false, false, false, false, false, false, None)

      RolesWithinBusiness.convertWithinBusiness(frontendModel) must be(desModel)

    }

    "convert from frontend model when everything is present" in {

      val frontendModel = RoleWithinBusiness(
        Set(
          BeneficialShareholder,
          Director,
          Partner,
          InternalAccountant,
          SoleProprietor,
          NominatedOfficer,
          DesignatedMember,
          Other("Some other text")
        )
      )

      val desModel = RolesWithinBusiness(true, true, true, true, true, true, true, true, Some("Some other text"))

      RolesWithinBusiness.convertWithinBusiness(frontendModel) must be(desModel)

    }
  }

}
