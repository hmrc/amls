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

import com.eclipsesource.schema.drafts.Version4.schemaTypeReads
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.main.JsonSchemaFactory
import models.des.SubscriptionRequest
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}

import java.io.InputStream
import javax.inject.Singleton
import scala.util.Using
import scala.io.Source

@Singleton
class SubscriptionRequestValidator {

  def validateRequest(request: SubscriptionRequest): Either[collection.Seq[JsObject], SubscriptionRequest] = {

    val stream = scala.io.Source.fromResource("api4_schema_release_5.1.0.json").mkString

    lazy val jsonMapper = new ObjectMapper()
    lazy val jsonFactory = jsonMapper.getFactory
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory
    val schemaParser: JsonParser = factory.createParser(stream)
    val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
    val schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
    val jsonParser = jsonFactory.createParser(Json.toJson(request).toString())
    val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
    val result = schema.validate(jsonNode).isSuccess


    result match {
      case false =>
      val validationResult = SchemaValidator().validate(Json.fromJson[SchemaType](Json.parse(stream)).get, Json.toJson(request)).asEither

        val reasons: collection.Seq[JsObject] = validationResult match {
          case Left(validationErrors) => validationErrors.map {
            case (path, messages) => Json.obj(
              "path" -> path.toJsonString,
              "messages" -> messages.map(_.messages)
            )
          }
          case Right(_) => collection.Seq[JsObject]()
        }
        Left(reasons)
      case true => Right(request)
    }
  }
}