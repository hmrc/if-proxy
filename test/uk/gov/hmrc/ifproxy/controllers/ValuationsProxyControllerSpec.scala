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

package uk.gov.hmrc.ifproxy.controllers

import akka.stream.Materializer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient

/**
 * @author Yuriy Tumakha
 */
class ValuationsProxyControllerSpec extends AnyFlatSpec with should.Matchers with Injecting with GuiceOneAppPerSuite {

  private val fakeRequest = FakeRequest()
  private val controller = inject[ValuationsProxyController]
  implicit val m: Materializer = inject[Materializer]

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .overrides(bind[DefaultHttpClient].to[MockHttpClient])
      .build()

  "GET /valuations/get-properties/Search" should "return 200" in {
    val result = controller.valuationsGetPropertiesSearchTypeGet("Search")(fakeRequest.withHeaders("Authorization" -> "Bearer XXX"))

    status(result) shouldBe OK
    contentAsJson(result) shouldBe Json.obj("requestUrl" -> "http://localhost:8887/valuations/get-properties/Search")
  }

  "GET /valuations/get-property/7777777" should "return 200" in {
    val result = controller.valuationsGetPropertyIdGet("7777777")(fakeRequest)

    status(result) shouldBe OK
    contentAsJson(result) shouldBe Json.obj("requestUrl" -> "http://localhost:8887/valuations/get-property/7777777")
  }

  "GET /valuations/get-property/UNKNOWN_ID" should "return 404 for UNKNOWN_ID" in {
    val result = controller.valuationsGetPropertyIdGet("UNKNOWN_ID")(fakeRequest)

    status(result) shouldBe NOT_FOUND
    contentAsJson(result) shouldBe Json.obj("requestUrl" -> "http://localhost:8887/valuations/get-property/UNKNOWN_ID")
  }

  "POST /valuations/council-tax-band-challenge" should "return 201" in {
    val requestWithJsonBody = fakeRequest.withMethod("POST").withJsonBody(Json.obj("param1" -> "value1"))
    val expectedJson = Json.parse("""{"requestUrl":"http://localhost:8887/valuations/council-tax-band-challenge/","requestBody":{"param1":"value1"}}""")
    val result = controller.valuationsCouncilTaxBandChallengePost()(requestWithJsonBody)

    status(result) shouldBe CREATED
    contentAsJson(result) shouldBe expectedJson
  }

  "POST /valuations/council-tax-band-challenge" should "return 400 for empty body in request" in {
    val requestEmptyBody = fakeRequest.withMethod("POST").withBody("")
    val expectedJson = Json.parse("""{"statusCode":400,"message":"JSON body is expected in request"}""")
    val result = controller.valuationsCouncilTaxBandChallengePost()(requestEmptyBody)

    status(result) shouldBe BAD_REQUEST
    contentAsJson(result) shouldBe expectedJson
  }

}
