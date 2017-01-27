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

import models.des.responsiblepeople.{PreviousNameDetails, OthrNamesOrAliasesDetails, PersonName=> DesPersonName, NameDetails}
import org.joda.time.LocalDate
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class PersonNameSpec extends PlaySpec with MockitoSugar {

  "PersonName" must {
    "Read/Write Json" in {
      val previousName = PreviousName(Some("FirstName"), Some("MiddleName"), Some("LastName"),
        // scalastyle:off magic.number
        new LocalDate(1990, 2, 24)
      )
      val personName = PersonName(
        firstName = "FirstName",
        middleName = Some("MiddleName"),
        lastName = "LastName",
        previousName = Some(previousName),
        otherNames = Some("Doc")
      )

      PersonName.format.reads(PersonName.format.writes(personName))
    }

    "Read/Write Json with no PreviousName" in {
      val personName = PersonName(
        firstName = "name",
        middleName = Some("some"),
        lastName = "surname",
        previousName = None,
        otherNames = Some("Doc")
      )
      PersonName.format.reads(PersonName.format.writes(personName))
    }


    "convert des model to frontend personName model" in {

      val desModel = Some(NameDetails(
        DesPersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1", "Aliases2", "Aliases3", "Aliases4"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(DesPersonName(Some("FirstName1"), Some("MiddleName1"), Some("LastName1"))),
          Some("2001-01-01")
        ))
      ))

      val previousName = PreviousName(Some("FirstName1"), Some("MiddleName1"), Some("LastName1"), // scalastyle:off magic.number
        new LocalDate(2001, 1, 1)
      )

      val fePersonName = PersonName(
        firstName = "FirstName",
        middleName = Some("MiddleName"),
        lastName = "LastName",
        previousName = Some(previousName),
        otherNames = Some("Aliases1")
      )
      PersonName.conv(desModel) must be(Some(fePersonName))
    }

    "convert when input is none" in {
      PersonName.conv(None) must be(None)
    }

    "convert othernames successfully when input is none" in {
      PersonName.convOtherNames(None) must be (None)
    }

    "convert othernames successfully" in {

      val desModel = Some(OthrNamesOrAliasesDetails(
        true,
        Some(List.empty)
      ))
      PersonName.convOtherNames(desModel) must be (None)
    }
  }
}
