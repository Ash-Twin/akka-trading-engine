package me.ashtwin

import akka.actor.typed.{ ActorRef, ActorSystem, SupervisorStrategy }
import akka.actor.typed.scaladsl.Behaviors
import akka.pattern.BackoffSupervisor
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior, Recovery, RetentionCriteria }
import me.ashtwin.model.{ LimitOrderBook, Order }

/** @author
 *    Chenyu Liu
 *  @since 3/8/23
 *    Wednesday
 */

object TradeActor {
  sealed trait Command
  case class AddOrder(order: Order)       extends Command
  case class CancelOrder(orderId: String) extends Command
  case class State(orderBook: LimitOrderBook)
  case class Check(replyTo: ActorRef[State]) extends Command
  sealed trait Event
  case class OrderAccepted()      extends Event
  case class OrderRejected()      extends Event
  case class OrderFilled()        extends Event
  case class OrderReplaced()      extends Event
  case class OrderReplaceFailed() extends Event
  case class OrderCancelled()     extends Event
  case class OrderCancelFailed()  extends Event

  def apply(name: String)(implicit system: ActorSystem[_]) = {
    val eventSourcingBehavior = EventSourcedBehavior
      .apply(
        persistenceId = PersistenceId.ofUniqueId(name),
        State(LimitOrderBook.empty),
        commandHandler,
        eventHandler
      )
      .withRetention(RetentionCriteria.snapshotEvery(500, 2))
      .withRecovery(Recovery.default)
    system.log.info(s"TradeActor-$name spawned")
    eventSourcingBehavior
  }

  def commandHandler: (State, Command) => Effect[Event, State] = { (state, command) =>
    command match {
      case Check(replyTo) =>
        Effect.reply(replyTo)(state)
      case _ =>
        Effect.persist(OrderAccepted()).thenNoReply()
    }
  }

  def eventHandler: (State, Event) => State = { (state, event) => state }

}
