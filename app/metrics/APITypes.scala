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

package metrics

sealed trait APITypes {
  def key: String
}

case object API4 extends APITypes {
  override val key: String = "etmp-amls-registration-response"
}

case object API5 extends APITypes {
  override val key: String = "etmp-amls-registration-view"
}

case object API6 extends APITypes {
  override val key: String = "etmp-amls-amendment-variation-response"
}

case object API9 extends APITypes {
  override val key: String = "etmp-amls-registration-status-response"
}

case object API8 extends APITypes {
  override val key: String = "etmp-amls-withdraw-subscription-response"
}

case object API10 extends APITypes {
  override val key: String = "etmp-amls-deregister-subscription-response"
}

case object GGAdmin extends APITypes {
  override val key: String = "gg-admin-amls-add-known-facts"
}

case object PayAPI extends APITypes {
  override val key: String = "pay-api-getPayment-with-id"
}

case object EnrolmentStoreKnownFacts extends APITypes {
  override val key: String = "enrolment-store-known-facts"
}
