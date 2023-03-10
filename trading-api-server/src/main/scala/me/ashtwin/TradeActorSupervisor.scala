package me.ashtwin

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import me.ashtwin.config.ServerConfig
import me.ashtwin.model.TradingPair
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

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
            serverConfig.pairs.foreach { pair =>
              val entiryRef = sharding.entityRefFor(TradeActor.TypeKey, pair.pairName)
            }

        }
        Behaviors.same
      }
    }

}
