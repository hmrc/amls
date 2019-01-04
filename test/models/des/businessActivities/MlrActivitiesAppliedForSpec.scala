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

package models.des.businessActivities

import models.des.businessactivities.{MlrActivitiesAppliedFor, AspServicesOffered}
import models.fe.asp._
import models.fe.businesscustomer.{Address, ReviewDetails}
import models.fe.businessmatching.BusinessType.SoleProprietor
import models.fe.businessmatching._
import org.scalatestplus.play.PlaySpec

class MlrActivitiesAppliedForSpec extends PlaySpec {

  "MlrActivitiesAppliedFor" should {

    "convert partial front end Business Matching model to des MlrActivitiesAppliedFor" in {

      val businessActivities = BusinessActivities(Set(MoneyServiceBusiness,
        TrustAndCompanyServices, TelephonePaymentService, EstateAgentBusinessService, BillPaymentServices))
      val businessAddress = Address("line1", "line2", Some("line3"), Some("line4"), Some("AA1 1AA"), "GB")
      val reviewDetails = ReviewDetails("BusinessName", SoleProprietor, businessAddress, "11111111")
      val model = BusinessMatching(
        reviewDetails,
        businessActivities,
        None,
        None)

      MlrActivitiesAppliedFor.conv(model) must be(Some(MlrActivitiesAppliedFor(true, false, false, true, true, true, true)))
    }

    "convert when no front model is empty" in {
      val businessActivities = BusinessActivities(Set.empty)
      val businessAddress = Address("line1", "line2", Some("line3"), Some("line4"), Some("AA1 1AA"), "GB")
      val reviewDetails = ReviewDetails("BusinessName", SoleProprietor, businessAddress, "11111111")
      val model = BusinessMatching(
        reviewDetails,
        businessActivities,
        None,
        None)

      MlrActivitiesAppliedFor.conv(model) must be(Some(MlrActivitiesAppliedFor(false, false, false, false, false,false, false)))
    }
  }

}
