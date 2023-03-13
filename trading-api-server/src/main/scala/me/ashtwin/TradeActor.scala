package me.ashtwin

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }
import akka.cluster.sharding.typed.scaladsl.EntityTypeKey
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior, Recovery, RetentionCriteria }
import me.ashtwin.model.Order.LimitOrder
import me.ashtwin.model.{ LimitOrderBook, Order, OrderSide, OrderType }

/** @author
 *    Chenyu Liu
 *  @since 3/8/23
 *    Wednesday
 */

object TradeActor {

  val TypeKey: EntityTypeKey[Command] =
    EntityTypeKey[Command]("TradeActor")

  sealed trait Command                                extends AkkaSerializable
  case class AddOrder(order: LimitOrder)              extends Command
  case class CancelOrder(orderId: String)             extends Command
  case class CheckOrderBook(replyTo: ActorRef[State]) extends Command
  case class State(orderBook: LimitOrderBook)         extends AkkaSerializable
  sealed trait Event                                  extends AkkaSerializable
  case class OrderAccepted(order: LimitOrder)         extends Event
  case class OrderRejected()                          extends Event
  case class OrderFilled()                            extends Event
  case class OrderReplaced()                          extends Event
  case class OrderReplaceFailed()                     extends Event
  case class OrderCancelled()                         extends Event
  case class OrderCancelFailed()                      extends Event

  def apply(name: String): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      val eventSourcingBehavior = EventSourcedBehavior
        .apply(
          persistenceId = PersistenceId.ofUniqueId(name),
          State(LimitOrderBook.empty),
          commandHandler,
          eventHandler
        )
        .withRetention(RetentionCriteria.snapshotEvery(500, 2))
        .withRecovery(Recovery.default)
      context.log.info(s"TradeActor-$name spawned")
      eventSourcingBehavior
    }

  def commandHandler: (State, Command) => Effect[Event, State] = { (state, command) =>
    command match {
      case CheckOrderBook(replyTo) =>
        Effect.reply(replyTo)(state)
      case AddOrder(order) =>
        Effect.persist(OrderAccepted(order)).thenNoReply()
      case _ =>
        Effect.noReply
    }
  }

  def eventHandler: (State, Event) => State = { (state, event) =>
    event match {
      case OrderAccepted(order) =>
        (order.orderType, order.side) match {
          case (OrderType.Limit, OrderSide.Buy) =>
            state.orderBook.asksBook.addOne(order)
            state
          case _ =>
            state
        }
      case _ => state
//      case OrderRejected() => ???
//      case OrderFilled() => ???
//      case OrderReplaced() => ???
//      case OrderReplaceFailed() => ???
//      case OrderCancelled() => ???
//      case OrderCancelFailed() => ???
    }
  }

}
