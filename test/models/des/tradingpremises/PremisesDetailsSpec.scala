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

package models.des.tradingpremises

import models.fe.tradingpremises.{ChequeCashingNotScrapMetal, ChequeCashingScrapMetal, CurrencyExchange, ForeignExchange, MsbService, TransmittingMoney}
import utils.AmlsBaseSpec

class PremisesDetailsSpec extends AmlsBaseSpec {

  "PremisesDetails" must {
    "have convert method which" when {
      "called with set of all FE Msb services will return DES Msb" in {
        val feMsbServices = Set[MsbService](TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal, ForeignExchange)

        val expectedMsb = Msb(mt = true, ce = true, smdcc = true, nonSmdcc = true, fx = true)

        Msb.convert(feMsbServices) mustBe expectedMsb
      }

      "called with TransmittingMoney will return correct DES Msb" in {
        val feMsbServices = Set[MsbService](TransmittingMoney)

        val expectedMsb = Msb(mt = true, ce = false, smdcc = false, nonSmdcc = false, fx = false)

        Msb.convert(feMsbServices) mustBe expectedMsb
      }

      "called with CurrencyExchange will return correct DES Msb" in {
        val feMsbServices = Set[MsbService](CurrencyExchange)

        val expectedMsb = Msb(mt = false, ce = true, smdcc = false, nonSmdcc = false, fx = false)

        Msb.convert(feMsbServices) mustBe expectedMsb
      }

      "called with ChequeCashingNotScrapMetal will return correct DES Msb" in {
        val feMsbServices = Set[MsbService](ChequeCashingNotScrapMetal)

        val expectedMsb = Msb(mt = false, ce = false, smdcc = false, nonSmdcc = true, fx = false)

        Msb.convert(feMsbServices) mustBe expectedMsb
      }

      "called with ChequeCashingScrapMetal will return correct DES Msb" in {
        val feMsbServices = Set[MsbService](ChequeCashingScrapMetal)

        val expectedMsb = Msb(mt = false, ce = false, smdcc = true, nonSmdcc = false, fx = false)

        Msb.convert(feMsbServices) mustBe expectedMsb
      }

      "called with ForeignExchange will return correct DES Msb" in {
        val feMsbServices = Set[MsbService](ForeignExchange)

        val expectedMsb = Msb(mt = false, ce = false, smdcc = false, nonSmdcc = false, fx = true)

        Msb.convert(feMsbServices) mustBe expectedMsb
      }
    }
  }
}
