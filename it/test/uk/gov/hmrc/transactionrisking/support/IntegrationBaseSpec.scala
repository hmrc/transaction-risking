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

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.mongo.play.PlayMongoModule

trait IntegrationBaseSpec
  extends AnyWordSpecLike
    with Matchers
    with WireMockHelper
    with GuiceOneServerPerSuite
    with BeforeAndAfterAll:

  lazy val client: WSClient = app.injector.instanceOf[WSClient]
  val mockHost: String = WireMockHelper.host
  val mockPort: Int = WireMockHelper.wireMockPort
  val basePrefix = "/transaction-risking"

  def servicesConfig: Map[String, Any] = Map(
    "microservice.services.cip-risk.host" -> mockHost,
    "microservice.services.cip-risk.port" -> mockPort
  )

  override implicit lazy val app: Application =
    GuiceApplicationBuilder()
      .in(Environment.simple(mode = Mode.Dev))
      .configure(servicesConfig)
      .disable[PlayMongoModule]
      .build()

  override def beforeAll(): Unit =
    super.beforeAll()
    startWireMock()

  override def afterAll(): Unit =
    stopWireMock()
    super.afterAll()

  def buildRequest(path: String): WSRequest =
    client
      .url(s"http://localhost:$port$basePrefix$path")
      .withFollowRedirects(false)

  def document(response: WSResponse): JsValue =
    Json.parse(response.body)