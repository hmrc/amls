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

package models.fe.tcsp

import models.des.{DesConstants, SubscriptionView}
import models.des.tcsp.TcspTrustCompFormationAgt
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import utils.AmlsBaseSpec

trait TcspValues {

  object DefaultValues {

    val DefaultProvidedServices        = ProvidedServices(Set(PhonecallHandling, Other("other service")))
    val DefaultCompanyServiceProviders = TcspTypes(
      Set(NomineeShareholdersProvider, TrusteeProvider, CompanyDirectorEtc, CompanyFormationAgent)
    )
    val DefaultDoServicesOfAnotherTCSP = true
    val DefaultServicesOfAnotherTCSP   = ServicesOfAnotherTCSPYes(Some("12345678"))

  }

  object NewValues {

    val NewProvidedServices        = ProvidedServices(Set(EmailHandling))
    val NewCompanyServiceProviders = TcspTypes(Set(NomineeShareholdersProvider, CompanyFormationAgent))
    val NewServicesOfAnotherTCSP   = ServicesOfAnotherTCSPNo

  }

  val completeJson = Json.obj(
    "tcspTypes"                    -> Json.obj(
      "serviceProviders" -> Seq("01", "02", "04", "05")
    ),
    "onlyOffTheShelfCompsSold"     -> Json.obj(
      "onlyOffTheShelfCompsSold" -> true
    ),
    "complexCorpStructureCreation" -> Json.obj(
      "complexCorpStructureCreation" -> false
    ),
    "providedServices"             -> Json.obj(
      "services" -> Seq("01", "08"),
      "details"  -> "other service"
    ),
    "doesServicesOfAnotherTCSP"    -> true,
    "servicesOfAnotherTCSP"        -> Json.obj(
      "servicesOfAnotherTCSP" -> true,
      "mlrRefNumber"          -> "12345678"
    )
  )

  val completeModel = Tcsp(
    Some(DefaultValues.DefaultCompanyServiceProviders),
    Some(OnlyOffTheShelfCompsSoldYes),
    Some(ComplexCorpStructureCreationNo),
    Some(DefaultValues.DefaultProvidedServices),
    Some(DefaultValues.DefaultDoServicesOfAnotherTCSP),
    Some(DefaultValues.DefaultServicesOfAnotherTCSP)
  )
}

class TcspSpec extends PlaySpec with AmlsBaseSpec with TcspValues {

  "Tcsp" must {

    "have a default function that" must {

      "correctly provides a default value when none is provided" in {
        Tcsp.default(None) must be(Tcsp())
      }

      "correctly provides a default value when existing value is provided" in {
        Tcsp.default(Some(completeModel)) must be(completeModel)
      }
    }

    "Complete Model" when {

      "correctly convert between json formats" when {

        "Serialise as expected" in {
          Json.toJson(completeModel) must be(completeJson)
        }

        "Deserialise as expected" in {
          completeJson.as[Tcsp] must be(completeModel)
        }
      }
    }

    "None" when {

      val initial: Option[Tcsp] = None

      "Merged with Company Service Providers" must {
        "return Tcsp with correct Company Service Providers" in {
          val result = initial.tcspTypes(NewValues.NewCompanyServiceProviders)
          result must be(Tcsp(tcspTypes = Some(NewValues.NewCompanyServiceProviders)))
        }
      }

      "Merged with Provided Services"        must {
        "return Tcsp with correct Provided Services" in {
          val result = initial.providedServices(NewValues.NewProvidedServices)
          result must be(Tcsp(providedServices = Some(NewValues.NewProvidedServices)))
        }
      }
      "Merged with services of another tcsp" must {
        "return Tcsp with correct services of another tcsp" in {
          val result = initial.servicesOfAnotherTCSP(NewValues.NewServicesOfAnotherTCSP)
          result must be(Tcsp(servicesOfAnotherTCSP = Some(NewValues.NewServicesOfAnotherTCSP)))
        }
      }
    }

    "Tcsp:merge with completeModel" when {

      "model is complete" when {

        "Merged with Company Service Providers" must {
          "return Tcsp with correct Company Service Providers" in {
            val result = completeModel.tcspTypes(NewValues.NewCompanyServiceProviders)
            result.tcspTypes must be(Some(NewValues.NewCompanyServiceProviders))
          }
        }

        "Merged with Provided Services" must {
          "return Tcsp with correct Provided Services" in {
            val result = completeModel.providedServices(NewValues.NewProvidedServices)
            result.providedServices must be(Some(NewValues.NewProvidedServices))
          }
        }

        "Merged with services of another tcsp" must {
          "return Tcsp with correct services of another tcsp" in {
            val result = completeModel.servicesOfAnotherTCSP(NewValues.NewServicesOfAnotherTCSP)
            result.servicesOfAnotherTCSP must be(Some(NewValues.NewServicesOfAnotherTCSP))
          }
        }
      }

    }
  }

  "converting the des subscription model must yield a frontend TCSP model" in {
    Tcsp.conv(DesConstants.SubscriptionViewModel) must
      be(
        Some(
          Tcsp(
            Some(
              TcspTypes(
                Set(
                  CompanyDirectorEtc,
                  NomineeShareholdersProvider,
                  TrusteeProvider,
                  RegisteredOfficeEtc,
                  CompanyFormationAgent
                )
              )
            ),
            Some(OnlyOffTheShelfCompsSoldYes),
            Some(ComplexCorpStructureCreationYes),
            Some(
              ProvidedServices(
                Set(
                  SelfCollectMailboxes,
                  ConferenceRooms,
                  PhonecallHandling,
                  EmailHandling,
                  Other("SpecifyOther"),
                  EmailServer
                )
              )
            ),
            Some(true),
            Some(ServicesOfAnotherTCSPYes(Some("111111111111111")))
          )
        )
      )
  }

