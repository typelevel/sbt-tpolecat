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
  projectID.value.withRevision("0.3.0")
)

mimaBinaryIssueFilters ++= Seq()

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
