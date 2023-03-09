package me.ashtwin

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.actor.typed.scaladsl.Behaviors
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

  def apply()(implicit system: ActorSystem[_]): Behavior[Activate] =
    Behaviors.setup[Activate] { ctx =>
      Behaviors.receiveMessage[Activate] { case Activate() =>
        ConfigSource.default.load[ServerConfig] match {
          case Left(err) =>
            system.log.error(err.prettyPrint())
            system.terminate()
          case Right(serverConfig) =>
            serverConfig.pairs.foreach { pair =>
              ctx.spawn(
                TradeActor.apply(pair.pairName),
                s"TradeActor-${pair.pairName.replace("/", "-")}"
              )
            }
        }
        Behaviors.same
      }
    }

}
