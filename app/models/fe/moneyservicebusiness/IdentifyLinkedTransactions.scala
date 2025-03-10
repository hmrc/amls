/*
 * Copyright 2024 HM Revenue & Customs
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

package models.fe.moneyservicebusiness

import models.des.msb.MsbAllDetails
import play.api.libs.json.{Json, OFormat}

case class IdentifyLinkedTransactions(linkedTxn: Boolean)

object IdentifyLinkedTransactions {

  implicit val format: OFormat[IdentifyLinkedTransactions] = Json.format[IdentifyLinkedTransactions]

  implicit def convMsbAll(msbAll: Option[MsbAllDetails]): Option[IdentifyLinkedTransactions] =
    msbAll match {
      case Some(msbDtls) => Some(IdentifyLinkedTransactions(msbDtls.sysLinkedTransIdentification))
      case None          => None
    }
}
