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

import models.fe.eab.{Eab, EabData}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class EabServicesSpec extends PlaySpec  {

  "EabServices " should {
    "Be convertable from front end Estate agent business services" in {
      val from = {
        Eab(
          EabData(
            List("businessTransfer", "developmentCompany", "commercial"),
            None,
            Some("propertyRedressScheme"),
            None,
            true,
            Some("Details"),
            true,
            Some("Details")
          )
        )
      }

      val expected = Some(models.des.businessactivities.EabServices(
        false, true, false, false, true, false, false, true, false, Some(false))
      )

      models.des.businessactivities.EabServices.convert(Some(from)) must be (expected)
    }

    "Be convertable from front end Estate agent business services when none" in {
      val from = {
        Eab(
          EabData(
            List(),
            None,
            Some("propertyRedressScheme"),
            None,
            true,
            Some("Details"),
            true,
            Some("Details")
          )
        )
      }

      val expected = Some(models.des.businessactivities.EabServices(
        false, false, false, false, false, false, false, false, false, Some(false))
      )

      models.des.businessactivities.EabServices.convert(Some(from)) must be (expected)
    }
  }
}
