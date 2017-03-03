package controllers

import connectors.{DESConnector, WithdrawSubscriptionConnector}
import play.api.mvc.Action
import uk.gov.hmrc.play.microservice.controller.BaseController


import scala.concurrent.Future

trait WithdrawSubscriptionController extends BaseController {

  val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r

  private[controllers] def withdrawSubscriptionConnector: WithdrawSubscriptionConnector

  def withdrawal(amlsRegistrationNumber: String) = Action.async(parse.json) {
    implicit request =>
      Future.successful(Ok)
  }
}

object WithdrawSubscriptionController extends WithdrawSubscriptionController {
  override private[controllers] val withdrawSubscriptionConnector = DESConnector
}
