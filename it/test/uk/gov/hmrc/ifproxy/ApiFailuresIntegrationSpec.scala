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

package uk.gov.hmrc.ifproxy

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSClient, writeableOf_String}
import play.api.test.Injecting

class ApiFailuresIntegrationSpec
  extends AnyFlatSpec
  with should.Matchers
  with ScalaFutures
  with IntegrationPatience
  with Injecting
  with GuiceOneServerPerSuite {

  private val wsClient           = inject[WSClient]
  private val baseUrl            = s"http://localhost:$port"
  private val submitChallengeUrl = s"$baseUrl/valuations/council-tax-band-challenge"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .build()

  "POST /valuations/council-tax-band-challenge" should "return 400 for empty body in request" in {
    val response =
      wsClient
        .url(submitChallengeUrl)
        .addHttpHeaders("Content-Type" -> "application/json")
        .post("")
        .futureValue

    response.status        shouldBe BAD_REQUEST
    response.body.toString shouldBe """{"statusCode":400,"message":"JSON body is expected in request"}"""
  }

  it should "return 400 for invalid JSON in request body" in {
    val response =
      wsClient
        .url(submitChallengeUrl)
        .addHttpHeaders("Content-Type" -> "application/json")
        .post("{invalid_json}")
        .futureValue

    response.status shouldBe BAD_REQUEST
  }

  it should "return 404 for unknown endpoint path" in {
    val response =
      wsClient
        .url(baseUrl + "/valuations/unknown_path")
        .addHttpHeaders("Content-Type" -> "application/json")
        .post("{}")
        .futureValue

    response.status shouldBe NOT_FOUND
  }

}
