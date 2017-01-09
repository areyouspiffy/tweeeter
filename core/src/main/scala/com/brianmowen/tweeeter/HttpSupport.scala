package com.brianmowen.tweeeter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait HttpSupport {
  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer
  implicit def ec = system.dispatcher
}
