package models.des

import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait WithdrawalReason

object WithdrawalReason {

  case object OutOfscope extends WithdrawalReason
  case object NotTradingInOwnRight extends WithdrawalReason
  case object UnderAnotherSupervisor extends WithdrawalReason
  case object JoinedAWRSGroup extends WithdrawalReason
  case object Other extends WithdrawalReason

  implicit val jsonServiceReads: Reads[WithdrawalReason] =
    Reads {
      case JsString("Out of scope") => JsSuccess(OutOfscope)
      case JsString("Not trading in own right") => JsSuccess(NotTradingInOwnRight)
      case JsString("Under another supervisor") => JsSuccess(UnderAnotherSupervisor)
      case JsString("Joined AWRS Group") => JsSuccess(JoinedAWRSGroup)
      case JsString("Other, please specify") => JsSuccess(Other)
      case _ => JsError((JsPath \ "withdrawalReason") -> ValidationError("error.invalid"))
    }

  implicit val jsonServiceWrites =
    Writes[WithdrawalReason] {
      case OutOfscope => JsString("Out of scope")
      case NotTradingInOwnRight => JsString("Not trading in own right")
      case UnderAnotherSupervisor => JsString("Under another supervisor")
      case JoinedAWRSGroup => JsString("Joined AWRS Group")
      case Other => JsString("Other, please specify")
    }
}

case class WithdrawSubscriptionRequest (acknowledgementReference: String,
                                        withdrawalDate: String,
                                        withdrawalReason: WithdrawalReason,
                                        withdrawalReasonOthers: Option[String]
                                       )

object WithdrawSubscriptionRequest {

  implicit val format = Json.format[WithdrawSubscriptionRequest]
}