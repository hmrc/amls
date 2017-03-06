package controllers

import connectors.{DESConnector, WithdrawSubscriptionConnector}
import models.des.WithdrawSubscriptionRequest
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc.Action
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DeregisterSubscriptionController extends BaseController {

  private val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r

  private[controllers] def deregisterSubscriptionConnector: DeregisterSubscriptionConnector

  private def toError(errors: Seq[(JsPath, Seq[ValidationError])]) = Json.obj(
    "errors" -> (errors map {
      case (path, error) =>
        Json.obj(
          "path" -> path.toJsonString,
          "error" -> error.head.message
        )
    })
  )

  private def toError(message: String) = Json.obj(
      "errors" -> Seq(message)
  )

  def deregistration(amlsRegistrationNumber: String) = Action.async(parse.json) {
    implicit request =>
      amlsRegNoRegex.findFirstMatchIn(amlsRegistrationNumber) match {
        case Some(_) => {
          Json.fromJson[DeregisterSubscriptionRequest](request.body) match {
            case JsSuccess(body,_) =>
              deregisterSubscriptionConnector.deregistration(amlsRegistrationNumber, body) map {
                response =>
                  Ok(Json.toJson(response))
              }
            case JsError(errors) =>
              Future.successful(BadRequest(toError(errors)))
          }
        }
        case None =>
          Future.successful {
            BadRequest(toError("Invalid amlsRegistrationNumber"))
          }
      }
  }
}


object DeregisterSubscriptionController extends DeregisterSubscriptionController {
  override private[controllers] val deregisterSubscriptionConnector = DESConnector
}