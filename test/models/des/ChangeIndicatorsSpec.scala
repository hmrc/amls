package models.des

import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class ChangeIndicatorsSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  "ChangeIndicators" must {
    "serialize correctly" in {

      val json = Json.parse(
        """{
    "businessDetails": false,
    "businessAddress": false,
    "businessReferences": true,
    "tradingPremises": true,
    "businessActivities": true,
    "bankAccountDetails": true,
    "msb": true,
    "hvd": false,
    "asp": true,
    "aspOrTcsp": false,
    "tcsp": true,
    "eab": true,
    "responsiblePersons": false,
    "filingIndividual": true
  }""")

      val changeIndicators = ChangeIndicators(false,false,true,true,true,true,true,false,true,false,true,true,false,true)

      ChangeIndicators.format.writes(changeIndicators) must be(json)

    }
  }

}

class ChangeIndicatorsR7Spec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))

  "ChangeIndicators" must {
    "serialize correctly" in {

      val json = Json.parse(
        """{
    "businessDetails": false,
    "businessAddress": false,
    "businessReferences": true,
    "tradingPremises": true,
    "businessActivities": true,
    "bankAccountDetails": true,
    "msb": {
    "msb": true
    },
    "hvd": {
    "hvd": false
    },
    "asp": {
    "asp": true
    },
    "aspOrTcsp": {
    "aspOrTcsp": false
    },
    "tcsp": {
    "tcsp": true
    },
    "eab": {
    "eab": true
    },
    "responsiblePersons": false,
    "filingIndividual": true
  }""")

      val changeIndicators = ChangeIndicators(false,false,true,true,true,true,true,false,true,false,true,true,false,true)

      ChangeIndicators.format.writes(changeIndicators) must be(json)

    }
  }

}
