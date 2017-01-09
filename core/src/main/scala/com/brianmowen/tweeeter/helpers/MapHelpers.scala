package com.brianmowen.tweeeter.helpers

import scala.collection.mutable.ListBuffer

object MapHelpers {
  def count[T, Key](n: Seq[T], key: T => Key): Map[Key, Int] = n.foldLeft(Map.empty[Key, Int]) { (m, h) =>
    val k = key(h)
    m.updated(k, m.getOrElse(k, 0) + 1)
  }

  def top[T](m: Map[T, Int], limit: Int): Seq[(T, Int)] =
    m.toSeq.sortWith { case ((_,a), (_,b)) => b < a }.take(limit)

}
