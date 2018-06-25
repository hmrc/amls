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

import models.des.businessactivities.MlrActivitiesAppliedFor
import models.des.supervision._
import org.joda.time.LocalDate
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, PropertyChecks}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class SupervisionSpec extends PlaySpec with MockitoSugar with SupervisionValues with PropertyChecks {

  "Supervision" must {
    "convert supervision des to frontend successfully" when {
      "Neither ASP or TCSP have been selected" in {
        forAll(arbitrary[AspOrTcsp], arbitrary[MlrActivitiesAppliedFor]) { (aspOrTcsp, activities) =>
          val result = Supervision.convertFrom(Some(aspOrTcsp), Some(withoutAspOrTcsp(activities))).get

          result.anotherBody mustBe defined
          result.professionalBody mustBe defined
          result.professionalBodyMember mustBe defined
          result.professionalBodies mustBe defined
        }
      }
    }

    "convert Supervision des to frontend" which {
      "has all of the questions set to 'no'" when {
        "the des model is None and the application contains ASP or TCSP" in {
          forAll(activitiesWithSupervision) { activitySet =>
            val result = Supervision.convertFrom(None, Some(activitySet))
            result mustBe Some(Supervision(Some(AnotherBodyNo), Some(ProfessionalBodyMemberNo), None, Some(ProfessionalBodyNo)))
          }
        }
      }

      "is None" when {
        "the des model is None and the application contains neither ASP or TCSP" in {
          forAll(arbitrary[MlrActivitiesAppliedFor]) { activities =>
            val result = Supervision.convertFrom(None, Some(withoutAspOrTcsp(activities)))
            result must not be defined
          }
        }
      }
    }

    "convert supervision des to frontend successfully when no professional body details returned" in {
      forAll(arbitrary[AspOrTcsp]) { model =>
        val result = Supervision.convertFrom(Some(model.copy(professionalBodyDetails = None)), None)

        result.get.professionalBody must contain(ProfessionalBodyNo)
      }
    }
  }
}

trait SupervisionValues {
  val activitiesWithAspTcsp = MlrActivitiesAppliedFor(msb = false, hvd = false, asp = true, tcsp = true, eab = false, bpsp = false, tditpsp = false)
  val activitiesWithAsp = MlrActivitiesAppliedFor(msb = false, hvd = false, asp = true, tcsp = false, eab = false, bpsp = false, tditpsp = false)
  val activitiesWithTcsp = MlrActivitiesAppliedFor(msb = false, hvd = false, asp = false, tcsp = true, eab = false, bpsp = false, tditpsp = false)
  val activitiesWithSupervision = Gen.oneOf(activitiesWithAspTcsp, activitiesWithAsp, activitiesWithTcsp)

  val activityGen: Gen[MlrActivitiesAppliedFor] = for {
    msb <- arbitrary[Boolean]
    hvd <- arbitrary[Boolean]
    asp <- arbitrary[Boolean]
    tcsp <- arbitrary[Boolean]
    eab <- arbitrary[Boolean]
    bpsp <- arbitrary[Boolean]
    tditpsp <- arbitrary[Boolean]
  } yield MlrActivitiesAppliedFor(msb, hvd, asp, tcsp, eab, bpsp, tditpsp)

  implicit val arbitraryMlrActivities: Arbitrary[MlrActivitiesAppliedFor] = Arbitrary(activityGen.sample.get)
  implicit val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary(LocalDate.now())

  val supervisorDetailsGen: Gen[SupervisorDetails] = for {
    name <- arbitrary[String]
    startDate <- Gen.const(LocalDate.now())
    endDate <- Gen.const(LocalDate.now())
    dateChange <- arbitrary[Boolean]
    reason <- arbitrary[String]
  } yield SupervisorDetails(name, startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"), Some(dateChange), reason)

  val supervisionDetailsGen: Gen[SupervisionDetails] = for {
    supervised <- arbitrary[Boolean]
    supervisor <- supervisorDetailsGen
  } yield SupervisionDetails(supervised, if (supervised) Some(supervisor) else None)

  val memberOfProfessionalBodyGen: Gen[MemberOfProfessionalBody] = Gen.const(
    MemberOfProfessionalBody(false, false, false, false, false, false, false, false, false, false, false, false, false, false, None)
  )

  val professionalBodyGen: Gen[ProfessionalBodyDetails] = for {
    preWarned <- arbitrary[Boolean]
    details <- arbitrary[String]
    professionalBodyMember <- arbitrary[Boolean]
    member <- Gen.const(ProfessionalBodyDesMember(professionalBodyMember, memberOfProfessionalBodyGen.sample))
  } yield ProfessionalBodyDetails(preWarned, if (preWarned) Some(details) else None, Some(member))

  val aspOrTcspGen: Gen[AspOrTcsp] = for {
    details <- supervisionDetailsGen
    professionalBody <- professionalBodyGen
  } yield AspOrTcsp(Some(details), Some(professionalBody))

  def withoutAspOrTcsp(model: MlrActivitiesAppliedFor): MlrActivitiesAppliedFor =
    model.copy(asp = false, tcsp = false)

  implicit val arbitraryAspOrTcsp: Arbitrary[AspOrTcsp] = Arbitrary(aspOrTcspGen)

  val negativeSupervision = Supervision(Some(AnotherBodyNo), Some(ProfessionalBodyMemberNo), None, Some(ProfessionalBodyNo))

}
