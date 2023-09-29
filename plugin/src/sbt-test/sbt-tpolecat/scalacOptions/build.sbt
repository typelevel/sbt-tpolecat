import org.typelevel.scalacoptions._
import munit.Assertions._
import scala.util.Try

val Scala211 = "2.11.12"
val Scala212 = "2.12.17"
val Scala213 = "2.13.8"
val Scala30  = "3.0.2"
val Scala31  = "3.1.3"
val Scala33  = "3.3.0"
val Scala331 = "3.3.1"

enablePlugins(OtherPlugin)

crossScalaVersions := Seq(
  Scala211,
  Scala212,
  Scala213,
  Scala30,
  Scala31,
  Scala33,
  Scala331
)

tpolecatDevModeOptions ++= Set(
  ScalacOptions.source213,
  ScalacOptions.source3Migration
)

tpolecatReleaseModeOptions ++= {
  ScalacOptions.optimizerOptions("**") +
    ScalacOptions.release("8") +
    ScalacOptions.privateBackendParallelism(8)
}

val Scala211Options =
  Seq(
    "-encoding",
    "utf8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Xlint:adapted-args",
    "-Xlint:by-name-right-associative",
    "-Xlint:delayedinit-select",
    "-Xlint:doc-detached",
    "-Xlint:inaccessible",
    "-Xlint:infer-any",
    "-Xlint:missing-interpolator",
    "-Xlint:nullary-override",
    "-Xlint:nullary-unit",
    "-Xlint:option-implicit",
    "-Xlint:package-object-classes",
    "-Xlint:poly-implicit-overload",
    "-Xlint:private-shadow",
    "-Xlint:stars-align",
    "-Xlint:type-parameter-shadow",
    "-Xlint:unsound-match",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-Ywarn-value-discard"
  )

val Scala212Options =
  Seq(
    "-encoding",
    "utf8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Xlint:adapted-args",
    "-Xlint:by-name-right-associative",
    "-Xlint:constant",
    "-Xlint:delayedinit-select",
    "-Xlint:deprecation",
    "-Xlint:doc-detached",
    "-Xlint:inaccessible",
    "-Xlint:infer-any",
    "-Xlint:missing-interpolator",
    "-Xlint:nullary-override",
    "-Xlint:nullary-unit",
    "-Xlint:option-implicit",
    "-Xlint:package-object-classes",
    "-Xlint:poly-implicit-overload",
    "-Xlint:private-shadow",
    "-Xlint:stars-align",
    "-Xlint:type-parameter-shadow",
    "-Xlint:unsound-match",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-extra-implicit",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-unused:implicits",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:params",
    "-Ywarn-unused:patvars",
    "-Ywarn-unused:privates",
    "-Ywarn-unused:nowarn",
    "-Xsource:2.13"
  )

val Scala213Options =
  Seq(
    "-encoding",
    "utf8",
    "-feature",
    "-unchecked",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Xlint:adapted-args",
    "-Xlint:constant",
    "-Xlint:delayedinit-select",
    "-Xlint:deprecation",
    "-Xlint:doc-detached",
    "-Xlint:implicit-recursion",
    "-Xlint:implicit-not-found",
    "-Xlint:inaccessible",
    "-Xlint:infer-any",
    "-Xlint:missing-interpolator",
    "-Xlint:nullary-unit",
    "-Xlint:option-implicit",
    "-Xlint:package-object-classes",
    "-Xlint:poly-implicit-overload",
    "-Xlint:private-shadow",
    "-Xlint:stars-align",
    "-Xlint:strict-unsealed-patmat",
    "-Xlint:type-parameter-shadow",
    "-Xlint:-byname-implicit",
    "-Wdead-code",
    "-Wextra-implicit",
    "-Wnumeric-widen",
    "-Wvalue-discard",
    "-Wunused:nowarn",
    "-Wunused:implicits",
    "-Wunused:explicits",
    "-Wunused:imports",
    "-Wunused:locals",
    "-Wunused:params",
    "-Wunused:patvars",
    "-Wunused:privates",
    "-Xsource:2.13"
  )

val Scala30Options =
  Seq(
    "-encoding",
    "utf8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ykind-projector",
    "-source",
    "3.0-migration"
  )

val Scala31Options =
  Seq(
    "-encoding",
    "utf8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ykind-projector",
    "-source",
    "3.0-migration"
  )
val Scala33Options =
  Seq(
    "-encoding",
    "utf8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ykind-projector",
    "-Wvalue-discard",
    "-Wunused:implicits",
    "-Wunused:explicits",
    "-Wunused:imports",
    "-Wunused:locals",
    "-Wunused:params",
    "-Wunused:privates",
    "-source",
    "3.0-migration"
  )
val Scala331Options =
  Seq(
    "-encoding",
    "utf8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ykind-projector",
    "-Wvalue-discard",
    "-Wnonunit-statement",
    "-Wunused:implicits",
    "-Wunused:explicits",
    "-Wunused:imports",
    "-Wunused:locals",
    "-Wunused:params",
    "-Wunused:privates",
    "-source",
    "3.0-migration"
  )

