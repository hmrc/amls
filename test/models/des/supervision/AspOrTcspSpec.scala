/*
 * Copyright 2018 HM Revenue & Customs
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

class AspOrTcspSpec extends PlaySpec {

  "Supervision" should {

    "convert frontend model to des AspOrTcsp" when {
      "given a complete frontend Supervision model" in {

        val frontendModel = Some(
          Supervision(Some(AnotherBodyNo),
            Some(ProfessionalBodyMemberNo),
            Some(BusinessTypes(Set(AccountingTechnicians))),
            Some(ProfessionalBodyNo))
        )

        val desModel = Some(AspOrTcsp(
          Some(SupervisionDetails(false, None)),
          Some(ProfessionalBodyDetails(false, None, Some(ProfessionalBodyDesMember(false, None))))
        ))

        AspOrTcsp.conv(frontendModel) mustBe desModel

      }

      "given an empty frontend Supervision model" in {

        val frontendModel = Some(
          Supervision()
        )

        AspOrTcsp.conv(frontendModel) mustBe Some(AspOrTcsp(None, None))

      }
    }

    "return None" when {
      "front end model is None" in {

        val frontendModel = None

        AspOrTcsp.conv(frontendModel) mustBe None

      }
    }
  }

}
