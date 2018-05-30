package scalaz.meta
package plugin

import scala.tools.nsc._
import plugins._

class ScalazPlugin(val global: Global) extends Plugin { plugin =>
  val name = "scalaz"
  val description = "scalaz"

  object sufficiency extends {
    val global: plugin.global.type = plugin.global
  } with SufficiencyChecker

  val components = List(
    "suff" -> sufficiency,
  ).flatMap { case (opt, phf) =>
      if (options.contains(s"-$opt")) None
      else Some(phf)
  }
}
