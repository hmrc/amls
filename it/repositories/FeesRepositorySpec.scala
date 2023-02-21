package repositories

import models.{AmendOrVariationResponseType, Fees}
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.LocalDateTime

class FeesRepositorySpec extends AnyFreeSpec with Matchers with DefaultPlayMongoRepositorySupport[Fees] with IntegrationPatience {

  override lazy val repository = new FeesRepository(mongoComponent)

  "Fees Repository" - {

    "must insert fees" in {
      // Given
      val fee = Fees(
        responseType = AmendOrVariationResponseType,
        amlsReferenceNumber = "XBML00000567882",
        paymentReference = Some("XA1000000000208"),
        difference = Some(BigDecimal(100)),
        createdAt = LocalDateTime.parse("2022-10-17T10:08:28.462"),
        fpFee = None)

      // When
      val inserted = repository.insert(fee).futureValue

      // Then
      inserted mustEqual true
    }

    "must find latest fee by AMLS reference" in {
      // Given
      val feeOne = Fees(
        responseType = AmendOrVariationResponseType,
        amlsReferenceNumber = "XBML00000567882",
        paymentReference = Some("XA1000000000208"),
        difference = Some(BigDecimal(100)),
        createdAt = LocalDateTime.parse("2022-10-17T10:08:28.462"),
        fpFee = None)

      val feeTwo = Fees(
        responseType = AmendOrVariationResponseType,
        amlsReferenceNumber = "XAML00000567890",
        paymentReference = Some("XA1000000000208"),
        difference = Some(BigDecimal(100)),
        createdAt = LocalDateTime.parse("2022-10-17T10:08:28.462"),
        fpFee = None)

      val feeThree = Fees(
        responseType = AmendOrVariationResponseType,
        amlsReferenceNumber = "XBML00000567882",
        paymentReference = Some("XA1000000000208"),
        difference = Some(BigDecimal(100)),
        createdAt = LocalDateTime.parse("2022-10-22T10:08:28.462"),
        fpFee = None)

      repository.insert(feeOne).futureValue
      repository.insert(feeTwo).futureValue
      repository.insert(feeThree).futureValue

      // When
      val foundFee = repository.findLatestByAmlsReference("XBML00000567882").futureValue.head

      // Then
      foundFee mustEqual feeThree
    }
  }
}
