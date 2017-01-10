/*
 * Copyright 2017 HM Revenue & Customs
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

import models.fe.businessmatching.BusinessType.{LPrLLP, LimitedCompany, Partnership, SoleProprietor, UnincorporatedBody}
import models.des.businessdetails.{BusinessType => DesBusinessType}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class BusinessTypeSpec extends PlaySpec {

  "BusinessType" should {

    "Read JSON data successfully" in {
      BusinessType.reads.reads(BusinessType.writes.writes(SoleProprietor)) must be(JsSuccess(SoleProprietor, JsPath))
      BusinessType.reads.reads(BusinessType.writes.writes(LPrLLP)) must be(JsSuccess(LPrLLP, JsPath))
      BusinessType.reads.reads(BusinessType.writes.writes(Partnership)) must be(JsSuccess(Partnership, JsPath))
      BusinessType.reads.reads(BusinessType.writes.writes(LimitedCompany)) must be(JsSuccess(LimitedCompany, JsPath))
      BusinessType.reads.reads(BusinessType.writes.writes(UnincorporatedBody)) must be(JsSuccess(UnincorporatedBody, JsPath))
    }

    "convert input string successfully" in {
      BusinessType.conv(DesBusinessType.LPrLLP) must be(LPrLLP)
      BusinessType.conv(DesBusinessType.Partnership) must be(Partnership)
      BusinessType.conv(DesBusinessType.LimitedCompany) must be(LimitedCompany)
      BusinessType.conv(DesBusinessType.UnincorporatedBody) must be(UnincorporatedBody)
      BusinessType.conv(DesBusinessType.SoleProprietor) must be(SoleProprietor)
    }
  }

}
