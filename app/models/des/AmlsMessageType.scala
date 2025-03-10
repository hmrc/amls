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

package models.des

sealed trait AmlsMessageType

case object Amendment extends AmlsMessageType

case object Variation extends AmlsMessageType

case object Renewal extends AmlsMessageType

case object RenewalAmendment extends AmlsMessageType

object AmlsMessageType {
  implicit def convToString(mt: AmlsMessageType): String =
    mt match {
      case Amendment        => "Amendment"
      case Variation        => "Variation"
      case Renewal          => "Renewal"
      case RenewalAmendment => "Renewal Amendment"
    }
}
