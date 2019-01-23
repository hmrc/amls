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

package models.des.tcsp

import models.fe.tcsp._
import org.scalatestplus.play.PlaySpec

class TcspAllSpec extends PlaySpec {
  "Tcsp All" should {

    "convert frontend model to Des with Yes" in {

      val servicesOfAnother = ServicesOfAnotherTCSPYes("12345678")

      val TcspSection = Some(Tcsp(
        None,
        None,
        Some(servicesOfAnother))
      )
      val tcspAllSection  = TcspAll(true,Some("12345678"))
      TcspAll.conv(TcspSection) must be (tcspAllSection)
    }

    "convert frontend model to Des with No" in {

      val servicesOfAnother = ServicesOfAnotherTCSPNo

      val TcspSection = Some(Tcsp(
        None,
        None,
        Some(servicesOfAnother))
      )
      val tcspAllSection  = TcspAll(false, None)
      TcspAll.conv(TcspSection) must be (tcspAllSection)
    }

    "convert frontend model to Des when frontened model holds no data" in {

      val servicesOfAnother = ServicesOfAnotherTCSPYes("12345678")

      val TcspSection = Some(Tcsp(
        None,
        None,
        None)
      )
      val tcspAllSection  = TcspAll(false, None)
      TcspAll.conv(TcspSection) must be (tcspAllSection)
    }
  }

}
