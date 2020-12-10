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

package models.des.responsiblepeople

import models.des.responsiblepeople.{SoleProprietor => DesSoleProprietor}
import models.fe.businesscustomer.{ReviewDetails, Address => BMAddress}
import models.fe.businessmatching._
import models.fe.responsiblepeople.{SoleProprietor => FESoleProprietor, _}
import org.joda.time.LocalDate
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec

class PositionInBusinessSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  val today = new LocalDate()

  "PositionInBusiness" must {

    "convert frontend model to des model successfully for SoleProprietor" in {

      val bm = BusinessMatching(ReviewDetails("CompanyName", BusinessType.SoleProprietor, BMAddress("BusinessAddressLine1", "BusinessAddressLine2",
        Some("BusinessAddressLine3"), Some("BusinessAddressLine4"),
        Some("AA1 1AA"), "AD"), ""),
        BusinessActivities(Set(HighValueDealing)),
        None, None)

      val desModel = Some(PositionInBusiness(
        Some(DesSoleProprietor(true, true, Some(true), Some("some other role"))),
        None,
        None
      ))

      val positions = Some(Positions(Set(Partner, FESoleProprietor, NominatedOfficer, Director, BeneficialOwner, Other("some other role")), Some(today)))

      PositionInBusiness.conv(positions, bm) must be(desModel)
    }

    "convert frontend model to des model  successfully for LimitedCompany" in {

      val bm = BusinessMatching(ReviewDetails("CompanyName", BusinessType.LimitedCompany, BMAddress("BusinessAddressLine1", "BusinessAddressLine2",
        Some("BusinessAddressLine3"), Some("BusinessAddressLine4"),
        Some("AA1 1AA"), "AD"), ""),
        BusinessActivities(Set(HighValueDealing)),
        None, None)

      val desModel = Some(PositionInBusiness(
        None,
        None,
        Some(CorpBodyOrUnInCorpBodyOrLlp(true, true, true, Some(true), Some(true), Some("some other role")))
      ))

      val positions = Some(Positions(Set(Director, NominatedOfficer, DesignatedMember, BeneficialOwner, Other("some other role")), Some(today)))

      PositionInBusiness.conv(positions, bm) must be(desModel)
    }

    "convert frontend model to des model  successfully for Partnership" in {

      val bm = BusinessMatching( ReviewDetails("CompanyName", BusinessType.Partnership, BMAddress("BusinessAddressLine1", "BusinessAddressLine2",
        Some("BusinessAddressLine3"), Some("BusinessAddressLine4"),
        Some("AA1 1AA"), "AD"), ""),
        BusinessActivities(Set(HighValueDealing)),
        None, None)

      val desModel = Some(PositionInBusiness(
        None,
        Some(Partnership(true, true, Some(true), Some("another role"))),
        None
      ))

      val positions = Some(Positions(Set(Partner, NominatedOfficer, Other("another role")),Some(today)))

      PositionInBusiness.conv(positions, bm) must be(desModel)
    }

    "convert frontend model to des model successfully when user has no data selected" in {
      val bm = BusinessMatching(ReviewDetails("CompanyName", BusinessType.Partnership, BMAddress("BusinessAddressLine1", "BusinessAddressLine2",
        Some("BusinessAddressLine3"), Some("BusinessAddressLine4"),
        Some("AA1 1AA"), "AD"), ""),
        BusinessActivities(Set(HighValueDealing)),
        None, None)

      val desModel = Some(PositionInBusiness(
        None,
        Some(Partnership(other = Some(false), otherDetails = None)),
        None
      ))

      val positions = Some(Positions(Set.empty, Some(today)))

      PositionInBusiness.conv(positions, bm) must be(desModel)
    }

    "convert des model to frontend model successfully when input is none" in {
      val bm = BusinessMatching(ReviewDetails("CompanyName", BusinessType.LimitedCompany, BMAddress("BusinessAddressLine1", "BusinessAddressLine2",
        Some("BusinessAddressLine3"), Some("BusinessAddressLine4"),
        Some("AA1 1AA"), "AD"), ""),
        BusinessActivities(Set(HighValueDealing)),
        None, None)
      PositionInBusiness.conv(None, bm) must be(None)
    }
  }

}
