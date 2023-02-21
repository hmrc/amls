/*
 * Copyright 2023 HM Revenue & Customs
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

package generators.supervision

import models.des.businessactivities.MlrActivitiesAppliedFor
import org.joda.time.LocalDate
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait BusinessActivityGenerators {
  val activityGen: Gen[MlrActivitiesAppliedFor] = for {
    msb <- arbitrary[Boolean]
    hvd <- arbitrary[Boolean]
    asp <- arbitrary[Boolean]
    tcsp <- arbitrary[Boolean]
    eab <- arbitrary[Boolean]
    bpsp <- arbitrary[Boolean]
    tditpsp <- arbitrary[Boolean]
    amp <- arbitrary[Boolean]
  } yield MlrActivitiesAppliedFor(msb, hvd, asp, tcsp, eab, bpsp, tditpsp, amp)

  implicit val arbitraryMlrActivities: Arbitrary[MlrActivitiesAppliedFor] = Arbitrary(activityGen.sample.get)
  implicit val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary(LocalDate.now())
}
