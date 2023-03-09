package me.ashtwin

import akka.Done
import akka.actor.typed.{ ActorSystem, SupervisorStrategy }
import akka.actor.typed.scaladsl.Behaviors

/** @author
 *    Chenyu Liu
 *  @since 3/9/23
 *    Thursday
 */

object Main {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem[_] = ActorSystem.create(
      Behaviors.setup[Done] { ctx =>
        implicit val system: ActorSystem[_] = ctx.system
        ctx.log.info("Trading System Initializing...")
        val tradeSupervisor = ctx.spawn(TradeActorSupervisor.apply(), "TradeSupervisor")
        tradeSupervisor ! TradeActorSupervisor.Activate()
        ctx.log.info(ctx.system.printTree)
        Behaviors.same
      },
      "trading-api-server"
    )

  }
}
