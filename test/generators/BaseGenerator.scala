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

package generators

import org.scalacheck.Gen

trait BaseGenerator {
  val refLength = 10

  //noinspection ScalaStyle
  def hashGen: Gen[String] = {
    val c = Gen.oneOf(Seq("a", "b", "c", "d", "e", "f"))
    val n = Gen.chooseNum(0, 9).map(_.toString)
    Gen.listOfN(32, Gen.oneOf(c, n)).map(l => l.mkString)
  }

  def alphaNumOfLengthGen(maxLength: Int) = {
    Gen.listOfN(maxLength, Gen.alphaNumChar).map(_.mkString)
  }

  //noinspection ScalaStyle
  def numGen = Gen.chooseNum(0,1000)
}