TaskKey[Unit]("checkDevMode") := {
  val scalaV = scalaVersion.value

  val expectedOptions = scalaV match {
    case Scala211 => Scala211Options
    case Scala212 => Scala212Options
    case Scala213 => Scala213Options
    case Scala30  => Scala30Options
    case Scala31  => Scala31Options
    case Scala33  => Scala33Options
    case Scala331 => Scala331Options
  }

  val actualOptions = scalacOptions.value

  assertEquals(actualOptions, expectedOptions)
}

TaskKey[Unit]("checkVerboseMode") := {
  val scalaV = scalaVersion.value

  val expectedOptions = scalaV match {
    case Scala211 => Scala211Options ++ Seq("-explaintypes")
    case Scala212 => Scala212Options ++ Seq("-explaintypes")
    case Scala213 => Scala213Options ++ Seq("-Vimplicits", "-Vtype-diffs", "-explaintypes")
    case Scala30  => Scala30Options ++ Seq("-explain")
    case Scala31  => Scala31Options ++ Seq("-explain")
    case Scala33  => Scala33Options ++ Seq("-explain")
    case Scala331 => Scala331Options ++ Seq("-explain")
  }

  val actualOptions = scalacOptions.value

  assertEquals(actualOptions, expectedOptions)
}

TaskKey[Unit]("checkCiMode") := {
  val scalaV = scalaVersion.value

  val expectedOptions = scalaV match {
    case Scala211 => Scala211Options ++ Seq("-Xfatal-warnings")
    case Scala212 => Scala212Options ++ Seq("-Xfatal-warnings")
    case Scala213 => Scala213Options ++ Seq("-Xfatal-warnings")
    case Scala30  => Scala30Options ++ Seq("-Xfatal-warnings")
    case Scala31  => Scala31Options ++ Seq("-Xfatal-warnings")
    case Scala33  => Scala33Options ++ Seq("-Xfatal-warnings")
    case Scala331 => Scala331Options ++ Seq("-Xfatal-warnings")
  }

  val actualOptions = scalacOptions.value

  assertEquals(actualOptions, expectedOptions)
}

TaskKey[Unit]("checkReleaseMode") := {
  val scalaV = scalaVersion.value

  val fatalWarnings = Seq("-Xfatal-warnings")

  val optimizerMethodLocal = Seq("-opt:l:method")

  val optimizerInline = Seq(
    "-opt-inline-from:**",
    "-opt:l:inline"
  )

  val releaseOptions =
    if (JavaMajorVersion.javaMajorVersion >= 9)
      Seq("-release", "8")
    else
      Seq.empty

  val expectedOptions = scalaV match {
    case Scala211 =>
      Scala211Options ++ fatalWarnings
    case Scala212 =>
      Scala212Options ++ fatalWarnings ++ optimizerMethodLocal ++ releaseOptions ++ optimizerInline ++ Seq(
        "-Ybackend-parallelism",
        "8"
      )
    case Scala213 =>
      Scala213Options ++ fatalWarnings ++ optimizerMethodLocal ++ releaseOptions ++ optimizerInline ++ Seq(
        "-Ybackend-parallelism",
        "8"
      )
    case Scala30  => Scala30Options ++ fatalWarnings ++ releaseOptions
    case Scala31  => Scala31Options ++ fatalWarnings ++ releaseOptions
    case Scala33  => Scala33Options ++ fatalWarnings ++ releaseOptions
    case Scala331 => Scala331Options ++ fatalWarnings ++ releaseOptions
  }

  val actualOptions = scalacOptions.value

  assertEquals(actualOptions, expectedOptions)
}

TaskKey[Unit]("checkConsoleScalacOptions") := {
  val shouldBeMissing =
    ScalacOptions.defaultConsoleExclude.flatMap(opt => opt.option :: opt.args).toSet
  val testConsoleOptions    = (Test / console / scalacOptions).value
  val compileConsoleOptions = (Compile / console / scalacOptions).value

  testConsoleOptions.foreach { opt =>
    assert(!shouldBeMissing.contains(opt), s"$opt is not excluded from Test/console")
  }

  compileConsoleOptions.foreach { opt =>
    assert(!shouldBeMissing.contains(opt), s"$opt is not excluded from Compile/console")
  }
}

addCommandAlias(
  "addScalacOptionsToThisProject",
  "set ThisProject / scalacOptions += \"non-existent-key\""
)

TaskKey[Unit]("checkThisProjectScalacOptions") := {
  val options = (Compile / scalacOptions).value
  assert(options.contains("non-existent-key"), "Scope ThisProject was ignored")
}

addCommandAlias(
  "addOtherPluginsScalacOptions",
  "set ThisProject / otherPluginActivate := true"
)

TaskKey[Unit]("checkOtherPluginsScalacOptions") := {
  val optionsProject = scalacOptions.value
  assert(
    optionsProject.contains("other-plugin-option-1"),
    "Project scope of OtherPlugin was ignored in Project"
  )
  assert(
    !optionsProject.contains("other-plugin-option-2"),
    "Unexpected Compile-only setting in Project"
  )

  val optionsCompile = (Compile / scalacOptions).value
  assert(
    optionsCompile.contains("other-plugin-option-1"),
    "Project scope of OtherPlugin was ignored in Compile"
  )
  assert(
    optionsCompile.contains("other-plugin-option-2"),
    "Compile scope of OtherPlugin was ignored in Compile"
  )
}
