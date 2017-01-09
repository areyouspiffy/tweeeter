package com.brianmowen.tweeeter.client

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Source}
import com.brianmowen.tweeeter.HttpSupport
import com.brianmowen.tweeeter.TweeeterProtocol.Tweet
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait GetPublicStreamRequest {
  self: HttpSupport with Client =>

  private def twitterPublicStreamRequest(): HttpRequest = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = Uri(settings.publicStreamUrl)
    )
    signer.signRequest(request)
  }

  private def entityToJson[T](e: HttpEntity, f: JsObject => T): Source[Try[T], NotUsed] =
    e.withoutSizeLimit()
      .dataBytes
      .via(jsonStreamingSupport.framingDecoder)
      .mapMaterializedValue(_ => NotUsed)
      .buffer(10, OverflowStrategy.dropHead)
      .map(v =>
        Try {
          val json = v.utf8String.parseJson.asJsObject
          f(json)
        }
      )

  private def jsonToTweet(json: JsObject): Option[Tweet] = json.fields.get("id_str").flatMap(_ => Some(json.convertTo[Tweet]))

  private def getPublicStream: Future[HttpResponse] = Http().singleRequest(twitterPublicStreamRequest())

  def getPublicStreamJson: Future[Source[Try[JsObject], NotUsed]] = getPublicStream.map(e => entityToJson(e.entity, identity))

  def getPublicTweets: Future[Source[Tweet, NotUsed]] = getPublicStream.map(r => entityToJson(r.entity, jsonToTweet).collect {
    case Success(Some(js)) => js
  })

}
