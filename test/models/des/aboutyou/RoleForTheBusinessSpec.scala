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

package models.des.aboutyou

import models.fe.declaration.{ExternalAccountant, Other, RoleWithinBusiness}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

/**
  * Created by NicoleAvison on 03/02/2017.
  */
class RoleForTheBusinessSpec extends PlaySpec with MockitoSugar {

  "Des release 7 model" must {
    "convert from frontend model when given an empty set" in {

      val frontendModel = RoleWithinBusiness(Set.empty)

      val desModel = RoleForTheBusiness(
        false, false, None
      )

      RoleForTheBusiness.convertForBusiness(frontendModel) must be(desModel)

    }

    "convert from frontend model when other not present" in {

      val frontendModel = RoleWithinBusiness(Set(ExternalAccountant))

      val desModel = RoleForTheBusiness(
        true, false, None
      )

      RoleForTheBusiness.convertForBusiness(frontendModel) must be(desModel)

    }

    "convert from frontend model when other is present" in {

      val frontendModel = RoleWithinBusiness(Set(ExternalAccountant, Other("Some other text")))

      val desModel = RoleForTheBusiness(
        true, true, Some("Some other text")
      )

      RoleForTheBusiness.convertForBusiness(frontendModel) must be(desModel)

    }
  }
}



