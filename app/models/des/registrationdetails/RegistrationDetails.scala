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

package models.des.registrationdetails

import org.joda.time.LocalDate
import play.api.libs.json._

sealed trait OrganisationType
case object Partnership extends OrganisationType
case object LLP extends OrganisationType
case object CorporateBody extends OrganisationType
case object UnincorporatedBody extends OrganisationType

object OrganisationType {

  val orgTypeToString = Map[OrganisationType, String](
    Partnership -> "Partnership",
    LLP -> "LLP",
    CorporateBody -> "Corporate body",
    UnincorporatedBody -> "Unincorporated body"
  )

  val stringToOrgType = orgTypeToString.map(_.swap)

  implicit val reads = new Reads[OrganisationType] {
    override def reads(json: JsValue) = json match {
      case JsString(x) if stringToOrgType.isDefinedAt(x) => JsSuccess(stringToOrgType(x))
      case x => JsError(s"Unable to parse the organisation type value: $x")
    }
  }

  implicit val writes = new Writes[OrganisationType] {
    override def writes(o: OrganisationType) = o match {
      case org if orgTypeToString.isDefinedAt(org) => JsString(orgTypeToString(org))
      case _ => throw new Exception("Unable to convert org type to string")
    }
  }
}

sealed trait OrganisationBodyDetails
case class Organisation(organisationName: String, isAGroup: Option[Boolean] = None, organisationType: Option[OrganisationType] = None) extends OrganisationBodyDetails

object Organisation {
  implicit val format = Json.format[Organisation]
}

case class Individual(firstName: String, middleName: Option[String], lastName: String) extends OrganisationBodyDetails

object Individual {
  implicit val format = Json.format[Individual]
}

object OrganisationBodyDetails {
  import play.api.libs.json._

  implicit val reads: Reads[OrganisationBodyDetails] = {
    (__ \ "isAnIndividual").read[Boolean] flatMap {
      case true => (__ \ "individual").read[Individual].map(identity[OrganisationBodyDetails])
      case _ => (__ \ "organisation").read[Organisation].map(identity[OrganisationBodyDetails])
    }
  }

  implicit val writes = new Writes[OrganisationBodyDetails] {
    override def writes(o: OrganisationBodyDetails) = o match {
      case x: Organisation => Json.obj("organisation" -> Organisation.format.writes(x))
      case x: Individual => Json.obj("individual" -> Individual.format.writes(x))
    }
  }
}

case class RegistrationDetails(isAnIndividual: Boolean, bodyDetails: OrganisationBodyDetails)

object RegistrationDetails {
  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val reads: Reads[RegistrationDetails] = {
    (
      (__ \ "isAnIndividual").read[Boolean] and
        __.read[OrganisationBodyDetails]
    )(RegistrationDetails.apply _)
  }

  implicit val writes: Writes[RegistrationDetails] = {
    (
      (__ \ "isAnIndividual").write[Boolean] and
      __.write[OrganisationBodyDetails]
      )(unlift(RegistrationDetails.unapply))
  }
}
