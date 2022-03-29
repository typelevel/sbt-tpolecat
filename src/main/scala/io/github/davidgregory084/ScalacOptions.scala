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

  /** Specify character encoding used by source files.
    */
  def encoding(enc: String) =
    new ScalacOption(List("-encoding", enc))

  /** Emit warning and location for usages of deprecated APIs.
    */
  val deprecation = new ScalacOption(
    List("-deprecation"),
    version => version < V2_13_0 || version >= V3_0_0
  )

  /** Emit warning and location for usages of features that should be imported explicitly.
    */
  val feature =
    new ScalacOption(List("-feature"))

  /** Enable or disable language features
    */
  def languageFeatureOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    new ScalacOption(
      List(s"-language:$name"),
      isSupported
    )

  /** Existential types (besides wildcard types) can be written and inferred.
    */
  val languageExistentials =
    languageFeatureOption("existentials", version => version < V3_0_0)

  /** Allow macro definition (besides implementation and application).
    */
  val languageExperimentalMacros =
    languageFeatureOption("experimental.macros")

  /** Allow higher-kinded types.
    */
  val languageHigherKinds =
    languageFeatureOption("higherKinds")

  /** Allow definition of implicit functions called views.
    */
  val languageImplicitConversions =
    languageFeatureOption("implicitConversions")

  /** Preferred language feature options.
    */
  val languageFeatureOptions: Set[ScalacOption] = ListSet(
    languageExistentials,
    languageExperimentalMacros,
    languageHigherKinds,
    languageImplicitConversions
  )

  /** Enable additional warnings where generated code depends on assumptions.
    */
  val unchecked =
    new ScalacOption(List("-unchecked"))

  /** Advanced options (-X)
    */
  def advancedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true): ScalacOption =
    new ScalacOption(List(s"-X$name"), isSupported)

  /** Wrap field accessors to throw an exception on uninitialized access.
    */
  val checkInit =
    advancedOption("checkinit", version => version < V3_0_0)

  /** Fail the compilation if there are any warnings.
    */
  val fatalWarnings =
    advancedOption("fatal-warnings", version => version < V2_13_0 || version >= V3_0_0)

  /** Enable recommended warnings.
    */
  val lint =
    advancedOption("lint", version => version < V2_11_0)

  /** Enable recommended warnings.
    */
  def lintOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    advancedOption(s"lint:$name", isSupported)

  /** Disable recommended warnings.
    */
  def disableLintOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    advancedOption(s"lint:-$name", isSupported)

  /** Warn if an argument list is modified to match the receiver.
    */
  val lintAdaptedArgs =
    lintOption("adapted-args", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn if a right-associative operator taking a by-name parameter is used.
    *
    * The bug which necessitated this lint was fixed in Scala 2.13.
    *
    * See: [[https://github.com/scala/scala/pull/5969 scala/scala#5969]],
    * [[https://docs.scala-lang.org/sips/right-associative-by-name-operators.html SIP-34]]
    */
  val lintByNameRightAssociative =
    lintOption("by-name-right-associative", version => version.isBetween(V2_11_0, V2_13_0))

  /** Warn if evaluation of a constant arithmetic expression results in an error.
    */
  val lintConstant =
    lintOption("constant", version => version.isBetween(V2_12_0, V3_0_0))

  /** Warn when selecting a member of DelayedInit.
    */
  val lintDelayedInitSelect =
    lintOption("delayedinit-select", version => version.isBetween(V2_11_0, V3_0_0))

  /** Enable linted deprecations.
    */
  val lintDeprecation =
    lintOption("deprecation", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn when a Scaladoc comment appears to be detached from its element.
    */
  val lintDocDetached =
    lintOption("doc-detached", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when an implicit resolves to an enclosing self-definition.
    */
  val lintImplicitRecursion =
    lintOption("implicit-recursion", version => version.isBetween(V2_13_3, V3_0_0))

  /** Warn when an @implicitNotFound or @implicitAmbigous annotation references an invalid type
    * parameter.
    */
  val lintImplicitNotFound =
    lintOption("implicit-not-found", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn about inaccessible types in method signatures.
    */
  val lintInaccessible =
    lintOption("inaccessible", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when a type argument is inferred to be Any.
    */
  val lintInferAny =
    lintOption("infer-any", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when a string literal appears to be missing an interpolator id.
    */
  val lintMissingInterpolator =
    lintOption("missing-interpolator", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when non-nullary def f() overrides nullary def f.
    */
  val lintNullaryOverride =
    lintOption("nullary-override", version => version.isBetween(V2_11_0, V2_13_0))

  /** Warn when nullary methods return Unit.
    */
  val lintNullaryUnit =
    lintOption("nullary-unit", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when Option.apply uses an implicit view.
    */
  val lintOptionImplicit =
    lintOption("option-implicit", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when a class or object is defined in a package object.
    */
  val lintPackageObjectClasses =
    lintOption("package-object-classes", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when a parameterized overloaded implicit methods is used in a view bound.
    */
  val lintPolyImplicitOverload =
    lintOption("poly-implicit-overload", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when a private field (or class parameter) shadows a superclass field.
    */
  val lintPrivateShadow =
    lintOption("private-shadow", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when a pattern sequence wildcard does not align with the sequence component of the data
    * being matched.
    */
  val lintStarsAlign =
    lintOption("stars-align", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when matching on an unsealed class without a catch-all patttern.
    */
  val lintStrictUnsealedPatmat =
    lintOption("strict-unsealed-patmat", version => version.isBetween(V2_13_4, V3_0_0))

  /** Warn when a local type parameter shadows a type already in scope.
    */
  val lintTypeParameterShadow =
    lintOption("type-parameter-shadow", version => version.isBetween(V2_11_0, V3_0_0))

  /** Warn when a pattern matches using `.equals` and therefore may be unsound.
    */
  val lintUnsoundMatch =
    lintOption("unsound-match", version => version.isBetween(V2_11_0, V2_13_0))

  /** Warn when a by-name implicit conversion is applied to the result of a block.
    *
    * We disable this lint because it is triggered by Shapeless' Lazy encoding.
    */
  val disableLintBynameImplicit =
    disableLintOption("byname-implicit", version => version.isBetween(V2_13_3, V3_0_0))

  /** Advanced linting options (-Xlint:)
    */
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

  /** Advanced options (-X)
    */
  val advancedOptions: Set[ScalacOption] = ListSet(
    checkInit,
    lint
  ) ++ lintOptions

  /** Private options (-Y)
    */
  def privateOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    new ScalacOption(List(s"-Y$name"), isSupported)

  /** Produce an error if an argument list is modified to match the receiver.
    */
  val privateNoAdaptedArgs =
    privateOption("no-adapted-args", version => version < V2_13_0)

  /** Enables support for a subset of [[https://github.com/typelevel/kind-projector kind-projector]]
    * syntax.
    */
  val privateKindProjector =
    privateOption("kind-projector", version => version >= V3_0_0)

  /** Private warning options (-Ywarn)
    */
  def privateWarnOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    privateOption(s"warn-$name", isSupported)

  /** Warn when dead code is identified.
    */
  val privateWarnDeadCode =
    privateWarnOption("dead-code", version => version < V2_13_0)

  /** Warn when more than one implicit parameter section is defined.
    */
  val privateWarnExtraImplicit =
    privateWarnOption("extra-implicit", version => version.isBetween(V2_12_0, V2_13_0))

  /** Warn about inaccessible types in method signatures.
    */
  val privateWarnInaccessible =
    privateWarnOption("inaccessible", version => version < V2_11_0)

  /** Warn when non-nullary def f() overrides nullary def f.
    */
  val privateWarnNullaryOverride =
    privateWarnOption("nullary-override", version => version < V2_13_0)

  /** Warn when nullary methods return Unit.
    */
  val privateWarnNullaryUnit =
    privateWarnOption("nullary-unit", version => version < V2_13_0)

  /** Warn when numerics are widened.
    */
  val privateWarnNumericWiden =
    privateWarnOption("numeric-widen", version => version < V2_13_0)

  /** Warn when local and private vals, vars, defs and types are unused.
    */
  val privateWarnUnused =
    privateWarnOption("unused", version => version.isBetween(V2_11_0, V2_12_0))

  /** Warn if an import selector is not referenced.
    */
  val privateWarnUnusedImport =
    privateWarnOption("unused-import", version => version.isBetween(V2_11_0, V2_12_0))

  /** Warn when non-Unit expression results are unused.
    */
  val privateWarnValueDiscard =
    privateWarnOption("value-discard", version => version < V2_13_0)

  /** Private unused warning options (-Ywarn-unused:)
    */
  def privateWarnUnusedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    privateWarnOption(s"unused:$name", isSupported)

  /** Warn if an implicit parameter is unused.
    */
  val privateWarnUnusedImplicits =
    privateWarnUnusedOption("implicits", version => version.isBetween(V2_12_0, V2_13_0))

  /** Warn if an import selector is not referenced.
    */
  val privateWarnUnusedImports =
    privateWarnUnusedOption("imports", version => version.isBetween(V2_12_0, V2_13_0))

  /** Warn if a local definition is unused.
    */
  val privateWarnUnusedLocals =
    privateWarnUnusedOption("locals", version => version.isBetween(V2_12_0, V2_13_0))

  /** Warn if an explicit parameter is unused.
    */
  val privateWarnUnusedParams =
    privateWarnUnusedOption("params", version => version.isBetween(V2_12_0, V2_13_0))

  /** Warn if a variable bound in a pattern is unused.
    */
  val privateWarnUnusedPatVars =
    privateWarnUnusedOption("patvars", version => version.isBetween(V2_12_0, V2_13_0))

  /** Warn if a private member is unused.
    */
  val privateWarnUnusedPrivates =
    privateWarnUnusedOption("privates", version => version.isBetween(V2_12_0, V2_13_0))

  /** Private unused warning options (-Ywarn-unused:)
    */
  val privateWarnUnusedOptions: Set[ScalacOption] = ListSet(
    privateWarnUnusedImplicits,
    privateWarnUnusedImports,
    privateWarnUnusedLocals,
    privateWarnUnusedParams,
    privateWarnUnusedPatVars,
    privateWarnUnusedPrivates
  )

  /** Private warning options (-Ywarn)
    */
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

  /** Private options (-Y)
    */
  val privateOptions: Set[ScalacOption] = ListSet(
    privateNoAdaptedArgs,
    privateKindProjector
  ) ++ privateWarnOptions

  /** Warning options (-W)
    */
  def warnOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    new ScalacOption(List(s"-W$name"), isSupported)

  /** Warn when dead code is identified.
    */
  val warnDeadCode =
    warnOption("dead-code", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn when more than one implicit parameter section is defined.
    */
  val warnExtraImplicit =
    warnOption("extra-implicit", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn when numerics are widened.
    */
  val warnNumericWiden =
    warnOption("numeric-widen", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn when non-Unit expression results are unused.
    */
  val warnValueDiscard =
    warnOption("value-discard", version => version.isBetween(V2_13_0, V3_0_0))

  /** Fail the compilation if there are any warnings.
    */
  val warnError =
    warnOption("error", version => version.isBetween(V2_13_0, V3_0_0))

  /** Unused warning options (-Wunused:)
    */
  def warnUnusedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    new ScalacOption(List(s"-Wunused:$name"), isSupported)

  /** Warn if a @nowarn annotation did not suppress at least one warning.
    */
  val warnUnusedNoWarn =
    warnUnusedOption("nowarn", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn if an implicit parameter is unused.
    */
  val warnUnusedImplicits =
    warnUnusedOption("implicits", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn if an explicit parameter is unused.
    */
  val warnUnusedExplicits =
    warnUnusedOption("explicits", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn if an import selector is not referenced.
    */
  val warnUnusedImports =
    warnUnusedOption("imports", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn if a local definition is unused.
    */
  val warnUnusedLocals =
    warnUnusedOption("locals", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn if either explicit or implicit parameters are unused.
    *
    * Equivalent to -Wunused:explicits,implicits.
    */
  val warnUnusedParams =
    warnUnusedOption("params", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn if a variable bound in a pattern is unused.
    */
  val warnUnusedPatVars =
    warnUnusedOption("patvars", version => version.isBetween(V2_13_0, V3_0_0))

  /** Warn if a private member is unused.
    */
  val warnUnusedPrivates =
    warnUnusedOption("privates", version => version.isBetween(V2_13_0, V3_0_0))

  /** Unused warning options (-Wunused:)
    */
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

  /** Warning options (-W)
    */
  val warnOptions: Set[ScalacOption] = ListSet(
    warnDeadCode,
    warnExtraImplicit,
    warnNumericWiden,
    warnValueDiscard
  ) ++ warnUnusedOptions

  /** Options which fail the compilation if there are any warnings.
    */
  val fatalWarningOptions: Set[ScalacOption] = ListSet(
    fatalWarnings,
    warnError
  )

  /** The default set of Scala compiler options defined by sbt-tpolecat.
    */
  val default: Set[ScalacOption] = ListSet(
    encoding("utf8"),
    deprecation,
    feature,
    unchecked
  ) ++ languageFeatureOptions ++ advancedOptions ++ privateOptions ++ warnOptions

  /** Optimizer options (-opt)
    */
  def optimizerOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    new ScalacOption(List(s"-opt$name"), isSupported)

  /** Enable intra-method optimizations:
    * unreachable-code,simplify-jumps,compact-locals,copy-propagation,redundant-casts,box-unbox,nullness-tracking,closure-invocations,allow-skip-core-module-init,assume-modules-non-null,allow-skip-class-loading.
    */
  val optimizerMethodLocal =
    optimizerOption(":l:method", version => version.isBetween(V2_12_0, V3_0_0))

  /** Enable cross-method optimizations.
    *
    * Note: inlining requires -opt-inline-from and -opt:l:method to be provided.
    */
  val optimizerInline =
    optimizerOption(":l:inline", version => version.isBetween(V2_12_0, V3_0_0))

  /** Enable optimizer warnings
    */
  val optimizerWarnings =
    optimizerOption("-warnings", version => version.isBetween(V2_12_0, V3_0_0))

  /** Patterns for classfile names from which to allow inlining.
    *
    * @param inlineFromPackages
    *   Patterns declaring packages and classfiles to inline from.
    * @return
    *   [[ScalacOption]] declaring the packages and classfiles to be used for inlining.
    */
  def optimizerInlineFrom(inlineFromPackages: String*) =
    optimizerOption(
      s"-inline-from:${inlineFromPackages.mkString(":")}",
      version => version.isBetween(V2_12_0, V3_0_0)
    )

  /** Enable cross-method optimizations.
    *
    * @param inlineFromPackages
    *   Patterns declaring packages and classfiles to inline from.
    * @return
    *   [[ScalacOption]]s used to enable cross-method optimizations.
    */
  def optimizerOptions(inlineFromPackages: String*): Set[ScalacOption] = ListSet(
    optimizerMethodLocal,
    optimizerInline,
    optimizerInlineFrom(inlineFromPackages: _*)
  )
}
