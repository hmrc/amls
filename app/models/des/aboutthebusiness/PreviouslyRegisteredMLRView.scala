/*
 * Copyright 2018 HM Revenue & Customs
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

package models.des.aboutthebusiness

import models.fe.aboutthebusiness.{PreviouslyRegisteredYes, PreviouslyRegisteredNo, AboutTheBusiness}
import play.api.libs.json.Json

case class PreviouslyRegisteredMLRView(amlsRegistered:Boolean,
                                       mlrRegNumber:Option[String],
                                       prevRegForMlr:Boolean,
                                       prevMlrRegNumber:Option[String])

object PreviouslyRegisteredMLRView{
  implicit val format = Json.format[PreviouslyRegisteredMLRView]

  implicit def convert(aboutTheBusiness:AboutTheBusiness):Option[PreviouslyRegisteredMLRView] ={
    aboutTheBusiness.previouslyRegistered match{
      case x:PreviouslyRegisteredYes if(x.value.length == 15)=> Some(PreviouslyRegisteredMLRView(false, None, true, Some(x.value)))
      case x:PreviouslyRegisteredYes if(x.value.length == 8) => Some(PreviouslyRegisteredMLRView(true, Some(x.value), false, None))
      case PreviouslyRegisteredNo => Some(PreviouslyRegisteredMLRView(false, None, false, None))
      case _ => None
    }
  }
}
