/*
 * Copyright 2019 HM Revenue & Customs
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

import models.des.DesConstants
import org.mockito.Matchers.{eq => eqTo}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}


class ResponsiblePeopleUpdateHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with OneAppPerSuite {

  val testResponsiblePeopleUpdateHelper = new ResponsiblePeopleUpdateHelper {}

  "ResponsiblePeopleUpdateHelper" must {

    "correctly register the responsible person as 'unchanged' if the data is the same" in {

      val viewModel = DesConstants.SubscriptionViewStatusRP.copy(
        responsiblePersons = DesConstants.SubscriptionViewStatusRP.responsiblePersons map {
          rps => Seq(rps.head)
        }
      )

      val request = DesConstants.updateAmendVariationRequestRP.copy(
        responsiblePersons = DesConstants.updateAmendVariationRequestRP.responsiblePersons map {
          rps => Seq(rps.tail.head)
        }
      )

      val convertedRequest = DesConstants.amendStatusAmendVariationRP.copy(
        responsiblePersons = DesConstants.amendStatusAmendVariationRP.responsiblePersons map {
          rps =>
            Seq(rps.tail.head.copy(extra = rps.tail.head.extra.copy(status = Some(StatusConstants.Unchanged))))
        })

      val result = testResponsiblePeopleUpdateHelper.updateWithResponsiblePeople(request, viewModel)

      result.responsiblePersons must be(convertedRequest.responsiblePersons)
    }

    "register the person as 'unchanged' if specified in RP Extra, even if the data is different" in {

      val viewModel = DesConstants.SubscriptionViewStatusRP.copy(
        responsiblePersons = DesConstants.SubscriptionViewStatusRP.responsiblePersons map {
          rps => Seq(rps.head.copy(startDate = Some("1970-01-01")))
        }
      )

      val request = DesConstants.updateAmendVariationRequestRP.copy(
        responsiblePersons = DesConstants.updateAmendVariationRequestRP.responsiblePersons map {
          rps => Seq(rps.tail.head) map {p => p.copy(extra = p.extra.copy(status = Some(StatusConstants.Unchanged)))}
        }
      )

      val convertedRequest = DesConstants.amendStatusAmendVariationRP.copy(
        responsiblePersons = DesConstants.amendStatusAmendVariationRP.responsiblePersons map {
          rps =>
            Seq(rps.tail.head.copy(dateChangeFlag = Some(true),
              extra = rps.tail.head.extra.copy(status = Some(StatusConstants.Unchanged))))
        })

      val result = testResponsiblePeopleUpdateHelper.updateWithResponsiblePeople(request, viewModel)

      result.responsiblePersons must be(convertedRequest.responsiblePersons)

    }

    "assume that the responsible person has changed if specified in RPExtra" in {

      val viewModel = DesConstants.SubscriptionViewStatusRP.copy(
        responsiblePersons = DesConstants.SubscriptionViewStatusRP.responsiblePersons map {
          rps => Seq(rps.head)
        }
      )

      val request = DesConstants.updateAmendVariationRequestRP.copy(
        responsiblePersons = DesConstants.updateAmendVariationRequestRP.responsiblePersons map {
          rps =>
            Seq(rps.tail.head) map {
              p => p.copy(extra = p.extra.copy(status = Some(StatusConstants.Updated)))
            }
        }
      )

      val convertedRequest = DesConstants.amendStatusAmendVariationRP.copy(
        responsiblePersons = DesConstants.amendStatusAmendVariationRP.responsiblePersons map {
          rps =>
            Seq(rps.tail.head.copy(extra = rps.tail.head.extra.copy(status = Some("Updated"))))
        })

      val result = testResponsiblePeopleUpdateHelper.updateWithResponsiblePeople(request, viewModel)

      result.responsiblePersons must be(convertedRequest.responsiblePersons)
    }

    "successfully compare and update api6 request with api5 data" when {

      "status is None in RPExtra" when {

        "responsible people start date changes" in {

          val viewModel = DesConstants.SubscriptionViewStatusRP.copy(
            responsiblePersons = DesConstants.SubscriptionViewStatusRP.responsiblePersons map {
              rps => Seq(rps.head.copy(startDate = Some("1970-01-01")))
            }
          )

          val request = DesConstants.updateAmendVariationRequestRP.copy(
            responsiblePersons = DesConstants.updateAmendVariationRequestRP.responsiblePersons map {
              rps => Seq(rps.tail.head) map {p => p.copy(extra = p.extra.copy(status = None))}
            }
          )

          val convertedRequest = DesConstants.amendStatusAmendVariationRP.copy(
            responsiblePersons = DesConstants.amendStatusAmendVariationRP.responsiblePersons map {
              rps =>
                Seq(rps.tail.head.copy(dateChangeFlag = Some(true),
                  extra = rps.tail.head.extra.copy(status = Some("Updated"))))
            })

          val result = testResponsiblePeopleUpdateHelper.updateWithResponsiblePeople(request, viewModel)

          result.responsiblePersons must be(convertedRequest.responsiblePersons)

        }

        "user has modified the previous name date of change of an RP" in {
          val viewModel = DesConstants.SubscriptionViewStatusRP.copy(
            responsiblePersons = DesConstants.SubscriptionViewStatusRP.responsiblePersons map {
              rps =>
                Seq(rps.head.copy(nameDetails = rps.head.nameDetails map {
                  nd =>
                    nd.copy(previousNameDetails = nd.previousNameDetails map {
                      pnd => pnd.copy(dateOfChange = Some("1970-01-01"))
                    })
                }))
            }
          )
          val request = DesConstants.updateAmendVariationRequestRP.copy(
            responsiblePersons = DesConstants.updateAmendVariationRequestRP.responsiblePersons map {
              rps => Seq(rps.tail.head) map {p => p.copy(extra = p.extra.copy(status = None))}
            }
          )
          val convertedRequest = DesConstants.amendStatusAmendVariationRP.copy(
            responsiblePersons = DesConstants.amendStatusAmendVariationRP.responsiblePersons map {
              rps =>
                Seq(rps.tail.head.copy(
                  nameDetails = rps.tail.head.nameDetails map {
                    nd =>
                      nd.copy(previousNameDetails = nd.previousNameDetails map {
                        pnd => pnd.copy(dateChangeFlag = Some(true))
                      })
                  }
                  , extra = rps.tail.head.extra.copy(status = Some("Updated")), dateChangeFlag = Some(false)))
            }
          )

          val result = testResponsiblePeopleUpdateHelper.updateWithResponsiblePeople(request, viewModel)

          result.responsiblePersons must be(convertedRequest.responsiblePersons)
        }

        "user has deleted a record, added one new record and has not changed one record of responsible people" in {
          val viewModel = DesConstants.SubscriptionViewStatusRP

          val testRequest = DesConstants.amendStatusAmendVariationRP

          val result = testResponsiblePeopleUpdateHelper.updateWithResponsiblePeople(DesConstants.updateAmendVariationRequestRP, viewModel)

          result.responsiblePersons must be(testRequest.responsiblePersons)
        }
      }
    }
  }
}

