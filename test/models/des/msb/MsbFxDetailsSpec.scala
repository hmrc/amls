/*
 * Copyright 2018 HM Revenue & Customs
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

package models.des.msb

import models.fe.moneyservicebusiness._
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class MsbFxDetailsSpec extends PlaySpec with OneAppPerSuite {

    override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

    "MsbFxDetails" should {

        "convert from fe model" in {
            val msbFxDetails = Some(MsbFxDetails("789789789"))

            val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
                fxTransactionsInNext12Months = Some(FXTransactionsInNext12Months("789789789"))
            )

            MsbFxDetails.conv(msbModel) must be(msbFxDetails)
        }

        "deserialize the json properly" in {

            val model = MsbFxDetails("789789789")

            val json =
                """ {
                  |   "anticipatedNoOfTransactions":"789789789"
                  | }
                """.stripMargin

            Json.parse(json).as[MsbFxDetails] must be(model)
        }
    }

}