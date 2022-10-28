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

//noinspection TypeAnnotation,ScalaUnusedSymbol
private[davidgregory084] trait ScalacOptions {
  import ScalaVersion._

  /** Provide another option that is not declared by the ScalacOptions DSL.
    */
  def other(option: String, isSupported: ScalaVersion => Boolean = _ => true): ScalacOption =
    ScalacOption(option, isSupported)

  /** Provide another option that is not declared by the ScalacOptions DSL.
    */
  def other(
    option: String,
    args: List[String],
    isSupported: ScalaVersion => Boolean
  ): ScalacOption =
    ScalacOption(option, args, isSupported)

  /** Specify character encoding used by source files.
    */
  def encoding(enc: String) =
    ScalacOption("-encoding", List(enc), _ => true)

  /** Emit warning and location for usages of deprecated APIs.
    */
  val deprecation = ScalacOption(
    "-deprecation",
    version => version < V2_13_0 || version >= V3_0_0
  )

  /** Emit warning and location for usages of features that should be imported explicitly.
    */
  val feature =
    ScalacOption("-feature", _ => true)

  /** Compile for a specific version of the Java platform. Supported targets: 8, 9, ..., 17, 18.
    *
    * The release flag is supported only on JDK 9 and above, since it relies on the functionality
    * provided in [[http://openjdk.java.net/jeps/247 JEP-247: Compile for Older Platform Versions]].
    */
  def release(version: String) =
    ScalacOption(
      "-release",
      List(version),
      version => JavaMajorVersion.javaMajorVersion >= 9 && version >= V2_12_5
    )

  /** Enable features that will be available in a future version of Scala, for purposes of early
    * migration and alpha testing.
    */
  def scala3Source(version: String, isSupported: ScalaVersion => Boolean = _ >= V3_0_0) =
    ScalacOption("-source", List(version), isSupported)

  /** Enable features that will be available in Scala 3.0.x with Scala 2.x compatibility mode, for
    * purposes of early migration and alpha testing.
    *
    * Same as the enabled default `-source:3.0` but with additional helpers to migrate from 2.13.
    *
    * In addition:
    *   - flags some Scala 2 constructs that are disallowed in Scala 3 as migration warnings instead
    *     of hard errors
    *   - changes some rules to be more lenient and backwards compatible with Scala 2.13
    *   - gives some additional warnings where the semantics has changed between Scala 2.13 and 3.0
    *   - in conjunction with -rewrite, offer code rewrites from Scala 2.13 to 3.0
    */
  val source3Migration = scala3Source("3.0-migration", version => version >= V3_0_0)

  /** Enable features that will be available in future versions of Scala 3.x, for purposes of early
    * migration and alpha testing.
    */
  val sourceFuture = scala3Source("future", version => version >= V3_0_0)

  /** Enable features that will be available in future versions of Scala 3.x with Scala 2.x
    * compatibility mode, for purposes of early migration and alpha testing.
    *
    * Same as `-source:future` but with additional helpers to migrate from 3.0.
    *
    * Similarly to the helpers available under 3.0-migration, these include migration warnings and
    * optional rewrites.
    */
  val sourceFutureMigration = scala3Source("future-migration", version => version >= V3_0_0)

  /** Enable features that will be available in Scala 3.1.x, for purposes of early migration and
    * alpha testing.
    */
  val source31 = scala3Source("3.1", version => version >= V3_1_0)

  /** Enable or disable language features
    */
  def languageFeatureOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(
      s"-language:$name",
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
    ScalacOption("-unchecked", _ => true)

  /** Advanced options (-X)
    */
  def advancedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true): ScalacOption =
    ScalacOption(s"-X$name", isSupported)

  /** Advanced options (-X)
    */
  def advancedOption(
    name: String,
    arguments: List[String],
    isSupported: ScalaVersion => Boolean
  ): ScalacOption = ScalacOption(s"-X$name", arguments, isSupported)

  /** Wrap field accessors to throw an exception on uninitialized access.
    */
  val checkInit =
    advancedOption("checkinit", version => version < V3_0_0)

  /** Fail the compilation if there are any warnings.
    */
  val fatalWarnings =
    advancedOption("fatal-warnings")

  /** Enable recommended warnings.
    */
  val lint =
    advancedOption("lint", version => version < V2_11_0)

  /** Enable SIP-22 async/await constructs
    */
  val async =
    advancedOption("async", version => version.isBetween(V2_13_3, V3_0_0))

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

  /** Treat compiler input as Scala source for the specified version.
    */
  def source(version: String, isSupported: ScalaVersion => Boolean = _ >= V3_0_0) =
    advancedOption(s"source:$version", isSupported)

  /** Treat compiler input as Scala source for version 2.10.
    */
  val source210 = source("2.10", version => version < V2_13_2)

  /** Treat compiler input as Scala source for version 2.11.
    */
  val source211 = source("2.11", version => version < V2_13_2)

  /** Treat compiler input as Scala source for version 2.12.
    */
  val source212 = source("2.12", version => version < V2_13_2)

  /** Treat compiler input as Scala source for version 2.13.
    */
  val source213 = source("2.13", version => version.isBetween(V2_12_2, V3_0_0))

  /** Treat compiler input as Scala source for version 3.x:
    *
    *   - Most deprecated syntax generates an error.
    *   - Infix operators can start a line in the middle of a multiline expression.
    *   - Implicit search and overload resolution follow Scala 3 handling of contravariance when
    *     checking specificity.
    */
  val source3 = source("3", version => version.isBetween(V2_12_2, V3_0_0))

  /** Advanced options (-X)
    */
  val advancedOptions: Set[ScalacOption] = ListSet(
    lint
  ) ++ lintOptions

  /** Private options (-Y)
    */
  def privateOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(s"-Y$name", isSupported)

  /** Private options (-Y)
    */
  def privateOption(
    name: String,
    arguments: List[String],
    isSupported: ScalaVersion => Boolean
  ) = ScalacOption(s"-Y$name", arguments, isSupported)

  /** Produce an error if an argument list is modified to match the receiver.
    */
  val privateNoAdaptedArgs =
    privateOption("no-adapted-args", version => version < V2_13_0)

  /** Enables support for a subset of [[https://github.com/typelevel/kind-projector kind-projector]]
    * syntax.
    */
  val privateKindProjector =
    privateOption("kind-projector", version => version >= V3_0_0)

  /** Enables support for higher order unification in type constructor inference.
    *
    * Initially provided as a compiler option in the 2.12.x series to fix the infamous
    * [[https://github.com/scala/bug/issues/2712 SI-2712]].
    *
    * Enabled by default in 2.13.0+ and no longer accepted by the compiler as an option.
    */
  val privatePartialUnification =
    privateOption("partial-unification", version => version.isBetween(V2_11_11, V2_13_0))

  /** Configures the number of worker threads for the compiler backend.
    *
    * As of 2.12.5 the compiler can serialize bytecode, perform method-local optimisations and write
    * class files in parallel.
    *
    * @param threads
    *   the number of worker threads. Default: 8, or the value returned by
    *   [[java.lang.Runtime#availableProcessors]] if fewer than 8.
    */
  def privateBackendParallelism(
    threads: Int = math.min(Runtime.getRuntime.availableProcessors, 8)
  ) = privateOption(
    "backend-parallelism",
    List(threads.toString),
    version => version.isBetween(V2_12_5, V3_0_0)
  )

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
    privateKindProjector,
    privatePartialUnification
  ) ++ privateWarnOptions

  /** Warning options (-W)
    */
  def warnOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(s"-W$name", isSupported)

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

  /** Warn when and expression is ignored because it is followed by another expression.
    */
  val warnNonUnitStatement =
    warnOption("nonunit-statement", version => version.isBetween(V2_13_9, V3_0_0))

  /** Fail the compilation if there are any warnings.
    */
  val warnError =
    warnOption("error", version => version.isBetween(V2_13_0, V3_0_0))

  /** Unused warning options (-Wunused:)
    */
  def warnUnusedOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(s"-Wunused:$name", isSupported)

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
    warnValueDiscard,
    warnNonUnitStatement
  ) ++ warnUnusedOptions

  /** Options which fail the compilation if there are any warnings.
    */
  val fatalWarningOptions: Set[ScalacOption] = ListSet(
    fatalWarnings,
    warnError
  )

  /** Verbose options (-V)
    */
  def verboseOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(s"-V$name", isSupported)

  /** Print dependent missing implicits.
    */
  val verboseImplicits =
    verboseOption("implicits", _.isBetween(V2_13_6, V3_0_0))

  /** Print found/required error messages as colored diffs.
    */
  val verboseTypeDiffs =
    verboseOption("type-diffs", _.isBetween(V2_13_6, V3_0_0))

  /** Explain type errors in more detail.
    */
  val explaintypes2 =
    // alternatively --explain-types which is still different from Scala 3 :(
    ScalacOption("-explaintypes", _ < V3_0_0)

  /** Explain type errors in more detail.
    */
  val explaintypes3 = ScalacOption("-explain-types", _ >= V3_0_0)

  /** Verbose options (-V)
    */
  val verboseOptions: Set[ScalacOption] = ListSet(
    verboseImplicits,
    verboseTypeDiffs,
    explaintypes2,
    explaintypes3
  )

  /** The default set of Scala compiler options defined by sbt-tpolecat.
    */
  val default: Set[ScalacOption] = ListSet(
    encoding("utf8"),
    deprecation,
    feature,
    unchecked
  ) ++ languageFeatureOptions ++ advancedOptions ++ privateOptions ++ warnOptions ++ verboseOptions

  /** Optimizer options (-opt)
    */
  def optimizerOption(name: String, isSupported: ScalaVersion => Boolean = _ => true) =
    ScalacOption(s"-opt$name", isSupported)

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

  /** Default options to exclude in console tasks
    */
  val defaultConsoleExclude: Set[ScalacOption] = privateWarnUnusedOptions ++
    warnUnusedOptions ++
    fatalWarningOptions +
    privateWarnDeadCode +
    warnDeadCode
}
