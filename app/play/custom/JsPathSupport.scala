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

package play.custom

import play.api.Logger
import play.api.libs.json.{JsPath, Reads}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.{ISO_LOCAL_DATE_TIME, ofPattern}
import scala.util.{Failure, Success, Try}

object JsPathSupport {
  implicit class RichJsPath(path: JsPath) {
    val logger: Logger = Logger(this.getClass())

    def readLocalDateTime: Reads[LocalDateTime] = {
      (path \ "$date").read[Long].map { dateTime =>
        LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneOffset.UTC)
      } orElse {
        path.read[Long].map { dateTime =>
          LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneOffset.UTC)
        }
      }.orElse {
        Reads
          .at[String](path \ "$date")
          .map(dateTimeStr => LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
          .orElse {
            Reads.at[String](path).map(dateTimeStr => LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
          }
          .orElse {
            Reads.at[LocalDateTime](path)(MongoJavatimeFormats.localDateTimeReads)
          }
      }
    }

    /**
      * TODO For whatever reason we are ending up with dates like this in the database ...
      * TODO further investigation is needed, but for the sake of deploying this custom formatter is written
      */
    val readCreatedDate: Reads[LocalDateTime] = {
      path.read[String].map(localDateTimeStr => {
        println(s"\nattempting to read created at date: $localDateTimeStr\n")
        logger.debug(s"\nattempting to read created at date: $localDateTimeStr\n")
          Try(LocalDateTime.parse(localDateTimeStr, ofPattern("uuuu-dd-MM HH:mm:ss.SSSX")))
        })
        .flatMap {
          case Success(v) => Reads.pure(v)
          case Failure(exception) => Reads.failed[LocalDateTime](exception.getMessage)
        }
        .orElse {
          Reads.at[LocalDateTime](path)(MongoJavatimeFormats.localDateTimeReads)
        }
        .orElse {
          Reads.at[String](path).map(dateTimeStr => LocalDateTime.parse(dateTimeStr, ISO_LOCAL_DATE_TIME))
        }
    }
  }
}
