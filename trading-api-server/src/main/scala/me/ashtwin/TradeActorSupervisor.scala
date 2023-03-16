package me.ashtwin

import akka.actor.typed.eventstream.EventStream
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, EntityRef }
import akka.stream.Supervision
import akka.util.Timeout
import io.circe.jawn
import io.circe.syntax._
import io.circe.generic.auto._
import me.ashtwin.config.ServerConfig
import me.ashtwin.model.Order.LimitOrder
import me.ashtwin.model.{ OrderSide, OrderType }
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import java.time.{ LocalDateTime, ZoneId }
import java.util.UUID
import scala.concurrent.ExecutionContextExecutor
import scala.util.{ Failure, Random, Success }

/** @author
 *    Chenyu Liu
 *  @since 3/9/23
 *    Thursday
 */

object TradeActorSupervisor {
  sealed trait Command
  case object Activate extends Command
  case class GetAllTradingMarket(
    tradeActorRefs: ActorRef[Map[String, EntityRef[TradeActor.Command]]]
  ) extends Command
  case object StartStream extends Command

  def apply(currentTradeActorRefs: Map[String, EntityRef[TradeActor.Command]] = Map.empty)(implicit
    sharding: ClusterSharding
  ): Behavior[Command] =
    Behaviors.receive[Command] { case (ctx, msg) =>
      import ctx.system
      msg match {
        case Activate =>
          ConfigSource.default.load[ServerConfig] match {
            case Left(err) =>
              ctx.log.error(err.prettyPrint())
              ctx.system.terminate()
              Behaviors.stopped
            case Right(serverConfig) =>
              implicit val timeout: Timeout             = Timeout.apply(serverConfig.system.timeout)
              implicit val ec: ExecutionContextExecutor = ctx.executionContext
              val tradeActorRefs = serverConfig.pairs.map { pair =>
                val entityRef = sharding.entityRefFor(TradeActor.TypeKey, pair.pairName)
                pair.pairName -> entityRef
              }.toMap
              tradeActorRefs.values.foreach(
                _ ! TradeActor.AddOrder(
                  LimitOrder(
                    UUID.randomUUID().toString,
                    OrderType.Limit,
                    OrderSide.Buy,
                    Random.nextInt(10),
                    Random.nextInt(8)
                  ),
                  LocalDateTime.now(ZoneId.of("UTC"))
                )
              )
              tradeActorRefs.values.foreach(
                _.ask(TradeActor.CheckOrderBook).onComplete {
                  case Failure(exception) => throw exception
                  case Success(orderBook) =>
                    println(orderBook.orderBook.asksBook.map(_ + "\n").toString())
                }
              )
              apply(tradeActorRefs)
          }
        case GetAllTradingMarket(tradeActorRefs) =>
          tradeActorRefs ! currentTradeActorRefs
          Behaviors.same
        case StartStream =>
          currentTradeActorRefs.map { case (pair, entityRef) =>
            ctx.log.info(s"Start KafkaConsumer on topic $pair")
            TradeKafkaSource
              .consumer(pair)
              .map(_.value())
              .map(TradeKafkaSource.objectMapper.writeValueAsString)
              .map(jawn.decode[LimitOrder])
              .map {
                case Left(err) =>
                  throw err
                case Right(limitOrder: LimitOrder) =>
                  limitOrder
              }
              .runForeach(limitOrder =>
                entityRef ! TradeActor.AddOrder(limitOrder, LocalDateTime.now(ZoneId.of("UTC")))
              )
          }
          Behaviors.same
      }
    }

}
