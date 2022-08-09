/*
 * Copyright 2022 Typelevel
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

class v0_5 extends SemanticRule("v0_5") {
  val PackageSym = SymbolMatcher.exact("io/github/davidgregory084/")

  // Remaining in sbt-tpolecat
  val TpolecatPluginSym = SymbolMatcher.exact("io/github/davidgregory084/TpolecatPlugin.")
  val OptionsModeSym    = SymbolMatcher.exact("io/github/davidgregory084/OptionsMode.")
  val VerboseModeSym    = SymbolMatcher.exact("io/github/davidgregory084/VerboseMode.")
  val DevModeSym        = SymbolMatcher.exact("io/github/davidgregory084/DevMode.")
  val CiModeSym         = SymbolMatcher.exact("io/github/davidgregory084/CiMode.")
  val ReleaseModeSym    = SymbolMatcher.exact("io/github/davidgregory084/ReleaseMode.")

  val PluginSym = TpolecatPluginSym + OptionsModeSym + VerboseModeSym + DevModeSym + CiModeSym + ReleaseModeSym

  // Moved to scalac-options
  val JavaMajorVersionSym = SymbolMatcher.exact("io/github/davidgregory084/JavaMajorVersion.")
  val ScalacOptionSym     = SymbolMatcher.exact("io/github/davidgregory084/ScalacOption.")
  val ScalaVersionSym     = SymbolMatcher.exact("io/github/davidgregory084/ScalaVersion.")

  val ScalacOptionsSym = JavaMajorVersionSym + ScalacOptionSym + ScalaVersionSym

  private def makeSelector(packages: String*): Term.Ref =
    packages.tail.foldLeft(Term.Name(packages.head): Term.Ref) { case (selector, pkg) =>
      Term.Select(selector, Term.Name(pkg))
    }

  private def makePluginImport(importee: Importee) =
    Patch.addGlobalImport(
      Importer(makeSelector("org", "typelevel", "sbt", "tpolecat"), List(importee))
    )

  private def makeScalacOptionsImport(importee: Importee) =
    Patch.addGlobalImport(
      Importer(makeSelector("org", "typelevel", "scalacoptions"), List(importee))
    )

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case importer @ Importer(ref, importees) if PackageSym.matches(ref) =>
        importees.collect {
          case importee @ Importee.Name(name) if PluginSym.matches(name) =>
            makePluginImport(importee) + Patch.removeImportee(importee)
          case rename @ Importee.Rename(name, _) if PluginSym.matches(name) =>
            makePluginImport(rename) + Patch.removeImportee(rename)
          case importee @ Importee.Name(name) if ScalacOptionsSym.matches(name) =>
            makeScalacOptionsImport(importee) + Patch.removeImportee(importee)
          case rename @ Importee.Rename(name, _) if ScalacOptionsSym.matches(name) =>
            makeScalacOptionsImport(rename) + Patch.removeImportee(rename)
          case importee @ Importee.Wildcard() =>
            makePluginImport(importee) +
              makeScalacOptionsImport(importee) +
              Patch.removeImportee(importee)
        }.asPatch
    }.asPatch
}
