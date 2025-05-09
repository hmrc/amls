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

package models.fe.responsiblepeople

import models.des.DesConstants
import models.fe.responsiblepeople.TimeAtAddress.{SixToElevenMonths, ThreeYearsPlus, ZeroToFiveMonths}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

import java.time.LocalDate

class ResponsiblePeopleSpec extends PlaySpec with GuiceOneAppPerSuite with ResponsiblePeopleValues {

  "ResponsiblePeople" must {

    "validate complete json" must {

      "serialise as expected" in {
        Json.toJson(CompleteResponsiblePeople) must be(CompleteJson)
      }

      "deserialise as expected" in {
        CompleteJson.as[ResponsiblePeople] must be(CompleteResponsiblePeople)
      }
    }

    "convert des model to frontend model" in {
      ResponsiblePeople.convert(Some(DesConstants.testResponsiblePersonsForRpPhase2)) must be(
        DefaultValues.convertedModelPhase2
      )
    }
  }
}

trait ResponsiblePeopleValues {

  object DefaultValues {

    private val residence               = UKResidence("AA1111111")
    private val residenceCountry        = "GB"
    private val residenceNationality    = "GB"
    private val currentPersonAddress    = PersonAddressUK("Line 1", Some("Line 2"), None, None, "AA1 1AA")
    private val currentAddress          = ResponsiblePersonAddress(currentPersonAddress, ZeroToFiveMonths)
    private val additionalPersonAddress = PersonAddressUK("Line 1", Some("Line 2"), None, None, "BB1 1BB")
    private val additionalAddress       = ResponsiblePersonAddress(additionalPersonAddress, ZeroToFiveMonths)
    val previousName                    = PreviousName(true, Some("ABCD"), Some("XYZ"), Some("Fly"))

    val personName = PersonName(
      firstName = "name",
      middleName = Some("middle name"),
      lastName = "surname"
    )

    val personResidenceType = PersonResidenceType(residence, residenceCountry, residenceNationality)
    val saRegistered        = SaRegisteredYes("0123456789")
    val contactDetails      = ContactDetails("07000001122", "test@test.com")
    val addressHistory      = ResponsiblePersonAddressHistory(Some(currentAddress), Some(additionalAddress))
    val vatRegistered       = VATRegisteredNo
    val training            = TrainingYes("test")
    val experienceTraining  = ExperienceTrainingYes("Some training")
    val positions           = Positions(Set(BeneficialOwner, InternalAccountant), Some(LocalDate.now))
    val ukPassport          = UKPassportYes("87654321")
    val approvalFlags       = ApprovalFlags(Some(false), Some(true))

    val convertedModel: Option[List[ResponsiblePeople]] = Some(
      List(
        ResponsiblePeople(
          personName = Some(PersonName("FirstName", Some("MiddleName"), "LastName")),
          legalName = Some(PreviousName(true, Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          legalNameChangeDate = Some(LocalDate.of(2001, 1, 1)),
          knownBy = Some(KnownBy(true, Some("Aliases1"))),
          personResidenceType = Some(PersonResidenceType(NonUKResidence, "AA", "AA")),
          ukPassport = Some(UKPassportYes("AA1111111")),
          nonUKPassport = Some(NoPassport),
          dateOfBirth = Some(DateOfBirth(LocalDate.of(2001, 1, 1))),
          contactDetails = None,
          addressHistory = Some(
            ResponsiblePersonAddressHistory(
              Some(
                ResponsiblePersonAddress(
                  PersonAddressUK(
                    "CurrentAddressLine1",
                    Some("CurrentAddressLine2"),
                    Some("CurrentAddressLine3"),
                    Some("CurrentAddressLine4"),
                    "AA1 1AA"
                  ),
                  ThreeYearsPlus
                )
              ),
              None,
              None
            )
          ),
          positions = Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(LocalDate.now))),
          saRegistered = Some(SaRegisteredYes("1234567890")),
          vatRegistered = Some(VATRegisteredYes("123456789")),
          experienceTraining = Some(ExperienceTrainingNo),
          training = Some(TrainingYes("TrainingDetails")),
          approvalFlags = ApprovalFlags(Some(false), None),
          lineId = Some(333333)
        ),
        ResponsiblePeople(
          personName = Some(PersonName("bbbbbbbbbbbb", Some("bbbbbbbbbbb"), "bbbbbbbbbbb")),
          legalName = Some(PreviousName(true, Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
          legalNameChangeDate = Some(LocalDate.of(1967, 8, 13)),
          knownBy = Some(KnownBy(true, Some("bbbbbbbbbbb"))),
          personResidenceType = Some(PersonResidenceType(UKResidence("BB000000A"), "GB", "GB")),
          ukPassport = None,
          nonUKPassport = None,
          dateOfBirth = None,
          contactDetails = None,
          addressHistory = Some(
            ResponsiblePersonAddressHistory(
              Some(
                ResponsiblePersonCurrentAddress(
                  PersonAddressUK("b", Some("b"), Some("b"), Some("b"), "AA1 1AA"),
                  ZeroToFiveMonths
                )
              ),
              Some(
                ResponsiblePersonAddress(
                  PersonAddressUK("b", Some("b"), Some("b"), Some("b"), "AA1 1AA"),
                  ZeroToFiveMonths
                )
              ),
              Some(
                ResponsiblePersonAddress(
                  PersonAddressUK("a", Some("a"), Some("a"), Some("a"), "AA1 1AA"),
                  SixToElevenMonths
                )
              )
            )
          ),
          positions = Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(LocalDate.now))),
          saRegistered = Some(SaRegisteredYes("1111111111")),
          vatRegistered = Some(VATRegisteredYes("111111111")),
          experienceTraining = Some(ExperienceTrainingYes("bbbbbbbbbb")),
          training = Some(TrainingNo),
          approvalFlags = ApprovalFlags(Some(true), None),
          lineId = Some(222222)
        )
      )
    )

