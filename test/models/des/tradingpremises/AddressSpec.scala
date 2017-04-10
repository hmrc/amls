package models.des.tradingpremises

import org.scalatestplus.play.PlaySpec

class AddressSpec extends PlaySpec {

  "convert to fe address to des Address when post code is empty" in {

    val desAddress = Address (
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      "GB",
      None
    )

    val feAddress = models.fe.tradingpremises.Address(
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      ""
    )
    Address.convert(feAddress) must be(desAddress)
  }

  "convert to fe address to des Address when post code is not empty" in {

    val desAddress = Address (
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      "GB",
      Some("postcode")
    )

    val feAddress = models.fe.tradingpremises.Address(
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      "postcode"
    )
    Address.convert(feAddress) must be(desAddress)
  }


}
