package com.brianmowen.tweeeter

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{ BroadcastHub, Keep, Source }
import akka.stream.{ ActorAttributes, ActorMaterializer, Supervision }
import com.brianmowen.tweeeter.logic.Renderable
import de.heikoseeberger.akkasse.EventStreamMarshalling._
import de.heikoseeberger.akkasse.ServerSentEvent

import scala.concurrent.{ Future, Promise }
import scala.io.StdIn
import scala.concurrent.duration._

class Server(tweetStats: Source[TweetStatModel, NotUsed],
             onComplete: Promise[ServerBinding])(implicit val system: ActorSystem, val materializer: ActorMaterializer)
    extends HttpSupport {

  import Renderable.TweeeterStatsRenderableNewline

  val errorHandlingStrategy: Supervision.Decider = { _ =>
    Supervision.Restart
  }

  // Transform values into SSE. Using a BroadcastHub allows the source to be materialized multiple times.
  private val sse: Source[ServerSentEvent, NotUsed] =
    tweetStats
      .groupedWithin(100, 5.seconds)
      .map(_.head)
      .map { s =>
        val r = Renderable.pprint[TweetStatModel, String](s)
        ServerSentEvent(r)
      }
      .toMat(BroadcastHub.sink(64))(Keep.right)
      .run()
      .withAttributes(ActorAttributes.supervisionStrategy(errorHandlingStrategy))

  val routes = get {
    // default route serves html
    pathSingleSlash {
      getFromDirectory("static/index.html")
    } ~
    // used by the client as an EventSource
    (get & path("sse")) {
      complete(sse.detach)
    }
  }

  val binding: Future[ServerBinding] = Http().bindAndHandle(routes, "127.0.0.1", 9000)

  binding.map { x =>
    println(s"Server started on port 9000\nPress any key to stop")
    StdIn.readLine()
    onComplete.trySuccess(x)
  }

}
