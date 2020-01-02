/*
 * Copyright 2020 HM Revenue & Customs
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

package models.des.businessActivities

import org.scalatestplus.play.PlaySpec

class EabServicesSpec extends PlaySpec {
  "EabServices " should {
    "Be convertable from front end Estate agent business services" in {
      val from = {
        import models.fe.estateagentbusiness._
        EstateAgentBusiness(services = Some(Services(Set(BusinessTransfer, Development, Commercial))))
      }

      val expected = Some(models.des.businessactivities.EabServices(false, true, false, false, true, false, false, true, false))

      models.des.businessactivities.EabServices.convert(Some(from)) must be (expected)
    }
  }
}
