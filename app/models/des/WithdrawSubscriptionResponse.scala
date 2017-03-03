package models.des

import play.api.libs.json.Json

case class WithdrawSubscriptionResponse (processingDate: String)

object WithdrawSubscriptionResponse {
  implicit val format = Json.format[WithdrawSubscriptionResponse]
}
