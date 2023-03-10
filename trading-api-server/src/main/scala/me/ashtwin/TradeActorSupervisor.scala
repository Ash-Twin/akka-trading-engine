package me.ashtwin

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, EntityRef }
import me.ashtwin.config.ServerConfig
import me.ashtwin.model.Order.LimitOrder
import me.ashtwin.model.{ OrderSide, OrderType, TradingPair }
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

import scala.util.Random

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

  def apply(currentTradeActorRefs: Map[String, EntityRef[TradeActor.Command]] = Map.empty)(implicit
    sharding: ClusterSharding
  ): Behavior[Command] =
    Behaviors.receive[Command] { case (ctx, msg) =>
      msg match {
        case Activate =>
          ConfigSource.default.load[ServerConfig] match {
            case Left(err) =>
              ctx.log.error(err.prettyPrint())
              ctx.system.terminate()
              Behaviors.stopped
            case Right(serverConfig) =>
              val tradeActorRefs = serverConfig.pairs.map { pair =>
                val entityRef = sharding.entityRefFor(TradeActor.TypeKey, pair.pairName)
                pair.pairName -> entityRef
              }.toMap
              tradeActorRefs.values.foreach(
                _ ! TradeActor.AddOrder(
                  LimitOrder(
                    Random.nextString(8),
                    OrderType.Limit,
                    OrderSide.Buy,
                    BigDecimal.apply(Random.nextInt(10)),
                    1
                  )
                )
              )
              apply(tradeActorRefs)
          }
        case GetAllTradingMarket(tradeActorRefs) =>
          tradeActorRefs ! currentTradeActorRefs
          Behaviors.same

      }
    }

}
