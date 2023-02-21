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

import play.api.Logging
import play.api.mvc._
import uk.gov.hmrc.http.HttpVerbs.{GET, POST}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.ifproxy.config.AppConfig
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


/**
 * @author Yuriy Tumakha
 */
@Singleton()
class ValuationsProxyController @Inject()(
                                           appConfig: AppConfig,
                                           httpClient: DefaultHttpClient,
                                           cc: ControllerComponents
                                         )(implicit ec: ExecutionContext)
  extends BackendController(cc) with Logging with HeadersHelpers {

  private val baseUrl = s"${appConfig.ifBaseUrl}/valuations"
  private val searchEndpoint = s"$baseUrl/get-properties/"
  private val getPropertyEndpoint = s"$baseUrl/get-property/"
  private val submitChallengeEndpoint = s"$baseUrl/council-tax-band-challenge/"

  private val staticHeaders: Seq[(String, String)] = Seq(
    AUTHORIZATION -> s"Bearer ${appConfig.ifToken}",
    ACCEPT -> "application/json;charset=UTF-8",
    "Environment" -> appConfig.ifEnvironment
  )

  private val forwardHeaders =
    Set("CorrelationId", "Content-Type", "start", "size", "searchBy", "postCode", "propertyNameOrNumber", "street", "town",
      "localAuthority", "localAuthorityReferenceNumber", "propertyPurpose", "councilTaxBand", "bandStatus", "courtCode")

  private val skipResponseHeaders = Set("Content-Type", "Content-Length", "Transfer-Encoding")

  def valuationsGetPropertiesSearchTypeGet(searchType: String): Action[AnyContent] = Action.async { implicit request =>
    forwardGetRequest(searchEndpoint + searchType)
  }

  def valuationsGetPropertyIdGet(id: String): Action[AnyContent] = Action.async { implicit request =>
    forwardGetRequest(getPropertyEndpoint + id)
  }

  def valuationsCouncilTaxBandChallengePost(): Action[AnyContent] = Action.async { implicit request =>
    forwardPostRequest(submitChallengeEndpoint)
  }

  private def forwardGetRequest(url: String)(implicit request: Request[_]): Future[Result] =
    forwardRequest(GET, url)

  private def forwardPostRequest(url: String)(implicit request: Request[_]): Future[Result] =
    forwardRequest(POST, url)

  private def forwardRequest(httpVerb: String, url: String)(implicit request: Request[_]): Future[Result] = {
    logger.info(s"$httpVerb $url Request Headers:\n${toPrintableHeaders(request.headers.headers)}")

    val headers = staticHeaders ++ extractHeaders(forwardHeaders)
    val correlationId = request.headers.get("CorrelationId")

    // The default HttpReads will wrap the response in an exception and make the body inaccessible
    implicit val responseReads: HttpReads[HttpResponse] = (_, _, response: HttpResponse) => response

    val result =
      if (httpVerb == GET) {
        httpClient.GET[HttpResponse](url, Seq.empty, headers)
      } else {
        httpClient.POSTString[HttpResponse](url, request.body.toString, headers)
      }

    result.map { response =>
      val body = response.body
      val logMessage = s"BST response ${response.status} $url \nCorrelationId: $correlationId \nHEADERS: ${toPrintableHeaders(response.headers)} \nBODY: $body"

      if (response.status == OK || response.status == CREATED) {
        logger.info(logMessage)
      } else {
        logger.warn(logMessage)
      }

      val responseHeaders = headersMapToSeq(response.headers).filter(h => !skipResponseHeaders.exists(_.equalsIgnoreCase(h._1))) :+ "API_URL" -> url

      Status(response.status)(body)
        .withHeaders(responseHeaders: _*)
    }
  }

}
