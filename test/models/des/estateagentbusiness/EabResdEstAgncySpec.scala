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

package models.des.estateagentbusiness

import models.fe.eab.{Eab, EabData}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class EabResdEstAgncySpec extends PlaySpec {
  "EstateAgentBusiness" must {

    val services = List("residential", "commercial", "auctioneering")

    val eabResdEstModel = EabResdEstAgncy(false, None)

    val eab = Eab(
      EabData(
        services,
        None,
        Some("propertyOmbudsman"),
        None,
        true,
        Some("PenaltyDetails"),
        true,
        Some("ProfBodyDetails")
      )
    )

    val eab1 = Eab(
      EabData(
        services,
        None,
        Some("propertyRedressScheme"),
        None,
        true,
        Some("PenaltyDetails"),
        true,
        Some("ProfBodyDetails")
      )
    )

    val eab2 = Eab(
      EabData(
        services,
        None,
        Some("notRegistered"),
        None,
        true,
        Some("PenaltyDetails"),
        true,
        Some("ProfBodyDetails")
      )
    )

    "serialise eabresdestagency model " in {
      EabResdEstAgncy.format.writes(eabResdEstModel) must be(Json.obj("regWithRedressScheme" -> false))
    }

    "successfully convert frontend eab to des model" in {

      EabResdEstAgncy.conv(Some(eab)) must be(Some(EabResdEstAgncy(true, Some("The Property Ombudsman Limited"))))

      EabResdEstAgncy.conv(Some(eab1)) must be(Some(EabResdEstAgncy(true, Some("Property Redress Scheme"))))

      EabResdEstAgncy.conv(Some(eab2)) must be(Some(EabResdEstAgncy(false, None)))

      EabResdEstAgncy.conv(None) must be(None)

    }
  }

}
