package me.ashtwin.model

import java.time.LocalDateTime

/** @author
 *    Chenyu Liu
 *  @since 3/8/23
 *    Wednesday
 */
object Order {
  case class MarketOrder(
    orderId: String,
    orderType: String,
    side: String,
    price: BigDecimal,
    amount: BigDecimal
  ) extends Order
  case class LimitOrder(
    orderId: String,
    orderType: String,
    side: String,
    price: BigDecimal,
    amount: BigDecimal
  ) extends Order
}
sealed trait Order {
  val orderId: String
  val orderType: String
  val price: BigDecimal
}
object OrderType {
  val Limit  = "Limit"
  val Market = "Market"
}
object OrderSide {
  val Buy  = "Buy"
  val Sell = "Sell"
}
