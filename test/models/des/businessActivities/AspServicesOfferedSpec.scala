/*
 * Copyright 2016 HM Revenue & Customs
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

import models.des.businessactivities.AspServicesOffered
import models.fe.asp._
import org.scalatestplus.play.PlaySpec

class AspServicesOfferedSpec extends PlaySpec {

  "AspServicesOffered" should {

    "convert partial front end Asp model to des AspServicesOffered" in {
      val model = Asp(
        Some(ServicesOfBusiness(Set(Accountancy, Auditing, FinancialOrTaxAdvice))),
        None
      )
      AspServicesOffered.conv(Some(model)) must be(Some(AspServicesOffered(true, false, false, true, true)))
    }

    "convert complete front end Asp model to des AspServicesOffered " in {
      val model = Asp(
        Some(ServicesOfBusiness(Set(Accountancy, PayrollServices, BookKeeping, Auditing, FinancialOrTaxAdvice))),
        Some(OtherBusinessTaxMattersYes)
      )
      AspServicesOffered.conv(Some(model)) must be(Some(AspServicesOffered(true, true, true, true, true)))
    }

    "convert when no front model is empty" in {
      val model = Asp(None, None)
      AspServicesOffered.conv(Some(model)) must be(Some(AspServicesOffered(false, false, false, false, false)))
    }
  }

}
