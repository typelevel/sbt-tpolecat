## sbt-tpolecat

[![Build Status](https://github.com/DavidGregory084/sbt-tpolecat/workflows/CI/badge.svg)](https://github.com/DavidGregory084/sbt-tpolecat/actions?query=workflow%3ACI)
[![License](https://img.shields.io/github/license/DavidGregory084/sbt-tpolecat.svg)](https://opensource.org/licenses/Apache-2.0)

### scalac options for the enlightened

sbt-tpolecat is an SBT plugin for automagically configuring scalac options according to the project Scala version, inspired by Rob Norris ([@tpolecat](https://github.com/tpolecat))'s excellent series of blog posts providing [recommended options](https://tpolecat.github.io/2017/04/25/scalac-flags.html) to get the most out of the compiler.

It also enables the excellent [sbt-partial-unification](https://github.com/fiadliel/sbt-partial-unification) plugin for those Scala versions where it is needed.

As of version 0.1.11, it also supports setting options for Scala 3.x.

### Usage

Add the following to your project's `project/plugins.sbt`:

```scala
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.2.1")
```

Once you are using this plugin we recommend that you don't manipulate the `scalacOptions` key directly.

Instead you should modify the `tpolecatScalacOptions` key or the options key for the relevant mode, for example `tpolecatDevModeOptions` for the development mode.

### Configuring the REPL

To filter out scala compiler options that don't work well in the REPL, use the `tpolecatConsoleOptionsFilter`.

By default the plugin only applies this filter to the `console` task in the `Compile` and `Test` configurations.

For example, to apply this filter to the `console` task in the `IntegrationTest` configuration you can do the following:

```scala
IntegrationTest / console / tpolecatScalacOptions ~= tpolecatConsoleOptionsFilter
```

### Modes

This plugin can be used in several modes. The default mode is the continous integration mode.

Modes can selected by using mode switching commands, or by setting environment variables.

When multiple mode-setting environment variables are defined, the most restrictive mode is selected. For example, if the `SBT_TPOLECAT_DEV` and `SBT_TPOLECAT_CI` variables are both defined, continuous integration mode will be enabled.

You can customise the default mode by modifying the `ThisBuild / tpolecatDefaultOptionsMode` key. Default: `CiMode`.

#### Development mode

To enable the development mode, use the `tpolecatDevMode` command or define the environment variable `SBT_TPOLECAT_DEV`.

In this mode a baseline set of scala compiler options are enabled.

You can customise the options that are enabled in this mode by modifying the `tpolecatDevModeOptions` key. Default: `ScalacOptions.default`.

For example, to disable macros you could customise the development mode options as follows:

```scala
tpolecatDevModeOptions ~= { opts =>
  opts.filterNot(Set(ScalacOptions.languageExperimentalMacros))
}
```

You can customise the environment variable that is used to enable this mode by modifying the `ThisBuild / tpolecatDevModeEnvVar` key. Default: `"SBT_TPOLECAT_DEV"`.

#### Continuous integration mode

To enable the continuous integration mode, use the `tpolecatCiMode` command or define the environment variable `SBT_TPOLECAT_CI`.

In this mode all development mode options are enabled, and the fatal warnings option (`-Xfatal-warnings` ) is added.

You can customise the options that are enabled in this mode by modifying the `tpolecatCiModeOptions` key. Default: `tpolecatDevModeOptions.value + ScalacOptions.fatalWarnings`.

For example, to disable unused linting you could customise the CI options as follows:

```scala
tpolecatCiModeOptions ~= { opts =>
  opts.filterNot(
    ScalacOptions.privateWarnUnusedOptions ++
      ScalacOptions.warnUnusedOptions
  )
}
```

You can customise the environment variable that is used to enable this mode by modifying the `ThisBuild / tpolecatCiModeEnvVar` key. Default: `"SBT_TPOLECAT_CI"`.

#### Release mode

To enable the release mode, use the `tpolecatReleaseMode` command or define the environment variable `SBT_TPOLECAT_RELEASE`.

In this mode all CI mode options are enabled, and the method-local optimisation option (`-opt:l:method`) is enabled if available for your Scala version.

You can customise the options that are enabled in this mode by modifying the `tpolecatReleaseModeOptions` key. Default: `tpolecatCiModeOptions.value + ScalacOptions.optimizerMethodLocal`.

For example, to enable inlining within your library or application's packages you could customise the release options as follows:

```scala
tpolecatReleaseModeOptions ++= ScalacOptions.optimizerOptions("your.library.**")
```

To understand more about the Scala optimizer read [The Scala 2.12 / 2.13 Inliner and Optimizer](https://docs.scala-lang.org/overviews/compiler-options/optimizer.html).

You can customise the environment variable that is used to enable this mode by modifying the `ThisBuild / tpolecatReleaseModeEnvVar` key. Default: `"SBT_TPOLECAT_RELEASE"`.

### Caveat

I can't promise this plugin will work for old minor releases of Scala.

It is currently tested with Scala 2.x releases:

* 2.13.8
* 2.12.15
* 2.11.12

and Scala 3.x releases:

* 3.0.2
* 3.1.1

### Conduct

Participants are expected to follow the [Scala Code of Conduct](https://www.scala-lang.org/conduct/) while discussing the project on GitHub and any other venues associated with the project.

### License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).
