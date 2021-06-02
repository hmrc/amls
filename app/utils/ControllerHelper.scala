/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import play.api.libs.json.{JsPath, Json, JsonValidationError}

trait ControllerHelper {

  val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r

  def toError(errors: Seq[(JsPath, Seq[JsonValidationError])]) = Json.obj(
    "errors" -> (errors map {
      case (path, error) =>
        Json.obj(
          "path" -> path.toJsonString,
          "error" -> error.head.message
        )
    })
  )

  def toError(message: String) = Json.obj(
    "errors" -> Seq(message)
  )

}
