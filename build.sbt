import com.typesafe.tools.mima.core._

// Common settings

name         := "sbt-tpolecat"
description  := "scalac options for the enlightened"
organization := "io.github.davidgregory084"

organizationName := "David Gregory"
startYear        := Some(2022)
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/DavidGregory084/sbt-tpolecat"),
    "scm:git:git@github.com:DavidGregory084/sbt-tpolecat.git"
  )
)
developers := List(
  Developer(
    "DavidGregory084",
    "David Gregory",
    "davidgregory084@gmail.com",
    url("https://github.com/DavidGregory084")
  )
)
homepage := scmInfo.value.map(_.browseUrl)

crossSbtVersions := Seq("1.6.2")

enablePlugins(SbtPlugin)

// Dependencies

addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")

// License headers

Compile / headerCreate := { (Compile / headerCreate).triggeredBy(Compile / compile).value }
Test / headerCreate    := { (Test / headerCreate).triggeredBy(Test / compile).value }

scalacOptions += "-Xlint:unused"

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"       % "3.2.11"   % Test,
  "org.scalacheck"    %% "scalacheck"      % "1.15.4"   % Test,
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test
)

ThisBuild / semanticdbEnabled                              := true
ThisBuild / semanticdbVersion                              := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

ThisBuild / versionScheme := Some(VersionScheme.EarlySemVer)

mimaPreviousArtifacts := Set(
  projectID.value.withRevision("0.2.0")
)

mimaBinaryIssueFilters ++= Seq(
  // New ScalacOptions DSL methods
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.source210"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.source211"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.source212"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.source213"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.source3"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.sourceFuture"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.sourceFutureMigration"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.source3Migration"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.source31"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$source210_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$source211_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$source212_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$source213_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$source3_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$sourceFuture_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$sourceFutureMigration_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$source3Migration_="
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$source31_="
  )
)

// Testing

scriptedBufferLog := false

scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
  "-Xmx1024M",
  "-Dplugin.version=" + version.value
)

test := {
  (Test / test).value
  scripted.toTask("").value
}
