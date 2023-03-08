package me.ashtwin.model
import scala.collection.mutable

/**
 * @author Chenyu Liu
 * @since 3/8/23 Wednesday
 *
 * */
case class LimitOrderBook(bidsBook: mutable.ArrayDeque[Order], asksBook: mutable.ArrayDeque[Order]) {

  /**
   * A limit order book is usually summarized by the following characteristics:
   *
   * Bid: The highest price against which a sell order can be executed
   * Ask: The lowest price against which a buy order can be executed
   * Spread: The difference between the lowest ask and the highest bid
   * Midpoint: The price halfway between the ask and the bid ((ask+bid)/2)
   *
   */
  def bids: BigDecimal = bidsBook.headOption.fold(BigDecimal(0))(_.price)
  def asks: BigDecimal = asksBook.headOption.fold(BigDecimal(0))(_.price)
  def spread: BigDecimal = (bids - asks).abs
  def midPoint: BigDecimal = (bids + asks) / 2

}
