package org.typelevel.fix

import scalafix.v1._
import scala.meta._

class v0_5 extends SemanticRule("v0_5") {
  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case Importer(ref , _) if ref.toString == "io.github.davidgregory084" =>
        Patch.replaceTree(ref, "org.typelevel.sbt")
      case Importer(ref, _) if ref.toString == "_root_.io.github.davidgregory084" =>
        Patch.replaceTree(ref, "_root_.org.typelevel.sbt")
    }.asPatch
}
