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

package models.fe.tcsp

import org.scalatestplus.play.PlaySpec
import utils.AmlsBaseSpec

class TcspServiceSpec extends PlaySpec with AmlsBaseSpec {

  "TcspService" should {

    "Provide the correct value" in {
      PhonecallHandling.value must be("01")
      EmailHandling.value must be("02")
      EmailServer.value must be("03")
      SelfCollectMailboxes.value must be("04")
      MailForwarding.value must be("05")
      Receptionist.value must be("06")
      ConferenceRooms.value must be("07")
      Other("").value must be("08")
    }
  }
}
