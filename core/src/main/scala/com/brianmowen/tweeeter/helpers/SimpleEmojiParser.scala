package com.brianmowen.tweeeter.helpers

import com.vdurmont.emoji.{Emoji, EmojiParser}

import scala.collection.JavaConverters._

object SimpleEmojiParser extends EmojiParser {
  def getEmoji(s: String): List[Emoji] = EmojiParser.getUnicodeCandidates(s).asScala.toList.map(_.getEmoji)
}
