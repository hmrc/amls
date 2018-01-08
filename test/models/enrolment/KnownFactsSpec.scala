package models.enrolment

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class KnownFactsSpec extends PlaySpec{


  "The model" must {
    "serialize to the correct Json" in new Fixture {
      val model = EnrolmentStoreEnrolment(userId, postCode)

      val expectedJson = Json.obj(
        "userId" -> userId,
        "friendlyName" -> "AMLS Enrolment",
        "type" -> "principal",
        "verifiers" -> Seq(
          EnrolmentIdentifier("Postcode", postCode)
        )
      )

      Json.toJson(model) mustBe expectedJson
    }
  }


}
