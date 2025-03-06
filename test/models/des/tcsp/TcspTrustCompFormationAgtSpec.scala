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

package models.des.tcsp

import models.fe.tcsp._
import org.scalatestplus.play.PlaySpec

class TcspTrustCompFormationAgtSpec extends PlaySpec {

  "TcspTrustCompFormationAgt" should {

    "convert frontend model to Des" in {
      val DefaultCompanyServiceProviders = TcspTypes(
        Set(
          NomineeShareholdersProvider,
          TrusteeProvider,
          CompanyDirectorEtc,
          RegisteredOfficeEtc,
          CompanyFormationAgent
        )
      )

      val TcspSection = Some(
        Tcsp(
          Some(DefaultCompanyServiceProviders),
          Some(OnlyOffTheShelfCompsSoldYes),
          Some(ComplexCorpStructureCreationYes),
          None,
          None
        )
      )

      TcspTrustCompFormationAgt.conv(TcspSection) must be(TcspTrustCompFormationAgt(true, true))
    }

    "convert frontend model to Des when frontend model is none" in {
      val TcspSection = Some(Tcsp(None, None, None, None, None))

      TcspTrustCompFormationAgt.conv(TcspSection) must be(TcspTrustCompFormationAgt(false, false))
    }
  }
}
