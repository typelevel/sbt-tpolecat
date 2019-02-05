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

object TpolecatPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  case class ScalacOption(
    name: String,
    addedIn: Option[Int] = None,
    removedIn: Option[Int] = None
  )

  val allScalacOptions = List(
    ScalacOption("-deprecation"),                                          // Emit warning and location for usages of deprecated APIs.
    ScalacOption("-explaintypes"),                                         // Explain type errors in more detail.
    ScalacOption("-feature"),                                              // Emit warning and location for usages of features that should be imported explicitly.
    ScalacOption("-language:existentials"),                                // Existential types (besides wildcard types) can be written and inferred
    ScalacOption("-language:experimental.macros"),                         // Allow macro definition (besides implementation and application)
    ScalacOption("-language:higherKinds"),                                 // Allow higher-kinded types
    ScalacOption("-language:implicitConversions"),                         // Allow definition of implicit functions called views
    ScalacOption("-unchecked"),                                            // Enable additional warnings where generated code depends on assumptions.
    ScalacOption("-Xcheckinit"),                                           // Wrap field accessors to throw an exception on uninitialized access.
    ScalacOption("-Xfatal-warnings"),                                      // Fail the compilation if there are any warnings.
    ScalacOption("-Xfuture"),                                              // Turn on future language features.
    ScalacOption("-Xlint", removedIn = Some(11)),
    ScalacOption("-Xlint:adapted-args", addedIn = Some(11)),               // Warn if an argument list is modified to match the receiver.
    ScalacOption("-Xlint:by-name-right-associative", addedIn = Some(11), removedIn = Some(13)),   // By-name parameter of right associative operator.
    ScalacOption("-Xlint:constant", addedIn = Some(12)),                   // Evaluation of a constant arithmetic expression results in an error.
    ScalacOption("-Xlint:delayedinit-select", addedIn = Some(11)),         // Selecting member of DelayedInit.
    ScalacOption("-Xlint:doc-detached", addedIn = Some(11)),               // A Scaladoc comment appears to be detached from its element.
    ScalacOption("-Xlint:inaccessible", addedIn = Some(11)),               // Warn about inaccessible types in method signatures.
    ScalacOption("-Xlint:infer-any", addedIn = Some(11)),                  // Warn when a type argument is inferred to be `Any`.
    ScalacOption("-Xlint:missing-interpolator", addedIn = Some(11)),       // A string literal appears to be missing an interpolator id.
    ScalacOption("-Xlint:nullary-override", addedIn = Some(11)),           // Warn when non-nullary `def f()' overrides nullary `def f'.
    ScalacOption("-Xlint:nullary-unit", addedIn = Some(11)),               // Warn when nullary methods return Unit.
    ScalacOption("-Xlint:option-implicit", addedIn = Some(11)),            // Option.apply used implicit view.
    ScalacOption("-Xlint:package-object-classes", addedIn = Some(11)),     // Class or object defined in package object.
    ScalacOption("-Xlint:poly-implicit-overload", addedIn = Some(11)),     // Parameterized overloaded implicit methods are not visible as view bounds.
    ScalacOption("-Xlint:private-shadow", addedIn = Some(11)),             // A private field (or class parameter) shadows a superclass field.
    ScalacOption("-Xlint:stars-align", addedIn = Some(11)),                // Pattern sequence wildcard must align with sequence component.
    ScalacOption("-Xlint:type-parameter-shadow", addedIn = Some(11)),      // A local type parameter shadows a type already in scope.
    ScalacOption("-Xlint:unsound-match", addedIn = Some(11), removedIn = Some(13)), // Pattern match may not be typesafe.
    ScalacOption("-Yno-adapted-args", removedIn = Some(13)),               // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    ScalacOption("-Ywarn-dead-code"),                  // Warn when dead code is identified.
    ScalacOption("-Ywarn-extra-implicit", addedIn = Some(12)),             // Warn when more than one implicit parameter section is defined.
    ScalacOption("-Ywarn-inaccessible", removedIn = Some(13)),             // Warn about inaccessible types in method signatures.
    ScalacOption("-Ywarn-infer-any", addedIn = Some(11), removedIn = Some(13)), // Warn when a type argument is inferred to be `Any`.
    ScalacOption("-Ywarn-nullary-override", removedIn = Some(13)),         // Warn when non-nullary `def f()' overrides nullary `def f'.
    ScalacOption("-Ywarn-nullary-unit", removedIn = Some(13)),             // Warn when nullary methods return Unit.
    ScalacOption("-Ywarn-numeric-widen"),                                  // Warn when numerics are widened.
    ScalacOption("-Ywarn-unused-import", addedIn = Some(11), removedIn = Some(12)), // Warn if an import selector is not referenced.
    ScalacOption("-Ywarn-unused:implicits", addedIn = Some(12)),           // Warn if an implicit parameter is unused.
    ScalacOption("-Ywarn-unused:imports", addedIn = Some(12)),             // Warn if an import selector is not referenced.
    ScalacOption("-Ywarn-unused:locals", addedIn = Some(12)),              // Warn if a local definition is unused.
    ScalacOption("-Ywarn-unused:params", addedIn = Some(12)),              // Warn if a value parameter is unused.
    ScalacOption("-Ywarn-unused:patvars", addedIn = Some(12)),             // Warn if a variable bound in a pattern is unused.
    ScalacOption("-Ywarn-unused:privates", addedIn = Some(12)),            // Warn if a private member is unused.
    ScalacOption("-Ywarn-value-discard")                                   // Warn when non-Unit expression results are unused.
  )

  object autoImport {
    def scalacOptionsFor(version: String): Seq[String] =
      List(
        "-encoding", "utf-8"                // Specify character encoding used by source files.
      ) ++ (CrossVersion.partialVersion(version) match {
        case Some((2, min)) =>
          allScalacOptions.filter { opt =>
            opt.addedIn.getOrElse(Int.MinValue) <= min &&
              opt.removedIn.getOrElse(Int.MaxValue) > min
          }.map(_.name)
      })

    val filterConsoleScalacOptions = { options: Seq[String] =>
      options.filterNot(Set(
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
