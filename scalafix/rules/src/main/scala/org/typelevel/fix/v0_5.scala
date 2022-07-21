package org.typelevel.fix

import scalafix.v1._
import scala.meta._

class v0_5 extends SyntacticRule("v0_5") {
  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case Importer(ref , _) if ref.toString == "io.github.davidgregory084" =>
        Patch.replaceTree(ref, "org.typelevel.sbt.tpolecat")
      case Importer(ref, _) if ref.toString == "_root_.io.github.davidgregory084" =>
        Patch.replaceTree(ref, "_root_.org.typelevel.sbt.tpolecat")
    }.asPatch
}
