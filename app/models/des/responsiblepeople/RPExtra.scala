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

package models.des.responsiblepeople

import models.des.StringOrInt
import models.fe.responsiblepeople.ResponsiblePeople
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.StatusConstants

case class RPExtra(
  lineId: Option[StringOrInt] = None,
  endDate: Option[String] = None,
  status: Option[String] = None,
  retestFlag: Option[Boolean] = None,
  retest: Option[String] = None,
  testResultFitAndProper: Option[String] = None,
  testDateFitAndProper: Option[String] = None,
  testResultApprovalCheck: Option[String] = None,
  testDateApprovalCheck: Option[String] = None
)

object RPExtra {

  implicit val reads: Reads[RPExtra] = (
    __.read(Reads.optionNoError[StringOrInt]) and
      (__ \ "endDate").readNullable[String] and
      (__ \ "status").readNullable[String] and
      (__ \ "retestFlag").readNullable[Boolean] and
      (__ \ "retest").readNullable[String] and
      (__ \ "testResultFitAndProper").readNullable[String] and
      (__ \ "testDateFitAndProper").readNullable[String] and
      (__ \ "testResultApprovalCheck").readNullable[String] and
      (__ \ "testDateApprovalCheck").readNullable[String]
  )(RPExtra.apply _)

  implicit val jsonWrites: Writes[RPExtra] =
    (
      __.writeNullable[StringOrInt] and
        (__ \ "endDate").writeNullable[String] and
        (__ \ "status").writeNullable[String] and
        (__ \ "retestFlag").writeNullable[Boolean] and
        (__ \ "retest").writeNullable[String] and
        (__ \ "testResultFitAndProper").writeNullable[String] and
        (__ \ "testDateFitAndProper").writeNullable[String] and
        (__ \ "testResultApprovalCheck").writeNullable[String] and
        (__ \ "testDateApprovalCheck").writeNullable[String]
    )(unlift(RPExtra.unapply))

  implicit def conv(rp: ResponsiblePeople): RPExtra =
    RPExtra(
      rp.lineId.fold[Option[StringOrInt]](None)(x => Some(StringOrInt(x.toString))),
      None,
      rp.lineId.fold[Option[String]](None)(_ => if (rp.hasChanged) rp.status else Some(StatusConstants.Unchanged)),
      None,
      None,
      None,
      None,
      None,
      None
    )
}
