package repositories

import models.payapi.PaymentStatus.{Cancelled, Sent, Successful}
import models.payments.Payment
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.LocalDateTime

class PaymentRepositorySpec extends AnyFreeSpec with Matchers with DefaultPlayMongoRepositorySupport[Payment] with IntegrationPatience {

  override lazy val repository = new PaymentRepository(mongoComponent)

  "Payment Repository" - {

    "must insert a payment" in {
      // Given
      val payment = Payment("9f8bbb4adbfaca8991005f9a1a291bef", "XQML00000315655", "XJML00000265458", "XtnaqFrm5x", None,
        1000, Cancelled, LocalDateTime.parse("2023-02-21T10:04:54"), None, None)

      // When
      val insertedPayment = repository.insert(payment).futureValue

      // Then
      insertedPayment mustEqual payment
      repository.collection.countDocuments().head().futureValue mustEqual 1
    }

    "must update a payment" in {
      // Given
      val payment = Payment("006d7f4db2700ab06ad90e1dfacfdae8", "XZML00000219894", "XCML00000571981", "Xbowpvotcr", None,
        10000, Sent, LocalDateTime.parse("2023-02-21T10:08:12"), None, None)

      repository.insert(payment).futureValue

      // When
      val updateResult = repository.update(Payment("006d7f4db2700ab06ad90e1dfacfdae8", "XZML00000219894", "XCML00000571981", "Xbowpvotcr", None,
        10000, Successful, LocalDateTime.parse("2023-02-21T10:08:12"), None, Some(LocalDateTime.parse("2023-02-22T12:09:18")))).futureValue

      // Then
      updateResult.wasAcknowledged() mustEqual true
      updateResult.getMatchedCount mustEqual 1
      updateResult.getModifiedCount mustEqual 1
    }

    "must find latest payment by AMLS reference" in {
      // Given
      val paymentOne = Payment("9f8bbb4adbfaca8991005f9a1a291bef", "XQML00000315655", "XJML00000265458", "XtnaqFrm5x", None,
        1000, Cancelled, LocalDateTime.parse("2023-02-18T10:04:54"), None, None)

      val paymentTwo = Payment("006d7f4db2700ab06ad90e1dfacfdae8", "XQML00000315655", "XCML00000571981", "Xbowpvotcr", None,
        10000, Sent, LocalDateTime.parse("2023-02-19T10:08:12"), None, None)

      val paymentThree = Payment("1b2cb690adb104c750b8deca92c0a2a0", "XQML00000315655", "XCML00000571981", "Xbowpvotcr", None,
        10000, Sent, LocalDateTime.parse("2023-02-20T12:06:22"), None, None)

      val paymentFour = Payment("0bda961ef49d11e70491dbabbf30fc01", "XQML00000315655", "XCML00000571981", "Xbowpvotcr", None,
        10000, Sent, LocalDateTime.parse("2023-02-21T12:06:22"), None, None)

      repository.insert(paymentOne).futureValue
      repository.insert(paymentTwo).futureValue
      repository.insert(paymentThree).futureValue
      repository.insert(paymentFour).futureValue

      // When
      val foundPayment = repository.findLatestByAmlsReference("XQML00000315655").futureValue.head

      // Then
      foundPayment mustEqual paymentFour
    }

    "must find latest payment by payment reference" in {
      // Given
      val paymentOne = Payment("9f8bbb4adbfaca8991005f9a1a291bef", "XQML00000315655", "XJML00000265458", "XtnaqFrm5x", None,
        1000, Cancelled, LocalDateTime.parse("2023-02-18T10:04:54"), None, None)

      val paymentTwo = Payment("006d7f4db2700ab06ad90e1dfacfdae8", "XQML00000315655", "XCML00000571981", "XyjbptHlai", None,
        10000, Sent, LocalDateTime.parse("2023-02-19T10:08:12"), None, None)

      val paymentThree = Payment("1b2cb690adb104c750b8deca92c0a2a0", "XQML00000315655", "XCML00000571981", "XyjbptHlai", None,
        10000, Sent, LocalDateTime.parse("2023-02-20T12:06:22"), None, None)

      val paymentFour = Payment("0bda961ef49d11e70491dbabbf30fc01", "XQML00000315655", "XCML00000571981", "XcjlbephaB", None,
        10000, Sent, LocalDateTime.parse("2023-02-21T12:06:22"), None, None)

      repository.insert(paymentOne).futureValue
      repository.insert(paymentTwo).futureValue
      repository.insert(paymentThree).futureValue
      repository.insert(paymentFour).futureValue

      // When
      val foundPayment = repository.findLatestByPaymentReference("XyjbptHlai").futureValue.head

      // Then
      foundPayment mustEqual paymentThree
    }
  }
}
