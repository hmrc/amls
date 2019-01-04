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

package models.des.tradingpremises

import models.fe.tradingpremises._
import play.api.libs.json.Json

case class Msb (mt: Boolean,
                ce: Boolean,
                smdcc: Boolean,
                nonSmdcc: Boolean,
                fx: Boolean
               )

object Msb{
  implicit val format = Json.format[Msb]

  implicit def convert(services: Set[MsbService]) : Msb = {

    services.foldLeft[Msb](Msb(false, false, false, false, false))((x,y) =>
      y match {
        case TransmittingMoney => x.copy(mt = true)
        case CurrencyExchange => x.copy(ce = true)
        case ChequeCashingNotScrapMetal => x.copy(smdcc = true)
        case ChequeCashingScrapMetal => x.copy(nonSmdcc = true)
        case ForeignExchange => x.copy(fx = true)
      }
    )
  }
}

case class Hvd(hvd: Boolean)

object Hvd{
  implicit val format = Json.format[Hvd]

  implicit def convert(businessActivity: Set[BusinessActivity]) : Hvd ={
    Hvd(businessActivity.contains(BusinessActivity.HighValueDealing))
  }
}

case class Asp(asp: Boolean)

object Asp{
  implicit val format = Json.format[Asp]

  implicit def convert(businessActivity: Set[BusinessActivity]) : Asp ={
    Asp(businessActivity.contains(BusinessActivity.AccountancyServices))
  }
}

case class Tcsp(tcsp: Boolean)

object Tcsp{
  implicit val format = Json.format[Tcsp]

  implicit def convert(businessActivity: Set[BusinessActivity]) : Tcsp ={
    Tcsp(businessActivity.contains(BusinessActivity.TrustAndCompanyServices))
  }
}

case class Eab(eab: Boolean)

object Eab{
  implicit val format = Json.format[Eab]

  implicit def convert(businessActivity: Set[BusinessActivity]) : Eab ={
    Eab(businessActivity.contains(BusinessActivity.EstateAgentBusinessService))
  }
}

case class Bpsp(bpsp: Boolean)

object Bpsp{
  implicit val format = Json.format[Bpsp]

  implicit def convert(businessActivity: Set[BusinessActivity]) : Bpsp ={
    Bpsp(businessActivity.contains(BusinessActivity.BillPaymentServices))
  }
}

case class Tditpsp(tditpsp: Boolean)

object Tditpsp{
  implicit val format = Json.format[Tditpsp]

  implicit def convert(businessActivity: Set[BusinessActivity]) : Tditpsp ={
    Tditpsp(businessActivity.contains(BusinessActivity.TelephonePaymentService))
  }
}
