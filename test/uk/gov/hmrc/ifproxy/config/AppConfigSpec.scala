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

package uk.gov.hmrc.ifproxy.config

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Injecting

/**
 * @author Yuriy Tumakha
 */
class AppConfigSpec extends AnyFlatSpec with should.Matchers with Injecting with GuiceOneAppPerSuite {

  private val appConfig = inject[AppConfig]

  "AppConfig" should "provide correct appName" in {
    appConfig.appName shouldBe "if-proxy"
  }

  it should "provide Integration Framework local config" in {
    appConfig.ifBaseUrl shouldBe "http://localhost:8887"
    appConfig.ifToken shouldBe "auth_token"
    appConfig.ifEnvironment shouldBe "ist0"
  }

}
