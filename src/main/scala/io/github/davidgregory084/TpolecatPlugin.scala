/*
 * Copyright 2019 David Gregory
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

import sbt._
import sbt.Keys._
import scala.util.Try

object TpolecatPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  case class Version(major: Long, minor: Long, patch: Long)
  object Version {
    val V2_11_0 = Version(2, 11, 0)
    val V2_12_0 = Version(2, 12, 0)
    val V2_13_0 = Version(2, 13, 0)
    val V3_0_0  = Version(3, 0, 0)

    implicit val versionOrdering: Ordering[Version] =
      Ordering.by(version => (version.major, version.minor, version.patch))
  }

  case class ScalacOption(
    name: String,
    addedIn: Option[Version] = None,
    removedIn: Option[Version] = None
  )

  import Version._

  val allScalacOptions = List(
    ScalacOption("-deprecation", removedIn = Some(V2_13_0)),                                              // Emit warning and location for usages of deprecated APIs. Not really removed but deprecated in 2.13.
    ScalacOption("-deprecation", addedIn = Some(V3_0_0)),                                                 // Emit warning and location for usages of deprecated APIs.
    ScalacOption("-explaintypes", removedIn = Some(V3_0_0)),                                              // Explain type errors in more detail.
    ScalacOption("-explain-types", addedIn = Some(V3_0_0)),                                               // Explain type errors in more detail.
    ScalacOption("-explain", addedIn = Some(V3_0_0)),                                                     // Explain errors in more detail.
    ScalacOption("-feature"),                                                                             // Emit warning and location for usages of features that should be imported explicitly.
    ScalacOption("-language:existentials", removedIn = Some(V3_0_0)),                                     // Existential types (besides wildcard types) can be written and inferred
    ScalacOption("-language:experimental.macros", removedIn = Some(V3_0_0)),                              // Allow macro definition (besides implementation and application)
    ScalacOption("-language:higherKinds", removedIn = Some(V3_0_0)),                                      // Allow higher-kinded types
    ScalacOption("-language:implicitConversions", removedIn = Some(V3_0_0)),                              // Allow definition of implicit functions called views
    ScalacOption("-language:existentials,experimental.macros,higherKinds,implicitConversions", addedIn = Some(V3_0_0)), // the four options above, dotty style
    ScalacOption("-unchecked"),                                                                           // Enable additional warnings where generated code depends on assumptions.
    ScalacOption("-Xcheckinit", removedIn = Some(V3_0_0)),                                                // Wrap field accessors to throw an exception on uninitialized access.
    ScalacOption("-Xfatal-warnings"),                                                                     // Fail the compilation if there are any warnings.
    ScalacOption("-Xlint", removedIn = Some(V2_11_0)),                                                    // Used to mean enable all linting options but now the syntax for that is different (-Xlint:_ I think)
    ScalacOption("-Xlint:adapted-args", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),               // Warn if an argument list is modified to match the receiver.
    ScalacOption("-Xlint:by-name-right-associative", addedIn = Some(V2_11_0), removedIn = Some(V2_13_0)), // By-name parameter of right associative operator.
    ScalacOption("-Xlint:constant", addedIn = Some(V2_12_0), removedIn = Some(V3_0_0)),                   // Evaluation of a constant arithmetic expression results in an error.
    ScalacOption("-Xlint:delayedinit-select", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),         // Selecting member of DelayedInit.
    ScalacOption("-Xlint:deprecation", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                // Emit warning and location for usages of deprecated APIs.
    ScalacOption("-Xlint:doc-detached", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),               // A Scaladoc comment appears to be detached from its element.
    ScalacOption("-Xlint:inaccessible", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),               // Warn about inaccessible types in method signatures.
    ScalacOption("-Xlint:infer-any", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),                  // Warn when a type argument is inferred to be `Any`.
    ScalacOption("-Xlint:missing-interpolator", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),       // A string literal appears to be missing an interpolator id.
    ScalacOption("-Xlint:nullary-override", addedIn = Some(V2_11_0), removedIn = Some(V2_13_0)),          // Warn when non-nullary `def f()' overrides nullary `def f'.
    ScalacOption("-Xlint:nullary-unit", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),               // Warn when nullary methods return Unit.
    ScalacOption("-Xlint:option-implicit", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),            // Option.apply used implicit view.
    ScalacOption("-Xlint:package-object-classes", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),     // Class or object defined in package object.
    ScalacOption("-Xlint:poly-implicit-overload", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),     // Parameterized overloaded implicit methods are not visible as view bounds.
    ScalacOption("-Xlint:private-shadow", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),             // A private field (or class parameter) shadows a superclass field.
    ScalacOption("-Xlint:stars-align", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),                // Pattern sequence wildcard must align with sequence component.
    ScalacOption("-Xlint:type-parameter-shadow", addedIn = Some(V2_11_0), removedIn = Some(V3_0_0)),      // A local type parameter shadows a type already in scope.
    ScalacOption("-Xlint:unsound-match", addedIn = Some(V2_11_0), removedIn = Some(V2_13_0)),             // Pattern match may not be typesafe.
    ScalacOption("-Wunused:nowarn", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                   // Ensure that a `@nowarn` annotation actually suppresses a warning.
    ScalacOption("-Yno-adapted-args", removedIn = Some(V2_13_0)),                                         // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    ScalacOption("-Ywarn-dead-code", removedIn = Some(V2_13_0)),                                          // Warn when dead code is identified.
    ScalacOption("-Wdead-code", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                       // ^ Replaces the above
    ScalacOption("-Ywarn-extra-implicit", addedIn = Some(V2_12_0), removedIn = Some(V2_13_0)),            // Warn when more than one implicit parameter section is defined.
    ScalacOption("-Wextra-implicit", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                  // ^ Replaces the above
    ScalacOption("-Ywarn-inaccessible", removedIn = Some(V2_11_0)),                                       // Warn about inaccessible types in method signatures. Alias for -Xlint:inaccessible so can be removed as of 2.11.
    ScalacOption("-Ywarn-nullary-override", removedIn = Some(V2_13_0)),                                   // Warn when non-nullary `def f()' overrides nullary `def f'.
    ScalacOption("-Ywarn-nullary-unit", removedIn = Some(V2_13_0)),                                       // Warn when nullary methods return Unit.
    ScalacOption("-Ywarn-numeric-widen", removedIn = Some(V2_13_0)),                                      // Warn when numerics are widened.
    ScalacOption("-Wnumeric-widen", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                   // ^ Replaces the above
    // Disabled due to false positives; see https://github.com/scala/bug/issues/11813
    // ScalacOption("-Wself-implicit", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                // Warn when an implicit resolves to an enclosing self-definition
    ScalacOption("-Ywarn-unused", addedIn = Some(V2_11_0), removedIn = Some(V2_12_0)),                    // Warn when local and private vals, vars, defs, and types are unused.
    ScalacOption("-Ywarn-unused-import", addedIn = Some(V2_11_0), removedIn = Some(V2_12_0)),             // Warn if an import selector is not referenced.
    ScalacOption("-Ywarn-unused:implicits", addedIn = Some(V2_12_0), removedIn = Some(V2_13_0)),          // Warn if an implicit parameter is unused.
    ScalacOption("-Wunused:implicits", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                // ^ Replaces the above
    ScalacOption("-Wunused:explicits", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                // Warn if an explicit parameter is unused.
    ScalacOption("-Ywarn-unused:imports", addedIn = Some(V2_12_0), removedIn = Some(V2_13_0)),            // Warn if an import selector is not referenced.
    ScalacOption("-Wunused:imports", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                  // ^ Replaces the above
    ScalacOption("-Ywarn-unused:locals", addedIn = Some(V2_12_0), removedIn = Some(V2_13_0)),             // Warn if a local definition is unused.
    ScalacOption("-Wunused:locals", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                   // ^ Replaces the above
    ScalacOption("-Ywarn-unused:params", addedIn = Some(V2_12_0), removedIn = Some(V2_13_0)),             // Warn if a value parameter is unused.
    ScalacOption("-Wunused:params", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                   // ^ Replaces the above
    ScalacOption("-Ywarn-unused:patvars", addedIn = Some(V2_12_0), removedIn = Some(V2_13_0)),            // Warn if a variable bound in a pattern is unused.
    ScalacOption("-Wunused:patvars", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                  // ^ Replaces the above
    ScalacOption("-Ywarn-unused:privates", addedIn = Some(V2_12_0), removedIn = Some(V2_13_0)),           // Warn if a private member is unused.
    ScalacOption("-Wunused:privates", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                 // ^ Replaces the above
    ScalacOption("-Ywarn-value-discard", removedIn = Some(V2_13_0)),                                      // Warn when non-Unit expression results are unused.
    ScalacOption("-Wvalue-discard", addedIn = Some(V2_13_0), removedIn = Some(V3_0_0)),                   // ^ Replaces the above
    ScalacOption("-Ykind-projector", addedIn = Some(V3_0_0))                                              // Enables a subset of kind-projector syntax (see https://github.com/lampepfl/dotty/pull/7775)
  )

  object autoImport {
    def validFor(currentVersion: Version)(opt: ScalacOption)(implicit ord: Ordering[Version]) = {
      val addedPriorTo = opt.addedIn
        .map(addedVersion => ord.gteq(currentVersion, addedVersion))
        .getOrElse(true)
      val notYetRemoved = opt.removedIn
        .map(removedVersion => ord.lt(currentVersion, removedVersion))
        .getOrElse(true)

      addedPriorTo && notYetRemoved
    }

    def scalacOptionsFor(version: String): Seq[String] =
      List(
        "-encoding", "utf8" // Specify character encoding used by source files.
      ) ++ ((CrossVersion.partialVersion(version), version.split('.')) match {
        case (Some((0, min)), _) => // dotty prereleases use 0 as major version
          allScalacOptions
            .filter(validFor(V3_0_0)) // treat dotty prereleases as 3.0.0
            .map(_.name)
        case (Some((maj, min)), Array(maj2, min2, patch)) if maj == maj2 && min == min2 =>
          allScalacOptions
            .filter(validFor(Version(maj, min, Try(patch.toLong).getOrElse(0))))
            .map(_.name)
        case (Some((maj, min)), _) =>
          allScalacOptions
            .filter(validFor(Version(maj, min, 0)))
            .map(_.name)
        case (None, _) =>
          Nil
      })

    val filterConsoleScalacOptions = { options: Seq[String] =>
      options.filterNot(Set(
        "-Werror",
        "-Wdead-code",
        "-Wunused:imports",
        "-Ywarn-unused",
        "-Ywarn-unused:imports",
        "-Ywarn-unused-import",
        "-Ywarn-dead-code",
        "-Xfatal-warnings"
      ))
    }
  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    scalacOptions ++= scalacOptionsFor(scalaVersion.value),
    scalacOptions.in(Compile, console) ~= filterConsoleScalacOptions,
    scalacOptions.in(Test, console) ~= filterConsoleScalacOptions
  )
}
