package com.brianmowen.tweeeter.fixtures

import com.brianmowen.tweeeter.TweeeterProtocol._
import spray.json._

import scala.io.Source

object Tweets {

  private val tweetText = Source.fromURL(getClass.getResource("/tweets.txt")).mkString("").split("\n")
  private val tweets = tweetText.map(_.parseJson.asJsObject).filter(_.getFields("id_str").nonEmpty).map(_.convertTo[Tweet])

  def apply() = tweets

}
