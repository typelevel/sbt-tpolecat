import com.typesafe.tools.mima.core._

ThisBuild / tlBaseVersion := "0.4"

ThisBuild / organization     := "org.typelevel"
ThisBuild / organizationName := "Typelevel"
ThisBuild / developers       := List(tlGitHubDev("DavidGregory084", "David Gregory"))

ThisBuild / licenses := Seq(License.Apache2)

ThisBuild / crossScalaVersions := Seq("2.12.15")

ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("8"),
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17")
)

ThisBuild / semanticdbEnabled                              := true
ThisBuild / semanticdbVersion                              := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

name        := "sbt-tpolecat"
description := "scalac options for the enlightened"

enablePlugins(SbtPlugin)

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"       % "3.2.11"   % Test,
  "org.scalacheck"    %% "scalacheck"      % "1.15.4"   % Test,
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test
)

mimaPreviousArtifacts := Set(
  projectID.value
    .withOrganization("io.github.davidgregory084")
    .withRevision("0.3.0")
)

mimaBinaryIssueFilters ++= Seq(
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.privatePartialUnification"
  ),
  ProblemFilters.exclude[ReversedMissingMethodProblem](
    "io.github.davidgregory084.ScalacOptions.io$github$davidgregory084$ScalacOptions$_setter_$privatePartialUnification_="
  )
)

scriptedBufferLog := false

scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
  "-Xmx1024M",
  "-Dplugin.version=" + version.value
)

test := {
  (Test / test).value
  scripted.toTask("").value
}
