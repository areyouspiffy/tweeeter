package com.brianmowen.tweeeter.helpers

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{ Balance, Flow, GraphDSL, Merge }

object WorkerPool {

  def apply[In, Out](worker: Flow[In, Out, Any], workerCount: Int): Flow[In, Out, NotUsed] = {
    import akka.stream.scaladsl.GraphDSL.Implicits._

    Flow.fromGraph(GraphDSL.create() { implicit b =>
      val balancer = b.add(Balance[In](workerCount, waitForAllDownstreams = true))
      val merge    = b.add(Merge[Out](workerCount))

      for (_ <- 1 to workerCount) {
        balancer ~> worker.async ~> merge
      }

      FlowShape(balancer.in, merge.out)
    })
  }

}
