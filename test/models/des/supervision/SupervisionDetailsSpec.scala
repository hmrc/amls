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

class SupervisionDetailsSpec extends PlaySpec {

  "SupervisionDetails" should {

    "convert frontend model to des MemberOfProfessionalBody" in {
      val feModel = Some(AnotherBodyNo)
      SupervisionDetails.conv(feModel) must be(Some(SupervisionDetails(false, None)))

    }

    "return default des model when front end model is empty" in {
      SupervisionDetails.conv(None) must be(None)

    }
  }

}