  "converting the des subscription model must yield a frontend TCSP model where empty mlr number" in {
    Tcsp.conv(DesConstants.SubscriptionViewModelNoTcspMLR) must
      be(
        Some(
          Tcsp(
            Some(
              TcspTypes(
                Set(
                  CompanyDirectorEtc,
                  NomineeShareholdersProvider,
                  TrusteeProvider,
                  RegisteredOfficeEtc,
                  CompanyFormationAgent
                )
              )
            ),
            Some(OnlyOffTheShelfCompsSoldYes),
            Some(ComplexCorpStructureCreationYes),
            Some(
              ProvidedServices(
                Set(
                  SelfCollectMailboxes,
                  ConferenceRooms,
                  PhonecallHandling,
                  EmailHandling,
                  Other("SpecifyOther"),
                  EmailServer
                )
              )
            ),
            Some(true),
            Some(ServicesOfAnotherTCSPNo)
          )
        )
      )
  }

  "converting the des subscription where no tcsp must yield None" in {
    Tcsp.conv(DesConstants.SubscriptionViewModelNoTcsp) must
      be(None)
  }

  "converting the des subscription model must yield a frontend TCSP model (CompanyFormationAgent variation 1)" in {
    val testTcspTrustCompFormationAgt = TcspTrustCompFormationAgt(true, false)
    val SubscriptionViewModel         = SubscriptionView(
      etmpFormBundleNumber = "111111",
      DesConstants.testBusinessDetails,
      DesConstants.testViewBusinessContactDetails,
      DesConstants.testBusinessReferencesAll,
      Some(DesConstants.testbusinessReferencesAllButSp),
      Some(DesConstants.testBusinessReferencesCbUbLlp),
      DesConstants.testBusinessActivities,
      DesConstants.testTradingPremisesAPI5,
      DesConstants.testBankDetails,
      Some(DesConstants.testMsb),
      Some(DesConstants.testHvd),
      Some(DesConstants.testAsp),
      Some(DesConstants.testAspOrTcsp),
      Some(DesConstants.testTcspAll),
      Some(testTcspTrustCompFormationAgt),
      Some(DesConstants.testEabAll),
      Some(DesConstants.testEabResdEstAgncy),
      Some(DesConstants.testResponsiblePersons),
      Some(DesConstants.testAmp),
      None,
      DesConstants.extraFields
    )
    Tcsp.conv(SubscriptionViewModel) must
      be(
        Some(
          Tcsp(
            Some(
              TcspTypes(
                Set(
                  CompanyDirectorEtc,
                  NomineeShareholdersProvider,
                  TrusteeProvider,
                  RegisteredOfficeEtc,
                  CompanyFormationAgent
                )
              )
            ),
            Some(OnlyOffTheShelfCompsSoldYes),
            Some(ComplexCorpStructureCreationNo),
            Some(
              ProvidedServices(
                Set(
                  SelfCollectMailboxes,
                  ConferenceRooms,
                  PhonecallHandling,
                  EmailHandling,
                  Other("SpecifyOther"),
                  EmailServer
                )
              )
            ),
            Some(true),
            Some(ServicesOfAnotherTCSPYes(Some("111111111111111")))
          )
        )
      )
  }

  "converting the des subscription model must yield a frontend TCSP model (CompanyFormationAgent variation 2)" in {
    val testTcspTrustCompFormationAgt = TcspTrustCompFormationAgt(false, true)
    val SubscriptionViewModel         = SubscriptionView(
      etmpFormBundleNumber = "111111",
      DesConstants.testBusinessDetails,
      DesConstants.testViewBusinessContactDetails,
      DesConstants.testBusinessReferencesAll,
      Some(DesConstants.testbusinessReferencesAllButSp),
      Some(DesConstants.testBusinessReferencesCbUbLlp),
      DesConstants.testBusinessActivities,
      DesConstants.testTradingPremisesAPI5,
      DesConstants.testBankDetails,
      Some(DesConstants.testMsb),
      Some(DesConstants.testHvd),
      Some(DesConstants.testAsp),
      Some(DesConstants.testAspOrTcsp),
      Some(DesConstants.testTcspAll),
      Some(testTcspTrustCompFormationAgt),
      Some(DesConstants.testEabAll),
      Some(DesConstants.testEabResdEstAgncy),
      Some(DesConstants.testResponsiblePersons),
      Some(DesConstants.testAmp),
      None,
      DesConstants.extraFields
    )
    Tcsp.conv(SubscriptionViewModel) must
      be(
        Some(
          Tcsp(
            Some(
              TcspTypes(
                Set(
                  CompanyDirectorEtc,
                  NomineeShareholdersProvider,
                  TrusteeProvider,
                  RegisteredOfficeEtc,
                  CompanyFormationAgent
                )
              )
            ),
            Some(OnlyOffTheShelfCompsSoldNo),
            Some(ComplexCorpStructureCreationYes),
            Some(
              ProvidedServices(
                Set(
                  SelfCollectMailboxes,
                  ConferenceRooms,
                  PhonecallHandling,
                  EmailHandling,
                  Other("SpecifyOther"),
                  EmailServer
                )
              )
            ),
            Some(true),
            Some(ServicesOfAnotherTCSPYes(Some("111111111111111")))
          )
        )
      )
  }

}
