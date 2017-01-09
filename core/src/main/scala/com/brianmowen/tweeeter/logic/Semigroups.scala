package com.brianmowen.tweeeter.logic

import cats.Semigroup
import cats.implicits._
import com.brianmowen.tweeeter.TweetStatModel

object  Semigroups {
  import cats.syntax.semigroup._

  implicit val tweeeterStatsSemigroup = new Semigroup[TweetStatModel] {
    override def combine(x: TweetStatModel, y: TweetStatModel): TweetStatModel = {
      TweetStatModel(
        totalTweets = x.totalTweets |+| y.totalTweets,
        tweetsWithEmoji = x.tweetsWithEmoji |+| y.tweetsWithEmoji,
        tweetsWithUrls = x.tweetsWithUrls |+| y.tweetsWithUrls,
        tweetsWithPhotos = x.tweetsWithPhotos |+| y.tweetsWithPhotos,
        emojis = x.emojis |+| y.emojis,
        hashtags = x.hashtags |+| y.hashtags,
        domains = x.domains |+| y.domains
      )
    }
  }
}
