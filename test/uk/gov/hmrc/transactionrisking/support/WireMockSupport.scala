/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.transactionrisking.support

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.{BeforeAndAfterAll, TestSuite}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.client.HttpClientV2

trait WireMockSupport extends BeforeAndAfterAll with GuiceOneAppPerSuite {
  this: TestSuite =>

  val wireMockServer: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .disable[uk.gov.hmrc.mongo.play.PlayMongoModule]
      .build()

  lazy val httpClient: HttpClientV2 = app.injector.instanceOf[HttpClientV2]

  def wireMockPort: Int = wireMockServer.port()

  override def beforeAll(): Unit = {
    super.beforeAll()
    wireMockServer.start()
  }

  override def afterAll(): Unit = {
    wireMockServer.stop()
    super.afterAll()
  }
}