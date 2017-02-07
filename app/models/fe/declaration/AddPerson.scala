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

package models.fe.declaration

import models.des.aboutyou.{AboutYouRelease7, Aboutyou}

case class AddPerson(firstName: String,
                     middleName: Option[String],
                     lastName: String,
                     roleWithinBusiness: RoleWithinBusiness
                    )

object AddPerson {

  import play.api.libs.json._

  implicit val jsonReads: Reads[AddPerson] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (
      (__ \ "firstName").read[String] and
        (__ \ "middleName").readNullable[String] and
        (__ \ "lastName").read[String] and
        __.read[RoleWithinBusiness]
      ) (AddPerson.apply _)

  }


  implicit val jsonWrites: Writes[AddPerson] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Writes._
    import play.api.libs.json._
    (
      (__ \ "firstName").write[String] and
        (__ \ "middleName").writeNullable[String] and
        (__ \ "lastName").write[String] and
        __.write[RoleWithinBusiness]
      ) (unlift(AddPerson.unapply))
  }

  implicit def conv(aboutYou: AboutYouRelease7): AddPerson = {
    aboutYou.individualDetails match {
      case Some(dtls) => AddPerson(dtls.firstName, dtls.middleName, dtls.lastName, RoleWithinBusiness.conv(aboutYou))
      case None => AddPerson("", None, "", RoleWithinBusiness.conv(aboutYou))
    }
  }
}

case class AddPersonRelease7(firstName: String,
                             middleName: Option[String],
                             lastName: String,
                             roleWithinBusiness: models.fe.declaration.release7.RoleWithinBusiness
                            )

object AddPersonRelease7 {

  import play.api.libs.json._

  implicit val jsonReads: Reads[AddPersonRelease7] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (
      (__ \ "firstName").read[String] and
        (__ \ "middleName").readNullable[String] and
        (__ \ "lastName").read[String] and
        __.read[models.fe.declaration.release7.RoleWithinBusiness]
      ) (AddPersonRelease7.apply _)

  }


  implicit val jsonWrites: Writes[AddPersonRelease7] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Writes._
    import play.api.libs.json._
    (
      (__ \ "firstName").write[String] and
        (__ \ "middleName").writeNullable[String] and
        (__ \ "lastName").write[String] and
        __.write[models.fe.declaration.release7.RoleWithinBusiness]
      ) (unlift(AddPersonRelease7.unapply))
  }

  def convert(aboutYou: AboutYouRelease7): AddPersonRelease7 = {

    aboutYou.individualDetails match {
      case Some(details) => {
        AddPersonRelease7(
          details.firstName,
          details.middleName,
          details.lastName,
          models.fe.declaration.release7.RoleWithinBusiness.convert(aboutYou))
      }
      case None =>
        AddPersonRelease7(
          "",
          None,
          "",
          models.fe.declaration.release7.RoleWithinBusiness.convert(aboutYou)
        )
    }
  }
}
