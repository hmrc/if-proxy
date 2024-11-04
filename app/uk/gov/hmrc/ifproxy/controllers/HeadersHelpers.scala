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

package uk.gov.hmrc.ifproxy.controllers

import play.api.mvc.{Request, RequestHeader}
import uk.gov.hmrc.http.HttpResponse

/**
  * @author Yuriy Tumakha
  */
trait HeadersHelpers {

  def extractHeaders(include: Set[String])(using request: Request[?]): Seq[(String, String)] =
    request.headers.headers.filter(h => include.exists(_.equalsIgnoreCase(h._1)))

  def headersMapToSeq(headers: Map[String, Seq[String]]): Seq[(String, String)] =
    headers.view.mapValues(_.mkString(",")).toSeq

  def toPrintableResponseHeaders(httpResponse: HttpResponse): String =
    toPrintableHeaders(headersMapToSeq(httpResponse.headers))

  def toPrintableRequestHeaders(httpRequest: RequestHeader): String =
    toPrintableHeaders(httpRequest.headers.headers)

  private def toPrintableHeaders(headers: Seq[(String, String)]): String =
    headers.map(h => s"${h._1}: ${h._2}").mkString(";\n")

}
