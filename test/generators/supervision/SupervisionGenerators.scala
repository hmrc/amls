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

package generators.supervision

import models.des.businessactivities.MlrActivitiesAppliedFor
import models.des.supervision._
import org.joda.time.LocalDate
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait SupervisionGenerators {
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
}
