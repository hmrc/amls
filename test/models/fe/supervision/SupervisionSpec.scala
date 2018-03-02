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

package models.fe.supervision

import models.des.supervision._
import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class SupervisionSpec extends PlaySpec with MockitoSugar with SupervisionValues {

  "Supervision" must {

    "Complete Model" when {

      "correctly convert between json formats" when {

        "Serialise as expected" in {
          Json.toJson(completeModel) must be(completeJson)
        }

        "Deserialise as expected" in {
          completeJson.as[Supervision] must be(completeModel)
        }
      }
    }

    "convert supervision des to frontend successfully" in {

      val desModel = AspOrTcsp(
        Some(SupervisionDetails(
          true,
          Some(SupervisorDetails(
            "NameOfLastSupervisor",
            "2001-01-01",
            "2001-01-01",
            None,
            "SupervisionEndingReason")
          )
        )),
        Some(ProfessionalBodyDetails(
          true,
          Some("DetailsIfFinedWarned"),
          Some(ProfessionalBodyDesMember(
            true,
            Some(MemberOfProfessionalBody(
              false, true, true, false, false, false, false, true, false, false, false, false, false, true, Some("SpecifyOther")
            ))
          ))
        ))
      )

      val convertedModel = Some(Supervision(
        Some(AnotherBodyYes("NameOfLastSupervisor",new LocalDate(2001,1,1), new LocalDate(2001,1,1), "SupervisionEndingReason")),
        Some(ProfessionalBodyMemberYes),
        Some(BusinessTypes(Set(
          AccountantsIreland,
          CharteredCertifiedAccountants,
          InternationalAccountants,
          Other("SpecifyOther")
        ))),
        Some(ProfessionalBodyYes("DetailsIfFinedWarned"))))

      Supervision.makeSupervisionSectionFromAspOrTcsp(Some(desModel)) must be(convertedModel)
    }

    "convert supervision des to frontend successfully wne input is none" in {
      Supervision.makeSupervisionSectionFromAspOrTcsp(None) must be(Some(Supervision(
        None,
        None,
        None,
        None
      )))
    }

    "convert supervision des to frontend successfully when no professional body details returned" in {
      val desModel = AspOrTcsp(
        Some(SupervisionDetails(
          true,
          Some(SupervisorDetails(
            "NameOfLastSupervisor",
            "2001-01-01",
            "2001-01-01",
            None,
            "SupervisionEndingReason")
          )
        )),
        None)

      Supervision.makeSupervisionSectionFromAspOrTcsp(Some(desModel)) must be(Some(Supervision(
        Some(AnotherBodyYes("NameOfLastSupervisor",new LocalDate(2001,1,1),new LocalDate(2001,1,1),"SupervisionEndingReason")),
        Some(ProfessionalBodyMemberNo),
        None,
        Some(ProfessionalBodyNo))
      ))
    }

  }
}

trait SupervisionValues {

  object DefaultValues {

    private val supervisor = "Company A"
    private val start = new LocalDate(1993, 8, 25)
    //scalastyle:off magic.number
    private val end = new LocalDate(1999, 8, 25)
    //scalastyle:off magic.number
    private val reason = "Ending reason"

    val DefaultAnotherBody = AnotherBodyYes(supervisor, start, end, reason)
    val DefaultProfessionalBody = ProfessionalBodyYes("details")
    val DefaultProfessionalBodyMember = ProfessionalBodyMemberYes
    val DefaultBusinessTypes = BusinessTypes(Set(AccountingTechnicians, CharteredCertifiedAccountants, Other("test")))
  }

  object NewValues {
    val NewAnotherBody = AnotherBodyNo
    val NewProfessionalBody = ProfessionalBodyNo
    val ProfessionalBodyMemberYes = ProfessionalBodyMemberNo
  }

  val completeModel = Supervision(
    Some(DefaultValues.DefaultAnotherBody),
    Some(DefaultValues.DefaultProfessionalBodyMember),
    Some(DefaultValues.DefaultBusinessTypes),
    Some(DefaultValues.DefaultProfessionalBody))
  val partialModel = Supervision(Some(DefaultValues.DefaultAnotherBody))

  val completeJson = Json.obj(
    "anotherBody" -> Json.obj(
      "anotherBody" -> true,
      "supervisorName" -> "Company A",
      "startDate" -> "1993-08-25",
      "endDate" -> "1999-08-25",
      "endingReason" -> "Ending reason"),
    "professionalBodyMember" -> Json.obj(
      "isAMember" -> true
    ),
    "professionalBodies" -> Json.obj(
      "businessType" -> Json.arr("01", "02", "14"),
      "specifyOtherBusiness" -> "test"
    ),
    "professionalBody" -> Json.obj(
      "penalised" -> true,
      "professionalBody" -> "details")
  )

}
