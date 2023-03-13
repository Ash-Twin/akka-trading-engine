package me.ashtwin

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.{ConsumerSettings, ProducerMessage, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Transactional}
import akka.stream.RestartSettings
import akka.stream.scaladsl.{RestartSource, Sink}

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.DurationInt

/**
 * @author Chenyu Liu
 * @since 3/13/23 Monday
**/
    
object TradeKafkaSource {
  val innerControl = new AtomicReference[Control](Consumer.NoopControl)
  // try to impl at-most-once strategy

}
