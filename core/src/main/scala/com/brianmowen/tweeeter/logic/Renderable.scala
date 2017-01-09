package com.brianmowen.tweeeter.logic

import com.brianmowen.tweeeter.TweetStatModel
import com.brianmowen.tweeeter.helpers.MapHelpers

trait Renderable[A, B] {
  def render(a: A): B
}

object Renderable {

  def pprint[A, B](a: A)(implicit ev: Renderable[A, B]) = ev.render(a)

  val startTimeMillis = System.currentTimeMillis()

  implicit object TweeeterStatsRenderableMap extends Renderable[TweetStatModel, Map[String, String]] {

    object Keys {
      val TweetsPerHour = "tph"
      val TweetsPerMinute = "tpm"
      val TweetsPerSecond = "tps"
      val TotalTweets = "tt"
      val PhotoUrlCount = "puc"
      val UrlCount = "uc"
      val TopEmoji = "te"
      val PercentEmoji = "pe"
      val TopHashtags = "th"
      val TopDomains = "td"
    }

    private val hourMillis = 3600000D
    private val minuteMillis = 60000D
    private val secondMillis = 1000D

    override def render(a: TweetStatModel): Map[String, String] = {

      val elapsed: Double = System.currentTimeMillis() - startTimeMillis
      val hour: Double = a.totalTweets / (elapsed / hourMillis)
      val minute: Double = a.totalTweets / (elapsed / minuteMillis)
      val second: Double = a.totalTweets / (elapsed / secondMillis)

      val percentEmoji = (a.tweetsWithEmoji.toDouble / a.totalTweets.toDouble) * 100

      val sortedEmojiMap = MapHelpers.top(a.emojis, 10).map { case ((k, v)) => k.getHtmlDecimal }.mkString("|")
      val sortedHashtags = MapHelpers.top(a.hashtags, 10).map { case ((s, _)) => s }.mkString("|")
      val sortedDomains = MapHelpers.top(a.domains, 10).map { case ((s, _)) => s }.mkString("|")

      Map(
        Keys.TweetsPerHour -> f"$hour%6.0f",
        Keys.TweetsPerMinute -> f"$minute%6.0f",
        Keys.TweetsPerSecond -> f"$second%6.0f",
        Keys.TotalTweets -> a.totalTweets.toString,
        Keys.PhotoUrlCount -> a.tweetsWithPhotos.toString,
        Keys.UrlCount -> a.tweetsWithUrls.toString,
        Keys.TopEmoji -> sortedEmojiMap,
        Keys.PercentEmoji -> (f"$percentEmoji%2.0f" + "%"),
        Keys.TopHashtags -> sortedHashtags,
        Keys.TopDomains -> sortedDomains
      )

    }
  }

  implicit object TweeeterStatsRenderableNewline extends Renderable[TweetStatModel, String] {
    def fmt(key: String, value: String): String = key + ": " + value + "\n"

    override def render(a: TweetStatModel): String = {
      val m = pprint[TweetStatModel, Map[String, String]](a)
      val builder = new StringBuilder()
      m.foreach { case (k, v) => builder.append(fmt(k, v)) }
      builder.result()
    }
  }

}
