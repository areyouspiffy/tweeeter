package com.brianmowen.tweeeter.logic

import akka.NotUsed
import akka.stream.scaladsl.Flow
import cats.kernel.Semigroup
import com.brianmowen.tweeeter.TweeeterProtocol.Tweet
import com.brianmowen.tweeeter.TweetStatModel
import com.brianmowen.tweeeter.helpers.WorkerPool

object TweetAccumulator {

  import TweetStat._
  import Semigroups._

  // Parallelize CPU bound work
  private val parallelism = Runtime.getRuntime.availableProcessors()

  def apply(): Flow[Tweet, TweetStatModel, NotUsed] =
    WorkerPool(Flow[Tweet].map { tweet =>
      TweetStat[TweetStatModel].calc(tweet)
    }, parallelism).scan(TweetStat[TweetStatModel].empty) { case (m, n) => Semigroup[TweetStatModel].combine(m, n) }

}
