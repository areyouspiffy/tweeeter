package com.brianmowen.tweeeter.client

import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.brianmowen.tweeeter.helpers.OauthSigner
import com.brianmowen.tweeeter.{HttpSupport, TweeeterSettings}

trait Client extends HttpSupport {

  protected val settings: TweeeterSettings
  protected val signer: OauthSigner

  protected val start: ByteString = ByteString.empty
  protected val sep: ByteString   = ByteString("\n")
  protected val end: ByteString   = ByteString.empty

  protected implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json(100000).withFramingRenderer(Flow[ByteString].intersperse(start, sep, end))

}

class ClientImpl(val settings: TweeeterSettings, val signer: OauthSigner)(implicit val system: ActorSystem,
                                                                          val materializer: ActorMaterializer)
    extends Client
    with GetPublicStreamRequest
