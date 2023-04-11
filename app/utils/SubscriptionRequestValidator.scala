/*
 * Copyright 2023 HM Revenue & Customs
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

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import models.des.SubscriptionRequest
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}

import java.io.InputStream
import javax.inject.Singleton

@Singleton
class SubscriptionRequestValidator {

  def validateRequest(request: SubscriptionRequest): Either[Seq[JsObject], SubscriptionRequest] = {

    val stream: InputStream = getClass.getResourceAsStream("/resources/api4_schema_release_5.1.0.json")

    val lines = scala.io.Source.fromInputStream(stream).getLines
    val linesString: String = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)

    val result: JsResult[JsValue] = SchemaValidator().validate(Json.fromJson[SchemaType](Json.parse(linesString.trim)).get, Json.toJson(request))

    result.asEither match {
      case Left(validationErrors) =>
        val reasons = validationErrors.map {
          case (path, messages) => Json.obj(
            "path" -> path.toJsonString,
            "messages" -> messages.map(_.messages)
          )
        }
        Left(reasons)
      case Right(_) => Right(request)
    }
  }
}
