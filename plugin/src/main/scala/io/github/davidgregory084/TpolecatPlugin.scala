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

import sbt.Keys._
import sbt._

import scala.util.Try

object TpolecatPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  import ScalaVersion._

  object autoImport {
    object ScalacOptions extends ScalacOptions

    private[TpolecatPlugin] def supportedOptionsFor(
      version: String,
      modeScalacOptions: Set[ScalacOption]
    ): Set[ScalacOption] = {
      (CrossVersion.partialVersion(version), version.split('.')) match {
        case (Some((0, _)), _) => // dotty prereleases use 0 as major version
          modeScalacOptions
            .filter(_.isSupported(V3_0_0)) // treat dotty prereleases as 3.0.0
        case (Some((maj, min)), Array(maj2, min2, patch))
            if maj.toString == maj2 && min.toString == min2 =>
          modeScalacOptions
            .filter(_.isSupported(ScalaVersion(maj, min, Try(patch.toLong).getOrElse(0))))
        case (Some((maj, min)), _) =>
          modeScalacOptions
            .filter(_.isSupported(ScalaVersion(maj, min, 0)))
        case (None, _) =>
          Set.empty[ScalacOption]
      }
    }

    def scalacOptionsFor(
      version: String,
      modeScalacOptions: Set[ScalacOption]
    ): Seq[String] = {
      supportedOptionsFor(version, modeScalacOptions).toList.flatMap(opt => opt.option :: opt.args)
    }

    val tpolecatDefaultOptionsMode = settingKey[OptionsMode](
      "The default mode to use for configuring scalac options via the sbt-tpolecat plugin."
    )

    val tpolecatOptionsMode = settingKey[OptionsMode](
      "The mode to use for configuring scalac options via the sbt-tpolecat plugin."
    )

    val tpolecatVerboseModeEnvVar = settingKey[String](
      "The environment variable to use to enable the sbt-tpolecat verbose mode."
    )

    val tpolecatDevModeEnvVar = settingKey[String](
      "The environment variable to use to enable the sbt-tpolecat development mode."
    )

    val tpolecatCiModeEnvVar = settingKey[String](
      "The environment variable to use to enable the sbt-tpolecat continuous integration mode."
    )

    val tpolecatReleaseModeEnvVar = settingKey[String](
      "The environment variable to use to enable the sbt-tpolecat release mode."
    )

    val tpolecatVerboseModeOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that will be applied by the sbt-tpolecat plugin in the verbose mode."
    )

    val tpolecatDevModeOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that will be applied by the sbt-tpolecat plugin in the development mode."
    )

    val tpolecatCiModeOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that will be applied by the sbt-tpolecat plugin in the continuous integration mode."
    )

    val tpolecatReleaseModeOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that will be applied by the sbt-tpolecat plugin in the release mode."
    )

    val tpolecatScalacOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that will be applied by the sbt-tpolecat plugin."
    )

    val tpolecatExcludeOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that will be excluded."
    )

    val tpolecatEffectiveScalacOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that will effectively be applied by the sbt-tpolecat. For internal use only."
    ).withRank(sbt.KeyRanks.Invisible)

    val tpolecatManagedScalacOptions = settingKey[Set[ScalacOption]](
      "The set of scalac options that sbt-tpolecat owns and manages. Defaults to anything it ever adds in any scope delegation chain."
    ).withRank(sbt.KeyRanks.DSetting)
  }

  import autoImport._

  val commandAliases =
    addCommandAlias(
      "tpolecatVerboseMode",
      "set every tpolecatOptionsMode := _root_.io.github.davidgregory084.VerboseMode"
    ) ++
      addCommandAlias(
        "tpolecatDevMode",
        "set every tpolecatOptionsMode := _root_.io.github.davidgregory084.DevMode"
      ) ++
      addCommandAlias(
        "tpolecatCiMode",
        "set every tpolecatOptionsMode := _root_.io.github.davidgregory084.CiMode"
      ) ++
      addCommandAlias(
        "tpolecatReleaseMode",
        "set every tpolecatOptionsMode := _root_.io.github.davidgregory084.ReleaseMode"
      )

  override def buildSettings: Seq[Setting[_]] = Seq(
    tpolecatDefaultOptionsMode := CiMode,
    tpolecatVerboseModeEnvVar  := "SBT_TPOLECAT_VERBOSE",
    tpolecatDevModeEnvVar      := "SBT_TPOLECAT_DEV",
    tpolecatCiModeEnvVar       := "SBT_TPOLECAT_CI",
    tpolecatReleaseModeEnvVar  := "SBT_TPOLECAT_RELEASE",
    tpolecatOptionsMode := {
      if (sys.env.contains(tpolecatReleaseModeEnvVar.value)) ReleaseMode
      else if (sys.env.contains(tpolecatCiModeEnvVar.value)) CiMode
      else if (sys.env.contains(tpolecatDevModeEnvVar.value)) DevMode
      else if (sys.env.contains(tpolecatVerboseModeEnvVar.value)) VerboseMode
      else tpolecatDefaultOptionsMode.value
    },
    tpolecatDevModeOptions := ScalacOptions.default
  ) ++ commandAliases

  private def removeOption(options: List[String], optionToRemove: ScalacOption): List[String] = {
    val option = optionToRemove.option
    val args   = optionToRemove.args

    if (args.isEmpty) {
      // fast path
      options.filterNot(_ == option)
    } else {
      def loop(options: List[String]): List[String] = options match {
        case Nil                                       => Nil
        case `option` :: tail if tail.startsWith(args) => loop(tail.drop(args.size))
        case head :: tail                              => head :: loop(tail)
      }
      loop(options)
    }
  }

  private def addOption(options: List[String], optionToAdd: ScalacOption): List[String] = {
    val option = optionToAdd.option
    val args   = optionToAdd.args

    if (args.isEmpty) {
      // fast path
      if (options.contains(option)) options
      else options ::: option :: Nil
    } else {
      if (options.containsSlice(option :: args)) options
      else options ::: option :: args
    }
  }

  override def projectSettings: Seq[Setting[_]] = Seq(
    Def.derive(
      scalacOptions := {
        val prevOptions      = scalacOptions.value.toList
        val managedOptions   = tpolecatManagedScalacOptions.value
        val effectiveOptions = tpolecatEffectiveScalacOptions.value

        val optionsToRemove = managedOptions.diff(effectiveOptions)
        val optionsToAdd    = effectiveOptions

        val optionsWithRemovals = optionsToRemove.foldLeft(prevOptions)(removeOption(_, _))
        optionsToAdd.foldLeft(optionsWithRemovals)(addOption(_, _))
      }
    ),
    Def.derive(
      tpolecatManagedScalacOptions ++= tpolecatEffectiveScalacOptions.value
    ),
    Def.derive(
      tpolecatEffectiveScalacOptions := {
        val pluginOptions   = tpolecatScalacOptions.value
        val pluginExcludes  = tpolecatExcludeOptions.value
        val selectedOptions = pluginOptions.diff(pluginExcludes)
        supportedOptionsFor(scalaVersion.value, selectedOptions)
      }
    ),
    Def.derive(
      tpolecatVerboseModeOptions := tpolecatDevModeOptions.value ++ ScalacOptions.verboseOptions
    ),
    Def.derive(
      tpolecatCiModeOptions := tpolecatDevModeOptions.value + ScalacOptions.fatalWarnings
    ),
    Def.derive(
      tpolecatReleaseModeOptions := tpolecatCiModeOptions.value + ScalacOptions.optimizerMethodLocal
    ),
    Def.derive(tpolecatScalacOptions := {
      tpolecatOptionsMode.value match {
        case VerboseMode => tpolecatVerboseModeOptions.value
        case DevMode     => tpolecatDevModeOptions.value
        case CiMode      => tpolecatCiModeOptions.value
        case ReleaseMode => tpolecatReleaseModeOptions.value
      }
    }),
    Compile / console / tpolecatExcludeOptions ++= ScalacOptions.defaultConsoleExclude,
    Test / console / tpolecatExcludeOptions ++= ScalacOptions.defaultConsoleExclude
  )

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    tpolecatManagedScalacOptions := Set.empty,
    tpolecatExcludeOptions       := Set.empty
  )
}
