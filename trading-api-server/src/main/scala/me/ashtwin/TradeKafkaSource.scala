package me.ashtwin

import akka.actor.typed.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.{ConsumerSettings, ProducerMessage, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Transactional}
import akka.stream.RestartSettings
import akka.stream.scaladsl.{RestartSource, Sink, Source}
import com.fasterxml.jackson.databind.ObjectMapper
import me.ashtwin.config.ServerConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.DurationInt

/** @author
 *    Chenyu Liu
 *  @since 3/13/23
 *    Monday
 */

object TradeKafkaSource {
  private val innerControl = new AtomicReference[Control](Consumer.NoopControl)
  // try to impl at-most-once strategy
  def consumer(pair: String)(implicit system: ActorSystem[_])= {
    val config        = system.settings.config
    val subscriptions = Subscriptions.topics(Set(pair))
    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    Consumer.plainSource(consumerSettings, subscriptions).mapMaterializedValue(c => innerControl.set(c))
  }

  final val objectMapper = new ObjectMapper()
}
