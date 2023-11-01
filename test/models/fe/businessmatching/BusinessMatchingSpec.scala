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

package models.fe.businessmatching

import models.des.DesConstants
import models.des.businessdetails.{BusinessDetails, CorpAndBodyLlps}
import models.fe.businesscustomer.{Address, ReviewDetails}
import models.fe.businessmatching.BusinessType.SoleProprietor
import org.scalatestplus.play.PlaySpec
import utils.AmlsBaseSpec

class BusinessMatchingSpec extends PlaySpec with AmlsBaseSpec {

  "BusinessMatchingSpec" must {

    import play.api.libs.json._
    val msbServices = MsbServices(
      Set(
        TransmittingMoney,
        CurrencyExchange,
        ChequeCashingNotScrapMetal,
        ChequeCashingScrapMetal
      )
    )
    val BusinessActivitiesModel = BusinessActivities(Set(MoneyServiceBusiness, TrustAndCompanyServices, TelephonePaymentService))
    val businessAddress = Address("line1", Some("line2"), Some("line3"), Some("line4"), Some("AA1 1AA"), "GB")
    val ReviewDetailsModel = ReviewDetails("BusinessName", BusinessType.UnincorporatedBody, businessAddress, "AA0001234567890")
    val TypeOfBusinessModel = TypeOfBusiness("test")
    val CompanyRegistrationNumberModel = CompanyRegistrationNumber("12345678")
    val BusinessAppliedForPSRNumberModel = BusinessAppliedForPSRNumberYes("123456")

    val jsonBusinessMatching = Json.obj(
      "businessActivities" -> Seq("05", "06", "07"),
      "msbServices" -> Seq("01", "02", "03", "04"),
      "businessName" -> "BusinessName",
      "businessType" -> "Unincorporated Body",
      "businessAddress" -> Json.obj(
        "line_1" -> "line1",
        "line_2" -> "line2",
        "line_3" -> "line3",
        "line_4" -> "line4",
        "postcode" -> "AA1 1AA",
        "country" -> "GB"
      ),
      "safeId" -> "AA0001234567890",
      "typeOfBusiness" -> "test",
      "companyRegistrationNumber" -> "12345678",
      "appliedFor" -> true,
      "regNumber" -> "123456"
    )

    val businessMatching = BusinessMatching(
      ReviewDetailsModel,
      BusinessActivitiesModel,
      Some(msbServices),
      Some(TypeOfBusinessModel),
      Some(CompanyRegistrationNumberModel),
      Some(BusinessAppliedForPSRNumberModel))

    "JSON validation" must {
      "READ the JSON successfully and return the domain Object" in {
        Json.fromJson[BusinessMatching](jsonBusinessMatching) must be(JsSuccess(businessMatching))
      }

      "WRITE the JSON successfully from the domain Object" in {
        Json.toJson(businessMatching) must be(jsonBusinessMatching)
      }
    }

    "Convert des model to frontend Business matching" in {

      val bmModel = BusinessMatching(
        ReviewDetails("CompanyName", SoleProprietor, Address("BusinessAddressLine1", Some("BusinessAddressLine2"), Some("BusinessAddressLine3"),
          Some("BusinessAddressLine4"), Some("AA1 1AA"), "GB"), ""),
        BusinessActivities(Set(HighValueDealing, AccountancyServices, EstateAgentBusinessService,
          BillPaymentServices, TelephonePaymentService, MoneyServiceBusiness, ArtMarketParticipant, TrustAndCompanyServices)),
        Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal, ForeignExchange))),
        Some(TypeOfBusiness("TypeOfBusiness")),
        Some(CompanyRegistrationNumber("12345678")),
        Some(BusinessAppliedForPSRNumberYes("123456")))

      BusinessMatching.conv(DesConstants.SubscriptionViewModel) must be(bmModel)

    }
    "Convert des model to frontend TypeOfBusiness" in {
      val desBusinessDetails = BusinessDetails(BusinessType.SoleProprietor,
        Some(CorpAndBodyLlps("CompanyName", "12345678")),
        None)

      TypeOfBusiness.conv(desBusinessDetails) must be(None)
    }
  }
}
