package me.ashtwin

import akka.actor.typed.scaladsl.Behaviors
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }
import me.ashtwin.model.{ LimitOrderBook, Order }

/** @author
 *    Chenyu Liu
 *  @since 3/8/23
 *    Wednesday
 */

object TradeActor {
  sealed trait Command
  case class AddOrder(order: Order)       extends Command
  case class RemoveOrder(orderId: String) extends Command
  case class State(orderBook: LimitOrderBook)
  sealed trait Event
  case class OrderAccepted()      extends Event
  case class OrderRejected()      extends Event
  case class OrderFilled()        extends Event
  case class OrderReplaced()      extends Event
  case class OrderReplaceFailed() extends Event
  case class OrderCancelled()     extends Event
  case class OrderCancelFailed()  extends Event

  def apply(name: String) =
    EventSourcedBehavior.apply(
      persistenceId = PersistenceId.ofUniqueId(name),
      State(LimitOrderBook.empty),
      commandHandler,
      eventHandler
    )

  def commandHandler: (State, Command) => Effect[Event, State] = { (state, command) =>
  }

  def eventHandler: (State, Event) => State = { (state, event) =>
  }

}
