package com.brianmowen.tweeeter.logic

import cats.kernel.Semigroup
import com.brianmowen.tweeeter.TweeeterProtocol.{Hashtag, Tweet}
import com.brianmowen.tweeeter.TweetStatModel
import com.brianmowen.tweeeter.helpers.{MapHelpers, SimpleEmojiParser}
import com.vdurmont.emoji.Emoji

trait Stat[T, C] {
  def empty: T
  def calc(context: C): T
  def merge(ts: T*)(implicit ev: Semigroup[T]): T = ev.combineAllOption(ts).getOrElse(this.empty)
}

object Stat {
  def apply[A, B](implicit ev: Stat[A, B]): Stat[A, B] = ev
}

object TweetStat {

  def apply[A](implicit ev: Stat[A, Tweet]): Stat[A, Tweet] = ev

  implicit def boolInt(b: Boolean): Int = if (b) 1 else 0

  implicit val TweeeterStatStat = new Stat[TweetStatModel, Tweet] {
    override def empty: TweetStatModel = TweetStatModel(0, 0, 0, 0, Map.empty, Map.empty, Map.empty)

    override def calc(context: Tweet): TweetStatModel = {
      // URLs
      val tweetUrls = context.entities.urls.getOrElse(List.empty).flatMap(_.display_url.map(_.takeWhile(_ != '.').mkString))
      val hasUrl    = tweetUrls.nonEmpty

      // Photos
      var hasPhotos = context.entities.media.map(_.exists(_.`type` == "photo")).getOrElse(false)

      // Domains
      val domains = MapHelpers.count[String, String](tweetUrls, identity)

      // Emoji
      val emoji    = SimpleEmojiParser.getEmoji(context.text)
      val hasEmoji = emoji.nonEmpty
      val emojiMap = if (emoji.isEmpty) Map.empty[Emoji, Int] else MapHelpers.count[Emoji, Emoji](emoji, identity)

      // Hashtags
      val hashtags   = context.entities.hashtags.getOrElse(List.empty)
      val hashTagMap = MapHelpers.count[Hashtag, String](hashtags, _.text)

      TweetStatModel(1, hasEmoji, hasUrl, hasPhotos, emojiMap, hashTagMap, domains)
    }
  }

}
