/*
 * Copyright 2022 HM Revenue & Customs
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

import generators.supervision.{BusinessActivityGenerators, SupervisionGenerators}
import models.des.businessactivities.MlrActivitiesAppliedFor
import models.des.supervision._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatestplus.play.PlaySpec

class SupervisionSpec extends PlaySpec
  with MockitoSugar
  with SupervisionValues
  with ScalaCheckPropertyChecks
  with SupervisionGenerators
  with BusinessActivityGenerators {

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
  val activitiesWithAspTcsp = MlrActivitiesAppliedFor(msb = false, hvd = false, asp = true, tcsp = true, eab = false, bpsp = false, tditpsp = false, amp = false)
  val activitiesWithAsp = MlrActivitiesAppliedFor(msb = false, hvd = false, asp = true, tcsp = false, eab = false, bpsp = false, tditpsp = false, amp = false)
  val activitiesWithTcsp = MlrActivitiesAppliedFor(msb = false, hvd = false, asp = false, tcsp = true, eab = false, bpsp = false, tditpsp = false, amp = false)
  val activitiesWithSupervision: Gen[MlrActivitiesAppliedFor] = Gen.oneOf(activitiesWithAspTcsp, activitiesWithAsp, activitiesWithTcsp)
  val negativeSupervision = Supervision(Some(AnotherBodyNo), Some(ProfessionalBodyMemberNo), None, Some(ProfessionalBodyNo))
}
