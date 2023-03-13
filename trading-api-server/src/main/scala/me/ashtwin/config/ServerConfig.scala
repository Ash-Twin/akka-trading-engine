package me.ashtwin.config

import me.ashtwin.model.TradingPair

import scala.concurrent.duration.FiniteDuration

/** @author
 *    Chenyu Liu
 *  @since 3/9/23
 *    Thursday
 */

case class ServerConfig(
  pairs: List[TradingPair],
  system: SystemConfig
)
case class SystemConfig(
  timeout: FiniteDuration
)
