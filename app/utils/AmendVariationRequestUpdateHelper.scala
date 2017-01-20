package utils

import connectors.{DESConnector, ViewDESConnector}

trait AmendVariationRequestUpdateHelper {
  val viewDesConnector: ViewDESConnector = _
  val amlsRegNo: String = _
  private val view = viewDesConnector.view(amlsRegNo)
}

object AmendVariationRequestUpdateHelper extends AmendVariationRequestUpdateHelper{

  def apply(amlsRegNo: String): AmendVariationRequestUpdateHelper ={
    new AmendVariationRequestUpdateHelper{
      override val viewDesConnector: ViewDESConnector = DESConnector
      override val amlsRegNo: String = amlsRegNo
    }
  }

}