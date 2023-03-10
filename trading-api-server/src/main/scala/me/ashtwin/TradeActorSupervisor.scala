package me.ashtwin

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
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
  case class Activate()

  def apply()(implicit sharding: ClusterSharding): Behavior[Activate] =
    Behaviors.setup[Activate] { ctx =>
      Behaviors.receiveMessage[Activate] { case Activate() =>
        ConfigSource.default.load[ServerConfig] match {
          case Left(err) =>
            ctx.log.error(err.prettyPrint())
            ctx.system.terminate()
          case Right(serverConfig) =>
            val tradeActorRefs = serverConfig.pairs.map { pair =>
              val entityRef = sharding.entityRefFor(TradeActor.TypeKey, pair.pairName)
              entityRef
            }
            tradeActorRefs.foreach(
              _ ! TradeActor.AddOrder(
                LimitOrder(Random.nextString(8), OrderType.Limit, OrderSide.Buy, 30, 1)
              )
            )

        }
        Behaviors.same
      }
    }

}
