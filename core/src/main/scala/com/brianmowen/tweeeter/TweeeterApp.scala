package com.brianmowen.tweeeter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.brianmowen.tweeeter.client.ClientImpl
import com.brianmowen.tweeeter.helpers.OauthSignerImpl
import com.brianmowen.tweeeter.logic.TweetAccumulator

import scala.concurrent.Promise
import scala.util.{Failure, Success}

object TweeeterApp extends App with HttpSupport {

  implicit val system       = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val settings = TweeeterSettings()
  val signer   = new OauthSignerImpl(settings.consumerKey, settings.consumerSecret, settings.token, settings.tokenSecret)

  val client = new ClientImpl(settings, signer)

  val source = client.getPublicTweets.map(_.via(TweetAccumulator()))

  val termination = Promise[ServerBinding]()
  termination.future.flatMap(_.unbind()).flatMap(_ => system.terminate())

  source.onComplete {
    case Success(f) => {
      new Server(f, termination)
    }
    case Failure(ex) =>
      println(ex)
      system.terminate()
  }

}
