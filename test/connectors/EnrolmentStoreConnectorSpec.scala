package connectors

import config.WSHttp
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers._
import sun.security.krb5.internal.AuthContext
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.auth.microservice.connectors.AuthConnector

import scala.concurrent.Future

class EnrolmentStoreConnectorSpec extends PlaySpec
  with MustMatchers
  with ScalaFutures
  with MockitoSugar
  with AmlsReferenceNumberGenerator
  with BaseGenerator {

  trait Fixture {

    implicit val headerCarrier = HeaderCarrier()
    implicit val authContext = mock[AuthContext]

    val http = mock[WSHttp]
    val appConfig = mock[AppConfig]
    val authConnector = mock[AuthConnector]

    val connector = new EnrolmentStoreConnector(http, appConfig, authConnector)
    val baseUrl = "http://tax-enrolments:3001"
    val userDetails = UserDetailsResponse("Test User", None, "123456789", "Organisation")
    val enrolKey = AmlsEnrolmentKey(amlsRegistrationNumber)

    when {
      appConfig.enrolmentStoreUrl
    } thenReturn baseUrl

    when {
      authConnector.userDetails(any(), any(), any())
    } thenReturn Future.successful(userDetails)
  }

  "enrol" when {
    "called" must {
      "call the ES6 enrolment store endpoint for known facts" in new Fixture {
        val enrolment = EnrolmentStoreEnrolment("123456789", postcodeGen.sample.get)
        val endpointUrl = s"$baseUrl/tax-enrolments/groups/${userDetails.affinityGroup}/enrolments/${enrolKey.key}"

        when {
          http.POST[EnrolmentStoreEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any())
        } thenReturn Future.successful(HttpResponse(OK))

        whenReady(connector.enrol(enrolKey, enrolment)) { _ =>
          verify(authConnector).userDetails(any(), any(), any())
          verify(http).POST[EnrolmentStoreEnrolment, HttpResponse](eqTo(endpointUrl), eqTo(enrolment), any())(any(), any(), any(), any())
        }
      }
    }
  }

}