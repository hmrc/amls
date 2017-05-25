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

package models.fe.responsiblepeople

import models.des.responsiblepeople._
import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsSuccess

class PersonResidenceTypeSpec extends PlaySpec {

  "PersonResidenceType" should {
    val year = 2001
    val month = 1
    val date = 1
    "Json validation" must {

      "Successfully read uk residence type model" in {
        val ukModel = PersonResidenceType(UKResidence("AA1111111"),
          "GB", "GB")

        PersonResidenceType.jsonRead.reads(PersonResidenceType.jsonWrite.writes(ukModel)) must
          be(JsSuccess(ukModel))
      }

      "Successfully validate non uk residence type model" in {
        val nonUKModel = PersonResidenceType(NonUKResidence(new LocalDate(year, month, date)), "GB", "GB")

        PersonResidenceType.jsonRead.reads(
          PersonResidenceType.jsonWrite.writes(nonUKModel)) must
          be(JsSuccess(nonUKModel))
      }
    }

    "nonUkResident:convert des model to frontend model successfully" in {

      val desModel = Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AD"),
        Some("AD")
      ))
      val feModel = Some(PersonResidenceType(NonUKResidence(new LocalDate(year, month, date)),"AD","AD"))
      PersonResidenceType.conv(desModel) must be(feModel)
    }

    "nonUkResident with nonuk pass port :convert des model to frontend model successfully" in {

      val desModel = Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              false,
              PassportNum(None,Some("AA1111111"))
            ))
          ))
        )),
        Some("AA"),
        Some("BB")
      ))
      val feModel = Some(PersonResidenceType(NonUKResidence(new LocalDate(year, month, date)),"AA","BB"))
      PersonResidenceType.conv(desModel) must be(feModel)

    }

    "nonUkResident with no passport:convert des model to frontend model successfully" in {

      val desModel = Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            None
          ))
        )),
        Some("AA"),
        Some("BB")
      ))
      val feModel = Some(PersonResidenceType(NonUKResidence(new LocalDate(year, month, date)),"AA","BB"))
      PersonResidenceType.conv(desModel) must be(feModel)

    }

    "UkResident:convert des model to frontend model successfully" in {

      val desModel = Some(NationalityDetails(
        true,
        Some(IdDetail(
          Some(UkResident("AA1111111")))),
        Some("JJ"),
        Some("GG")
      ))
      val feModel = Some(PersonResidenceType(UKResidence("AA1111111"),"JJ","GG"))
      PersonResidenceType.conv(desModel) must be(feModel)

    }
  }
}
