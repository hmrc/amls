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
import play.api.libs.json.{JsPath, JsSuccess, Json}

class DateOfBirthSpec extends PlaySpec {

  val date = new LocalDate(1990,2,24)

  "DateOfBirth" must {

    "validate JSON successfully" when {

      "given a valid date" in {

        val json = Json.obj(
          "dateOfBirth" -> "1990-02-24"
        )

        Json.fromJson[DateOfBirth](json) must
          be(JsSuccess(DateOfBirth(date), JsPath \ "dateOfBirth"))
      }

    }

    "write the correct value to JSON" in {
      Json.toJson(DateOfBirth(date)) must
        be(Json.obj(
          "dateOfBirth" -> "1990-02-24"
        ))
    }

    "convert from des ResponsiblePerson to fe DateOfBirth" in {

      val desModel = ResponsiblePersons(
        None,Some(NationalityDetails(
          false,
          Some(IdDetail(
            nonUkResident = Some(NonUkResident(
              "1990-03-23",false,None
            ))
          )),None,None
        )),None,None,None,None,None,None,None,None,None,false,None,false,None,None,None,None,RPExtra()
      )

      DateOfBirth.conv(desModel) must be(Some(DateOfBirth(new LocalDate(1990,3,23))))
    }
  }
}
