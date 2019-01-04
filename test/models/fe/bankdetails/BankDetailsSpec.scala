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

package models.fe.bankdetails

import models.des.DesConstants
import models.des.bankdetails.BankDetailsView
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class BankDetailsSpec extends PlaySpec with MockitoSugar {

  val accountType = PersonalAccount

  "BankDetails with Bank Account Type and Bank Details" must {

    "convert des model to frontend model" in {

      val convertedModel = List(
        BankDetails(BelongsToBusiness,"AccountName",UKAccount("12345678","123456")),
        BankDetails(PersonalAccount,"AccountName1",NonUKIBANNumber("87654321")),
        BankDetails(BelongsToOtherBusiness,"AccountName2",NonUKAccountNumber("87654321")))

      BankDetails.conv(DesConstants.testBankDetails) must be(convertedModel)
    }

    "return empty list when des returns none " in {
      val testBankDetails = Some(BankDetailsView(Some("0"), None))
      BankDetails.conv(testBankDetails) must be(List.empty)
    }
  }

}
