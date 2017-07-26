/*
 * Copyright 2017 HM Revenue & Customs
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

package repository

import de.flapdoodle.embed.mongo.{Command, MongodStarter}
import de.flapdoodle.embed.mongo.config._
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.extract.UserTempNaming
import de.flapdoodle.embed.process.runtime.Network
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Suite}
import reactivemongo.api.MongoConnection

trait EmbeddedMongo extends BeforeAndAfterAll with BeforeAndAfter {
  self: Suite =>

  private val mongod = {
    val command = Command.MongoD

    val runtimeConfig = new RuntimeConfigBuilder()
      .defaults(command)
      .artifactStore(new ExtractedArtifactStoreBuilder()
        .defaults(command)
        .download(new DownloadConfigBuilder()
          .defaultsForCommand(command).build())
        .executableNaming(new UserTempNaming()))
      .build()

    val starter = MongodStarter.getInstance(runtimeConfig)
    val port = 12345
    val config = new MongodConfigBuilder()
      .version(Version.Main.V3_2)
      .net(new Net(port, Network.localhostIsIPv6()))
      .build()

    starter.prepare(config)
  }

  override protected def beforeAll(): Unit = {
    mongod.start()
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    mongod.stop()
    super.afterAll()
  }

  private val driver = new reactivemongo.api.MongoDriver
  protected val connection: MongoConnection = driver.connection(List("localhost:12345"))

}
