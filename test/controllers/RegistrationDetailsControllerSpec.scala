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

package controllers

import connectors.RegistrationDetailsDesConnector
import models.fe.registrationdetails.RegistrationDetails
import models.des.registrationdetails.{Organisation, Partnership, RegistrationDetails => DesRegistrationDetails}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class RegistrationDetailsControllerSpec extends PlaySpec with MustMatchers with ScalaFutures with MockitoSugar {

  implicit val hc = HeaderCarrier()

  val controller = new RegistrationDetailsController {
    override private[controllers] val registrationDetailsConnector = mock[RegistrationDetailsDesConnector]
  }

  "The RegistrationDetailsController" must {
    "use the Des connector to retrieve registration details" in {
      val safeId = "SAFEID"

      val desDetails = DesRegistrationDetails(isAnIndividual = false, Organisation("Test Company", isAGroup = Some(false), Some(Partnership)))
      val feDetails = RegistrationDetails("Test Company", isIndividual = false)

      when {
        controller.registrationDetailsConnector.getRegistrationDetails(eqTo(safeId))(any(), any())
      } thenReturn Future.successful(desDetails)

      val response = controller.get("account", "ref", safeId)(FakeRequest())

      status(response) mustBe OK
      contentAsJson(response) mustBe Json.toJson(feDetails)
      verify(controller.registrationDetailsConnector).getRegistrationDetails(eqTo(safeId))(any(), any())
    }
  }

}
