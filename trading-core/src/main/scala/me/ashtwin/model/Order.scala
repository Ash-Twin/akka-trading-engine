package me.ashtwin.model

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
