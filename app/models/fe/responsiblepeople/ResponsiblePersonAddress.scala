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

package models.fe.responsiblepeople

case class ResponsiblePersonAddress(personAddress: PersonAddress,
                                    timeAtAddress: TimeAtAddress)

object ResponsiblePersonAddress {

  import play.api.libs.json._

  implicit val format = Json.format[ResponsiblePersonAddress]

  implicit def convertToCurrent(address:ResponsiblePersonAddress) : ResponsiblePersonCurrentAddress= {
    ResponsiblePersonCurrentAddress(address.personAddress, address.timeAtAddress, dateOfChange = None)
  }

}

case class ResponsiblePersonCurrentAddress(personAddress: PersonAddress,
                                           timeAtAddress: TimeAtAddress,
                                           dateOfChange: Option[String] = None)

object ResponsiblePersonCurrentAddress {

  import play.api.libs.json._

  implicit val format = Json.format[ResponsiblePersonCurrentAddress]
}
