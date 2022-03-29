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

package io.github.davidgregory084

import scala.Ordering.Implicits._
import scala.collection.immutable.ListSet

trait ScalacOptions {
  import ScalaVersion._

  def encoding(enc: String) = ScalacOption(List("-encoding", enc))

  val deprecation = ScalacOption(
    List("-deprecation"),
    version => version < V2_13_0 || version >= V3_0_0
  )

  val feature = ScalacOption(List("-feature"))

  val languageExistentials = ScalacOption(
    List("-language:existentials"),
    version => version < V3_0_0
  )

  val languageExperimentalMacros = ScalacOption(
    List("-language:experimental.macros")
  )

  val languageHigherKinds = ScalacOption(
    List("-language:higherKinds")
  )

  val languageImplicitConversions = ScalacOption(
    List("-language:implicitConversions")
  )

  val languageFeatures: Set[ScalacOption] = ListSet(
    languageExistentials,
    languageExperimentalMacros,
    languageHigherKinds,
    languageImplicitConversions
  )

  val unchecked = ScalacOption(List("-unchecked"))

  def advancedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true): ScalacOption =
    ScalacOption(List(s"-X$name"), isSupported)

  val checkInit =
    advancedOption("checkinit", version => version < V3_0_0)

  val fatalWarnings =
    advancedOption("fatal-warnings", version => version < V2_13_0 || version >= V3_0_0)

  val lint =
    advancedOption("lint", version => version < V2_11_0)

  def lintOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    advancedOption(s"lint:$name", isSupported)

  def disableLintOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    advancedOption(s"lint:-$name", isSupported)

  val lintAdaptedArgs =
    lintOption("adapted-args", version => version.isBetween(V2_11_0, V3_0_0))

  val lintByNameRightAssociative =
    lintOption("by-name-right-associative", version => version.isBetween(V2_11_0, V2_13_0))

  val lintConstant =
    lintOption("constant", version => version.isBetween(V2_12_0, V3_0_0))

  val lintDelayedInitSelect =
    lintOption("delayedinit-select", version => version.isBetween(V2_11_0, V3_0_0))

  val lintDeprecation =
    lintOption("deprecation", version => version.isBetween(V2_13_0, V3_0_0))

  val lintDocDetached =
    lintOption("doc-detached", version => version.isBetween(V2_11_0, V3_0_0))

  val lintImplicitRecursion =
    lintOption("implicit-recursion", version => version.isBetween(V2_13_3, V3_0_0))

  val lintImplicitNotFound =
    lintOption("implicit-not-found", version => version.isBetween(V2_13_0, V3_0_0))

  val lintInaccessible =
    lintOption("inaccessible", version => version.isBetween(V2_11_0, V3_0_0))

  val lintInferAny =
    lintOption("infer-any", version => version.isBetween(V2_11_0, V3_0_0))

  val lintMissingInterpolator =
    lintOption("missing-interpolator", version => version.isBetween(V2_11_0, V3_0_0))

  val lintNullaryOverride =
    lintOption("nullary-override", version => version.isBetween(V2_11_0, V2_13_0))

  val lintNullaryUnit =
    lintOption("nullary-unit", version => version.isBetween(V2_11_0, V3_0_0))

  val lintOptionImplicit =
    lintOption("option-implicit", version => version.isBetween(V2_11_0, V3_0_0))

  val lintPackageObjectClasses =
    lintOption("package-object-classes", version => version.isBetween(V2_11_0, V3_0_0))

  val lintPolyImplicitOverload =
    lintOption("poly-implicit-overload", version => version.isBetween(V2_11_0, V3_0_0))

  val lintPrivateShadow =
    lintOption("private-shadow", version => version.isBetween(V2_11_0, V3_0_0))

  val lintStarsAlign =
    lintOption("stars-align", version => version.isBetween(V2_11_0, V3_0_0))

  val lintStrictUnsealedPatmat =
    lintOption("strict-unsealed-patmat", version => version.isBetween(V2_13_4, V3_0_0))

  val lintTypeParameterShadow =
    lintOption("type-parameter-shadow", version => version.isBetween(V2_11_0, V3_0_0))

  val lintUnsoundMatch =
    lintOption("unsound-match", version => version.isBetween(V2_11_0, V2_13_0))

  val disableLintBynameImplicit =
    disableLintOption("byname-implicit", version => version.isBetween(V2_13_3, V3_0_0))

  val lintOptions: Set[ScalacOption] = ListSet(
    lintAdaptedArgs,
    lintByNameRightAssociative,
    lintConstant,
    lintDelayedInitSelect,
    lintDeprecation,
    lintDocDetached,
    lintImplicitRecursion,
    lintImplicitNotFound,
    lintInaccessible,
    lintInferAny,
    lintMissingInterpolator,
    lintNullaryOverride,
    lintNullaryUnit,
    lintOptionImplicit,
    lintPackageObjectClasses,
    lintPolyImplicitOverload,
    lintPrivateShadow,
    lintStarsAlign,
    lintStrictUnsealedPatmat,
    lintTypeParameterShadow,
    lintUnsoundMatch,
    disableLintBynameImplicit
  )

  val advancedOptions: Set[ScalacOption] = ListSet(
    checkInit,
    lint
  ) ++ lintOptions

  def privateOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(List(s"-Y$name"), isSupported)

  val privateNoAdaptedArgs =
    privateOption("no-adapted-args", version => version < V2_13_0)

  val privateKindProjector =
    privateOption("kind-projector", version => version >= V3_0_0)

  def privateWarnOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    privateOption(s"warn-$name", isSupported)

  val privateWarnDeadCode =
    privateWarnOption("dead-code", version => version < V2_13_0)

  val privateWarnExtraImplicit =
    privateWarnOption("extra-implicit", version => version.isBetween(V2_12_0, V2_13_0))

  val privateWarnInaccessible =
    privateWarnOption("inaccessible", version => version < V2_11_0)

  val privateWarnNullaryOverride =
    privateWarnOption("nullary-override", version => version < V2_13_0)

  val privateWarnNullaryUnit =
    privateWarnOption("nullary-unit", version => version < V2_13_0)

  val privateWarnNumericWiden =
    privateWarnOption("numeric-widen", version => version < V2_13_0)

  val privateWarnUnused =
    privateWarnOption("unused", version => version.isBetween(V2_11_0, V2_12_0))

  val privateWarnUnusedImport =
    privateWarnOption("unused-import", version => version.isBetween(V2_11_0, V2_12_0))

  val privateWarnValueDiscard =
    privateWarnOption("value-discard", version => version < V2_13_0)

  def privateWarnUnusedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    privateWarnOption(s"unused:$name", isSupported)

  val privateWarnUnusedImplicits =
    privateWarnUnusedOption("implicits", version => version.isBetween(V2_12_0, V2_13_0))

  val privateWarnUnusedImports =
    privateWarnUnusedOption("imports", version => version.isBetween(V2_12_0, V2_13_0))

  val privateWarnUnusedLocals =
    privateWarnUnusedOption("locals", version => version.isBetween(V2_12_0, V2_13_0))

  val privateWarnUnusedParams =
    privateWarnUnusedOption("params", version => version.isBetween(V2_12_0, V2_13_0))

  val privateWarnUnusedPatVars =
    privateWarnUnusedOption("patvars", version => version.isBetween(V2_12_0, V2_13_0))

  val privateWarnUnusedPrivates =
    privateWarnUnusedOption("privates", version => version.isBetween(V2_12_0, V2_13_0))

  val privateWarnUnusedOptions: Set[ScalacOption] = ListSet(
    privateWarnUnusedImplicits,
    privateWarnUnusedImports,
    privateWarnUnusedLocals,
    privateWarnUnusedParams,
    privateWarnUnusedPatVars,
    privateWarnUnusedPrivates
  )

  val privateWarnOptions: Set[ScalacOption] = ListSet(
    privateWarnDeadCode,
    privateWarnExtraImplicit,
    privateWarnInaccessible,
    privateWarnNullaryOverride,
    privateWarnNullaryUnit,
    privateWarnNumericWiden,
    privateWarnUnused,
    privateWarnUnusedImport,
    privateWarnValueDiscard
  ) ++ privateWarnUnusedOptions

  val privateOptions: Set[ScalacOption] = ListSet(
    privateNoAdaptedArgs,
    privateKindProjector
  ) ++ privateWarnOptions

  def warnOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(List(s"-W$name"), isSupported)

  val warnDeadCode =
    warnOption("dead-code", version => version.isBetween(V2_13_0, V3_0_0))

  val warnExtraImplicit =
    warnOption("extra-implicit", version => version.isBetween(V2_13_0, V3_0_0))

  val warnNumericWiden =
    warnOption("numeric-widen", version => version.isBetween(V2_13_0, V3_0_0))

  val warnValueDiscard =
    warnOption("value-discard", version => version.isBetween(V2_13_0, V3_0_0))

  val warnError =
    warnOption("error", version => version.isBetween(V2_13_0, V3_0_0))

  def warnUnusedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(List(s"-Wunused:$name"), isSupported)

  val warnUnusedNoWarn =
    warnUnusedOption("nowarn", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedImplicits =
    warnUnusedOption("implicits", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedExplicits =
    warnUnusedOption("explicits", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedImports =
    warnUnusedOption("imports", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedLocals =
    warnUnusedOption("locals", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedParams =
    warnUnusedOption("params", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedPatVars =
    warnUnusedOption("patvars", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedPrivates =
    warnUnusedOption("privates", version => version.isBetween(V2_13_0, V3_0_0))

  val warnUnusedOptions: Set[ScalacOption] = ListSet(
    warnUnusedNoWarn,
    warnUnusedImplicits,
    warnUnusedExplicits,
    warnUnusedImports,
    warnUnusedLocals,
    warnUnusedParams,
    warnUnusedPatVars,
    warnUnusedPrivates
  )

  val warnOptions: Set[ScalacOption] = ListSet(
    warnDeadCode,
    warnExtraImplicit,
    warnNumericWiden,
    warnValueDiscard
  ) ++ warnUnusedOptions

  val fatalWarningOptions: Set[ScalacOption] = ListSet(
    fatalWarnings,
    warnError
  )

  val default: Set[ScalacOption] = ListSet(
    encoding("utf8"),
    deprecation,
    feature,
    unchecked
  ) ++ languageFeatures ++ advancedOptions ++ privateOptions ++ warnOptions

  def optimizerOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(List(s"-opt$name"), isSupported)

  val optimizerMethodLocal =
    optimizerOption(":l:method", version => version.isBetween(V2_12_0, V3_0_0))

  val optimizerInline =
    optimizerOption(":l:inline", version => version.isBetween(V2_12_0, V3_0_0))

  val optimizerWarnings =
    optimizerOption("-warnings", version => version.isBetween(V2_12_0, V3_0_0))

  def optimizerInlineFrom(inlineFromPackages: String*) =
    optimizerOption(
      s"-inline-from:${inlineFromPackages.mkString(":")}",
      version => version.isBetween(V2_12_0, V3_0_0)
    )

  def optimizerOptions(inlineFromPackages: String*): Set[ScalacOption] = ListSet(
    optimizerMethodLocal,
    optimizerInline,
    optimizerInlineFrom(inlineFromPackages: _*)
  )
}
