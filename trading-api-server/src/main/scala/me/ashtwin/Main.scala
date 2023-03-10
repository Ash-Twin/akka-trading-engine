package me.ashtwin

import akka.Done
import akka.actor.typed.{ActorSystem, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.persistence.typed.PersistenceId

/** @author
 *    Chenyu Liu
 *  @since 3/9/23
 *    Thursday
 */

object Main {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem[_] = ActorSystem.create(
      Behaviors.setup[Done] { ctx =>
        implicit val system: ActorSystem[_]    = ctx.system
        implicit val sharding: ClusterSharding = ClusterSharding(system)
        // registration at startup
        sharding.init(Entity(typeKey = TradeActor.TypeKey) { entityContext =>
          TradeActor(entityContext.entityId)
        })
        ctx.log.info("Trading System Initializing...")
        val tradeSupervisor = ctx.spawn(TradeActorSupervisor.apply(), "TradeSupervisor")
        tradeSupervisor ! TradeActorSupervisor.Activate()
        ctx.log.info("\n" + ctx.system.printTree)

        Behaviors.same
      },
      "trading-api-server"
    )
    AkkaManagement(system).start()
    // Starting the bootstrap process needs to be done explicitly
    ClusterBootstrap(system).start()
  }
}
