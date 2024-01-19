/*
 * Copyright 2024 HM Revenue & Customs
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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import models.des.AmendVariationRequest
import play.api.libs.json.jackson.PlayJsonModule
import play.api.libs.json.{JsObject, Json}

import java.io.InputStream
import javax.inject.Singleton
import scala.collection.mutable.ListBuffer

@Singleton
class AmendVariationValidator {

  def validateResult(request: AmendVariationRequest): Either[collection.Seq[JsObject], AmendVariationRequest] = {
    // $COVERAGE-OFF$
    val stream: InputStream = getClass.getResourceAsStream("/resources/api6_schema_release_5.1.0.json")
    val lines = scala.io.Source.fromInputStream(stream).getLines().mkString

    lazy val jsonMapper = new ObjectMapper().registerModule(PlayJsonModule)
    lazy val jsonFactory = jsonMapper.getFactory
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory
    val schemaParser: JsonParser = factory.createParser(lines)
    val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
    val schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
    val jsonParser = jsonFactory.createParser(Json.toJson(request).toString())
    val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
    val report: ProcessingReport = schema.validate(jsonNode)
    val result = report.isSuccess

    if (result) {
      Right(request)
    } else {
      val errors = new ListBuffer[JsObject]()
      report.iterator().forEachRemaining(pm => errors += jsonMapper.treeToValue(pm.asJson(), classOf[JsObject]))
      Left(errors.toSeq)
    }
  }
}
