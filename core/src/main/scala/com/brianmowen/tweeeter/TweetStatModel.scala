package com.brianmowen.tweeeter

import com.vdurmont.emoji.Emoji

case class TweetStatModel(totalTweets: Int,
                          tweetsWithEmoji: Int,
                          tweetsWithUrls: Int,
                          tweetsWithPhotos: Int,
                          emojis: Map[Emoji, Int],
                          hashtags: Map[String, Int],
                          domains: Map[String, Int])

