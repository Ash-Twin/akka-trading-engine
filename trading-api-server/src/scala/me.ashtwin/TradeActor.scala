package me.ashtwin

import akka.actor.typed.scaladsl.Behaviors
import me.ashtwin.model.Order

/**
 * @author Chenyu Liu
 * @since 3/8/23 Wednesday
**/
    
object TradeActor {
  sealed trait Command
  case class AddOrder(order:Order) extends Command
  case class RemoveOrder(orderId: String)extends Command
  def apply() = {
    Behaviors.receiveMessage[Command] {
      case AddOrder(order) =>
        order.orderType
        Behaviors.same
    }
  }
}

