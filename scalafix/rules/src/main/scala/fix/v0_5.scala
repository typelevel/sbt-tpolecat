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

  val PluginSym =
    TpolecatPluginSym + OptionsModeSym + VerboseModeSym + DevModeSym + CiModeSym + ReleaseModeSym

  // Moved to scalac-options
  val JavaMajorVersionSym = SymbolMatcher.exact("io/github/davidgregory084/JavaMajorVersion.")
  val ScalacOptionSym     = SymbolMatcher.exact("io/github/davidgregory084/ScalacOption.")
  val ScalaVersionSym     = SymbolMatcher.exact("io/github/davidgregory084/ScalaVersion.")

  val ScalacOptionsSym = JavaMajorVersionSym + ScalacOptionSym + ScalaVersionSym

  // autoImport contents
  val TpolecatPluginAutoImportSym =
    SymbolMatcher.exact("io/github/davidgregory084/TpolecatPlugin.autoImport.")

  // ScalacOptions contents
  val TpolecatPluginScalacOptionsSym =
    SymbolMatcher.exact("io/github/davidgregory084/TpolecatPlugin.autoImport.ScalacOptions.")

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

  private def makeTpolecatPluginObjectImport(importee: Importee) =
    Patch.addGlobalImport(
      Importer(
        makeSelector("org", "typelevel", "sbt", "tpolecat", "TpolecatPlugin"),
        List(importee)
      )
    )

  private def makeAutoImportObjectImport(importee: Importee) =
    Patch.addGlobalImport(
      Importer(
        makeSelector("org", "typelevel", "sbt", "tpolecat", "TpolecatPlugin", "autoImport"),
        List(importee)
      )
    )

  private def makeScalacOptionsObjectImport(importee: Importee) =
    Patch.addGlobalImport(
      Importer(
        makeSelector("org", "typelevel", "scalacoptions", "ScalacOptions"),
        List(importee)
      )
    )

  private def makeScalaVersionObjectImport(importee: Importee) =
    Patch.addGlobalImport(
      Importer(
        makeSelector("org", "typelevel", "scalacoptions", "ScalaVersion"),
        List(importee)
      )
    )

  def handleImports(importees: List[Importee], createNewImport: Importee => Patch): Patch =
    importees.collect {
      case importee @ Importee.Name(_) =>
        createNewImport(importee) + Patch.removeImportee(importee)
      case rename @ Importee.Rename(_, _) =>
        createNewImport(rename) + Patch.removeImportee(rename)
      case importee @ Importee.Wildcard() =>
        createNewImport(importee) + Patch.removeImportee(importee)
    }.asPatch

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      // Handle imports from `io.github.davidgregory084`
      case _ @Importer(ref, importees) if PackageSym.matches(ref) =>
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
      // Handle imports from `io.github.davidgregory084.TpolecatPlugin`
      case _ @Importer(ref, importees) if TpolecatPluginSym.matches(ref) =>
        handleImports(importees, makeTpolecatPluginObjectImport)
      // Handle imports from `io.github.davidgregory084.ScalaVersion`
      case _ @Importer(ref, importees) if ScalaVersionSym.matches(ref) =>
        handleImports(importees, makeScalaVersionObjectImport)
      // Handle imports from `io.github.davidgregory084.TpolecatPlugin.ScalacOptions`
      case _ @Importer(ref, importees) if TpolecatPluginScalacOptionsSym.matches(ref) =>
        handleImports(importees, makeScalacOptionsObjectImport)
      // Handle imports from `io.github.davidgregory084.TpolecatPlugin.autoImport`
      case _ @Importer(ref, importees) if TpolecatPluginAutoImportSym.matches(ref) =>
        importees.collect {
          // The `ScalacOptions` object moved to `scalac-options`
          case importee @ Importee.Name(name) if TpolecatPluginScalacOptionsSym.matches(name) =>
            makeScalacOptionsImport(importee) + Patch.removeImportee(importee)
          case rename @ Importee.Rename(name, _) if TpolecatPluginScalacOptionsSym.matches(name) =>
            makeScalacOptionsImport(rename) + Patch.removeImportee(rename)
          case importee @ Importee.Name(_) =>
            makeAutoImportObjectImport(importee) + Patch.removeImportee(importee)
          case rename @ Importee.Rename(_, _) =>
            makeAutoImportObjectImport(rename) + Patch.removeImportee(rename)
          case importee @ Importee.Wildcard() =>
            makeAutoImportObjectImport(importee) + Patch.removeImportee(importee)
        }.asPatch
    }.asPatch
}
