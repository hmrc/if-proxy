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

import org.apache.pekko.actor.ActorSystem
import play.api.Configuration
import play.api.http.Status.{CREATED, NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Yuriy Tumakha
 */
@Singleton
class MockHttpClient @Inject() (
  config: Configuration,
  override val httpAuditing: HttpAuditing,
  override val wsClient: WSClient,
  override protected val actorSystem: ActorSystem
) extends DefaultHttpClient(config, httpAuditing, wsClient, actorSystem) {

  override def GET[A](
    url: String,
    queryParams: Seq[(String, String)],
    headers: Seq[(String, String)]
  )(implicit
    rds: HttpReads[A],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[A] = {
    val status =
      if (url.contains("UNKNOWN_ID")) {
        NOT_FOUND
      } else {
        OK
      }
    mockResponse(url, status, None, headers)
  }

  override def POST[I, O](
    url: String,
    body: I,
    headers: Seq[(String, String)]
  )(implicit
    wts: Writes[I],
    rds: HttpReads[O],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[O] =
    mockResponse(url, CREATED, Some(body.asInstanceOf[JsValue]), headers)

  private def mockResponse[A](url: String, status: Int, requestBody: Option[JsValue], headers: Seq[(String, String)]): Future[A] = {
    val body = Json.obj("requestUrl" -> url) ++
      requestBody.fold(Json.obj())(b => Json.obj("requestBody" -> b))

    val httpResponse = HttpResponse(status, body, headers.map(h => (h._1, Seq(h._2))).toMap)
    Future.successful(httpResponse.asInstanceOf[A])
  }

}
