package com.brianmowen.tweeeter.helpers

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.brianmowen.tweeeter.HttpSupport

import scala.concurrent.Future

trait HttpHelpers {
  self: HttpSupport =>

  // consume response entity as a string
  implicit def byteSourceToString(bytes: Source[ByteString, Any]): Future[String] =
    bytes.runFold(ByteString())(_ ++ _).map(_.utf8String)

}
