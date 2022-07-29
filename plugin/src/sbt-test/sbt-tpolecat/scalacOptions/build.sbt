import _root_.io.github.davidgregory084.JavaMajorVersion
import munit.Assertions._
import scala.util.Try

val Scala211 = "2.11.12"
val Scala212 = "2.12.16"
val Scala213 = "2.13.8"
val Scala30  = "3.0.2"
val Scala31  = "3.1.3"

crossScalaVersions := Seq(
  Scala211,
  Scala212,
  Scala213,
  Scala30,
  Scala31
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
    "-Ywarn-value-discard",
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
  )

val Scala212Options =
  Seq(
    "-Xlint:adapted-args",
    "-Xlint:by-name-right-associative",
    "-Xlint:constant",
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
    "-Xsource:2.13",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-extra-implicit",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused:implicits",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:params",
    "-Ywarn-unused:patvars",
    "-Ywarn-unused:privates",
    "-Ywarn-value-discard",
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
  )

val Scala213Options =
  Seq(
    "-Wdead-code",
    "-Wextra-implicit",
    "-Wnumeric-widen",
    "-Wunused:explicits",
    "-Wunused:implicits",
    "-Wunused:imports",
    "-Wunused:locals",
    "-Wunused:nowarn",
    "-Wunused:params",
    "-Wunused:patvars",
    "-Wunused:privates",
    "-Wvalue-discard",
    "-Xlint:-byname-implicit",
    "-Xlint:adapted-args",
    "-Xlint:constant",
    "-Xlint:delayedinit-select",
    "-Xlint:deprecation",
    "-Xlint:doc-detached",
    "-Xlint:implicit-not-found",
    "-Xlint:implicit-recursion",
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
    "-Xsource:2.13",
    "-encoding",
    "utf8",
    "-feature",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
  )

val Scala30Options =
  Seq(
    "-Ykind-projector",
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-source",
    "3.0-migration",
    "-unchecked",
  )

val Scala31Options =
  Seq(
    "-Ykind-projector",
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-source",
    "3.0-migration",
    "-unchecked",
  )

TaskKey[Unit]("checkDevMode") := {
  val scalaV = scalaVersion.value

  val expectedOptions = scalaV match {
    case Scala211 => Scala211Options
    case Scala212 => Scala212Options
    case Scala213 => Scala213Options
    case Scala30  => Scala30Options
    case Scala31  => Scala31Options
  }

  val actualOptions = (Compile / scalacOptions).value

  assertEquals(
    actualOptions,
    expectedOptions,
    s"Expected dev mode options were not applied for $scalaV"
  )
}

TaskKey[Unit]("checkCiMode") := {
  val scalaV = scalaVersion.value

  val expectedOptions = scalaV match {
    case Scala211 => "-Xfatal-warnings" +: Scala211Options
    case Scala212 => Scala212Options
    case Scala213 => Scala213Options
    case Scala30  => Scala30Options
    case Scala31  => Scala31Options
  }

  val actualOptions = (Compile / scalacOptions).value

  assertEquals(
    actualOptions,
    expectedOptions,
    s"Expected CI mode options were not applied for $scalaV"
  )
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
    case Scala30 => Scala30Options ++ fatalWarnings ++ releaseOptions
    case Scala31 => Scala31Options ++ fatalWarnings ++ releaseOptions
  }

  val actualOptions = (Compile / scalacOptions).value

  assertEquals(
    actualOptions,
    expectedOptions,
    s"Expected release mode options were not applied for $scalaV"
  )
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
