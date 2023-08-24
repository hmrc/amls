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
import play.api.libs.json.{Reads, Writes, __}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}

object JsPathSupport {
  val logger: Logger = Logger(this.getClass())

  final val readLocalDateTime: Reads[LocalDateTime] = {
    (__ \ "$date").read[Long].map(dateTime => LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneOffset.UTC))
      .orElse {
        __.read[Long].map(dateTime => LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneOffset.UTC))
      }.orElse {
      (__ \ "$date").read[String].map(dateTimeStr => LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        .orElse {
          Reads.at[String](__).map(dateTime => Instant.ofEpochMilli(dateTime.toLong).atZone(ZoneOffset.UTC).toLocalDateTime)
        }
        .orElse {
          Reads.at[String](__ \ "$date" \ "$numberLong").map(dateTime => Instant.ofEpochMilli(dateTime.toLong).atZone(ZoneOffset.UTC).toLocalDateTime)
        }
    }
  }

  final val localDateTimeWrites: Writes[LocalDateTime] =
    Writes.at[String](__ \ "$date" \ "$numberLong")
      .contramap(_.toInstant(ZoneOffset.UTC).toEpochMilli.toString)
}
