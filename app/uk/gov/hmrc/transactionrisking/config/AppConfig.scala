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

package uk.gov.hmrc.transactionrisking.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject() (config: ServicesConfig, configuration: Configuration):

  val appName: String = config.getString("appName")

  private val cipRiskConfig            = configuration.get[Configuration]("microservice.services.cip-risk")
  val cipRiskServiceBaseUrl: String    = config.baseUrl("cip-risk") + cipRiskConfig.get[String]("submit-url")

  // RSD (TEMP:I haven't got the service details)
  private val rsdConfig = configuration.get[Configuration]("microservice.services.rsd")
  val rsdBaseUrl: String = config.baseUrl("rsd") + rsdConfig.get[String]("submit-url")
  val rsdToken: String = config.getString("microservice.services.rsd.token")
  val rsdEnv: String = config.getString("microservice.services.rsd.env")
  val rsdEnvironmentHeaders: Option[Seq[String]] = configuration.getOptional[Seq[String]]("microservice.services.rsd.environmentHeaders")

