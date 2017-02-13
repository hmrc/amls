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

package models.des.tradingpremises

import models.des.{DesConstants, RequestType, StringOrInt}
import org.joda.time.LocalDate
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsSuccess, Json}

class AgentDetailsSpec extends PlaySpec with OneAppPerSuite {

  "AgentDetails" must {

    "reads using sample Json" in {

      val model = AgentDetails(
        agentLegalEntity = "agle",
        companyRegNo = Some("crn"),
        dateOfBirth = Some("2008-09-11"),
        agentLegalEntityName = Some("aglen"),
        agentPremises = DesConstants.AgentPremisesModel1,
        startDate = Some("2001-10-11"),
        dateChangeFlag = Some(false),
        endDate = Some("2003-09-08"),
        status = Some("Added"),
        lineId = Some("44444"),
        agentDetailsChangeDate = Some("2003-07-06")
      )

      val json = Json.obj(
        "agentLegalEntity" -> "agle",
        "companyRegNo" -> "crn",
        "dateOfBirth" -> "2008-09-11",
        "agentLegalEntityName" -> "aglen",
        "agentPremises" -> Json.obj(
          "tradingName" -> "TradingName",
          "businessAddress" -> Json.obj(
            "addressLine1" -> "AddressLine1",
            "addressLine2" -> "AddressLine2",
            "addressLine3" -> "AddressLine3",
            "addressLine4" -> "AddressLine4",
            "country" -> "AD",
            "postcode" -> "AA1 1AA"
          ),
          "residential" -> true,
          "msb" -> Json.obj(
            "mt" -> true,
            "ce" -> false,
            "smdcc" -> true,
            "nonSmdcc" -> true,
            "fx" -> true
          ),
          "hvd" -> Json.obj(
            "hvd" -> true
          ),
          "asp" -> Json.obj(
            "asp" -> false
          ),
          "tcsp" -> Json.obj(
            "tcsp" -> true
          ),
          "eab" -> Json.obj(
            "eab" -> false
          ),
          "bpsp" -> Json.obj(
            "bpsp" -> true
          ),
          "tditpsp" -> Json.obj(
            "tditpsp" -> false
          ),
          "startDate" -> "2001-01-01"
        ),
        "startDate" -> "2001-10-11",
        "dateChangeFlag" -> false,
        "endDate" -> "2003-09-08",
        "status" -> "Added",
        "lineId" -> "44444",
        "agentDetailsChangeDate" -> "2003-07-06"
      )

      json must be (Json.toJson(model))

    }

  }

}
