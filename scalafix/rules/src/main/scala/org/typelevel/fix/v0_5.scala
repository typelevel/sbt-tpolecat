/*
 * Copyright 2022 David Gregory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.typelevel.fix

import scalafix.v1._
import scala.meta._

class v0_5 extends SyntacticRule("v0_5") {
  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case Importer(ref, _) if ref.toString == "io.github.davidgregory084" =>
        Patch.replaceTree(ref, "org.typelevel.sbt.tpolecat")
      case Importer(ref, _) if ref.toString == "_root_.io.github.davidgregory084" =>
        Patch.replaceTree(ref, "_root_.org.typelevel.sbt.tpolecat")
    }.asPatch
}