    val convertedModelPhase2: Option[List[ResponsiblePeople]] = convertedModel.map { responsiblePersonSeq =>
      List(
        responsiblePersonSeq(0).copy(
          approvalFlags = responsiblePersonSeq(0).approvalFlags
            .copy(hasAlreadyPassedFitAndProper = Some(false), hasAlreadyPaidApprovalCheck = Some(true))
        ),
        responsiblePersonSeq(1).copy(
          dateOfBirth = Some(DateOfBirth(LocalDate.of(2001, 1, 1))),
          approvalFlags = responsiblePersonSeq(1).approvalFlags
            .copy(hasAlreadyPassedFitAndProper = Some(true), hasAlreadyPaidApprovalCheck = Some(false))
        )
      )
    }

  }

  object NewValues {

    private val residence                  = NonUKResidence
    private val residenceCountry           = "GB"
    private val residenceNationality       = "GB"
    private val newPersonAddress           = PersonAddressNonUK("Line 1", Some("Line 2"), None, None, "ES")
    private val newAdditionalPersonAddress = PersonAddressNonUK("Line 1", Some("Line 2"), None, None, "FR")
    private val currentAddress             = ResponsiblePersonAddress(newPersonAddress, ZeroToFiveMonths)
    private val additionalAddress          = ResponsiblePersonAddress(newAdditionalPersonAddress, ZeroToFiveMonths)

    val personName          = PersonName("first", Some("middle"), "last")
    val previousName        = PreviousName(true, Some("Old"), Some("middle"), Some("Name"))
    val contactDetails      = ContactDetails("07000001122", "new@test.com")
    val addressHistory      = ResponsiblePersonAddressHistory(Some(currentAddress), Some(additionalAddress))
    val personResidenceType = PersonResidenceType(residence, residenceCountry, residenceNationality)
    val saRegistered        = SaRegisteredNo
    val vatRegistered       = VATRegisteredYes("12345678")
    val positions           = Positions(Set(Director, SoleProprietor), Some(LocalDate.now))
    val experienceTraining  = ExperienceTrainingNo
    val training            = TrainingNo

  }

  val CompleteResponsiblePeople = ResponsiblePeople(
    Some(DefaultValues.personName),
    Some(DefaultValues.previousName),
    Some(LocalDate.of(1990, 2, 24)),
    Some(KnownBy(true, Some("Doc"))),
    Some(DefaultValues.personResidenceType),
    Some(DefaultValues.ukPassport),
    None,
    Some(DateOfBirth(LocalDate.of(2001, 1, 1))),
    Some(DefaultValues.contactDetails),
    Some(DefaultValues.addressHistory),
    Some(DefaultValues.positions),
    Some(DefaultValues.saRegistered),
    Some(DefaultValues.vatRegistered),
    Some(DefaultValues.experienceTraining),
    Some(DefaultValues.training),
    DefaultValues.approvalFlags
  )

  val CompleteJson = Json.obj(
    "personName"          -> Json.obj(
      "firstName"  -> "name",
      "middleName" -> "middle name",
      "lastName"   -> "surname"
    ),
    "legalName"           -> Json
      .obj("hasPreviousName" -> true, "firstName" -> "ABCD", "middleName" -> "XYZ", "lastName" -> "Fly"),
    "legalNameChangeDate" -> "1990-02-24",
    "knownBy"             -> Json.obj(
      "hasOtherNames" -> true,
      "otherNames"    -> "Doc"
    ),
    "personResidenceType" -> Json.obj(
      "nino"           -> "AA1111111",
      "countryOfBirth" -> "GB",
      "nationality"    -> "GB"
    ),
    "ukPassport"          -> Json.obj(
      "ukPassport"       -> true,
      "ukPassportNumber" -> "87654321"
    ),
    "dateOfBirth"         -> Json.obj(
      "dateOfBirth" -> "2001-01-01"
    ),
    "approvalFlags"       -> Json.obj(
      "hasAlreadyPassedFitAndProper" -> false,
      "hasAlreadyPaidApprovalCheck"  -> true
    ),
    "contactDetails"      -> Json.obj(
      "phoneNumber"  -> "07000001122",
      "emailAddress" -> "test@test.com"
    ),
    "addressHistory"      -> Json.obj(
      "currentAddress"    -> Json.obj(
        "personAddress" -> Json.obj(
          "personAddressLine1"    -> "Line 1",
          "personAddressLine2"    -> "Line 2",
          "personAddressPostCode" -> "AA1 1AA"
        ),
        "timeAtAddress" -> Json.obj(
          "timeAtAddress" -> "01"
        )
      ),
      "additionalAddress" -> Json.obj(
        "personAddress" -> Json.obj(
          "personAddressLine1"    -> "Line 1",
          "personAddressLine2"    -> "Line 2",
          "personAddressPostCode" -> "BB1 1BB"
        ),
        "timeAtAddress" -> Json.obj(
          "timeAtAddress" -> "01"
        )
      )
    ),
    "positions"           -> Json.obj(
      "positions" -> Seq("01", "03"),
      "startDate" -> LocalDate.now()
    ),
    "saRegistered"        -> Json.obj(
      "saRegistered" -> true,
      "utrNumber"    -> "0123456789"
    ),
    "vatRegistered"       -> Json.obj(
      "registeredForVAT" -> false
    ),
    "experienceTraining"  -> Json.obj(
      "experienceTraining"    -> true,
      "experienceInformation" -> "Some training"
    ),
    "training"            -> Json.obj(
      "training"    -> true,
      "information" -> "test"
    ),
    "hasChanged"          -> false
  )
}
