package com.brianmowen.tweeeter

import com.typesafe.config.{Config, ConfigFactory}

object TweeeterSettings {
  def apply()               = new TweeeterSettings(ConfigFactory.load().getConfig("com.brianmowen.tweeeter"))
  def apply(config: Config) = new TweeeterSettings(config)
}

class TweeeterSettings(config: Config) {
  val consumerKey: String  = config getString "api.twitter.auth.consumer-key"
  val consumerSecret: String = config getString "api.twitter.auth.consumer-secret"
  val token: String         = config getString "api.twitter.auth.token"
  val tokenSecret: String       = config getString "api.twitter.auth.token-secret"
  val authUrl: String         = config getString "api.twitter.endpoints.auth"
  val publicStreamUrl: String = config getString "api.twitter.endpoints.public-stream"
}
