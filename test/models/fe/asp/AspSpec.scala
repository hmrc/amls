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

package models.fe.asp

import models.des.DesConstants
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

trait AspValues {

  object DefaultValues {

    val DefaultOtherBusinessTax = OtherBusinessTaxMattersYes

    val DefaultServices = ServicesOfBusiness(Set(Accountancy, Auditing, FinancialOrTaxAdvice))
  }

  object NewValues {

    val NewOtherBusinessTax = OtherBusinessTaxMattersNo

    val NewServices = ServicesOfBusiness(Set(Accountancy, PayrollServices, FinancialOrTaxAdvice))
  }

  val completeJson = Json.obj(
    "services" -> Json.obj(
      "services" -> Seq("01", "04", "05")
    ),
    "otherBusinessTaxMatters" -> Json.obj(
      "otherBusinessTaxMatters" -> true)
  )
  val completeModel = Asp(
    Some(DefaultValues.DefaultServices),
    Some(DefaultValues.DefaultOtherBusinessTax)
  )

}

class AspSpec extends PlaySpec with MockitoSugar with AspValues {

  "Asp" must {

    "have a default function that" must {

      "correctly provides a default value when none is provided" in {
        Asp.default(None) must be(Asp())
      }

      "correctly provides a default value when existing value is provided" in {
        Asp.default(Some(completeModel)) must be(completeModel)
      }

    }

    "Complete Model" when {

      "correctly convert between json formats" when {

        "Serialise as expected" in {
          Json.toJson(completeModel) must be(completeJson)
        }

        "Deserialise as expected" in {
          completeJson.as[Asp] must be(completeModel)
        }
      }
    }

    "None" when {

      val initial: Option[Asp] = None

      "Merged with other business tax matters" must {
        "return Asp with correct other business tax matters" in {

          val result = initial.otherBusinessTaxMatters(NewValues.NewOtherBusinessTax)
          result must be(Asp(otherBusinessTaxMatters = Some(NewValues.NewOtherBusinessTax)))

        }

        "Merged with services does your business provide" must {
          "return Asp with correct services does your business provide" in {

            val result = initial.services(NewValues.NewServices)
            result must be(Asp(services = Some(NewValues.NewServices)))

          }
        }

      }
    }

    "Asp:merge with completeModel" when {

      "model is complete" when {

        "Merged with other business tax matters" must {
          "return Asp with correct Company Service Providers" in {

            val result = completeModel.otherBusinessTaxMatters(NewValues.NewOtherBusinessTax)
            result.otherBusinessTaxMatters must be(Some(NewValues.NewOtherBusinessTax))

          }
        }

        "Merged with services does your business provide" must {
          "return Asp with correct services does your business provide" in {

            val result = completeModel.services(NewValues.NewServices)
            result.services must be(Some(NewValues.NewServices))

          }
        }
      }
    }

    "converting the des subscription model must yield a frontend Hvd model" in {
      Asp.conv(DesConstants.SubscriptionViewModel) must be(
        Some(
          Asp(
            Some(ServicesOfBusiness(Set(Auditing, FinancialOrTaxAdvice, BookKeeping, PayrollServices, Accountancy))),
            Some(OtherBusinessTaxMattersYes)
          )))
    }

    "convert to None given view.asp = None" in {
      Asp.conv(DesConstants.SubscriptionViewModel.copy(asp = None,businessActivities = models.des.businessactivities.BusinessActivities())) must be(None)
    }

    "convert to Some given view.asp = None but asp activities supplied" in {
      Asp.conv(DesConstants.SubscriptionViewModel.copy(asp = None)) must be(Some(Asp(DesConstants.SubscriptionViewModel.businessActivities,Some(OtherBusinessTaxMattersNo))))
    }

  }
}
