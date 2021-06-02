/*
 * Copyright 2021 HM Revenue & Customs
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

package models.fe.businessactivities

import models.des.businessactivities.FranchiseDetails
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsPath, JsSuccess}

class BusinessFranchiseSpec extends PlaySpec {

  "BusinessAppliedForPSRNumber" should {

    "Json Validation" must {

      "Successfully read and write data:option yes" in {
        BusinessFranchise.jsonReads.reads(BusinessFranchise.jsonWrites.writes(BusinessFranchiseYes("afdafaaa"))) must
          be(JsSuccess(BusinessFranchiseYes("afdafaaa"), JsPath \ "franchiseName"))
      }

      "Successfully read and write data:option No" in {
        BusinessFranchise.jsonReads.reads(BusinessFranchise.jsonWrites.writes(BusinessFranchiseNo)) must
          be(JsSuccess(BusinessFranchiseNo))
      }
    }

    "convert des to frontend model successfully for businessFranchiseYes" in {
      BusinessFranchise.conv(Some(FranchiseDetails(true, Some(Seq("notes"))))) must be(Some(BusinessFranchiseYes("notes")))
    }

    "convert des to frontend model successfully for businessFranchiseNo" in {
      BusinessFranchise.conv(Some(FranchiseDetails(false, None))) must be(Some(BusinessFranchiseNo))
    }

    "convert des to frontend model successfully when input is none" in {
      BusinessFranchise.conv(None) must be(Some(BusinessFranchiseNo))
    }
  }
}
