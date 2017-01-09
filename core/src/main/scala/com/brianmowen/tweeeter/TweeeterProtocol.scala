package com.brianmowen.tweeeter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object TweeeterProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  // Twitter API

  case class Hashtag(indices: Array[Int], text: String)
  case class TweetUrl(display_url: Option[String], expanded_url: Option[String])
  case class Media(media_url: String, `type`: String)
  case class Entities(hashtags: Option[List[Hashtag]], urls: Option[List[TweetUrl]], media: Option[List[Media]])
  case class Tweet(id_str: Option[String], entities: Entities, text: String)

  implicit val HashtagFmt = jsonFormat2(Hashtag)
  implicit val TweetUrlFmt = jsonFormat2(TweetUrl)
  implicit val MediaFmt = jsonFormat2(Media)
  implicit val EntitiesFmt = jsonFormat3(Entities)
  implicit val TweetFmt = jsonFormat3(Tweet)

}
