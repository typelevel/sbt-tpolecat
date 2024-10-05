import com.typesafe.tools.mima.core._

ThisBuild / tlBaseVersion := "0.5"

ThisBuild / organization     := "org.typelevel"
ThisBuild / organizationName := "Typelevel"

ThisBuild / startYear := Some(2022)
ThisBuild / licenses  := Seq(License.Apache2)

ThisBuild / developers ++= List(
  tlGitHubDev("DavidGregory084", "David Gregory")
)

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

ThisBuild / versionScheme := Some(VersionScheme.EarlySemVer)

ThisBuild / crossScalaVersions := Seq("2.12.19", "3.3.4")

lazy val `sbt-tpolecat` = project
  .in(file("."))
  .enablePlugins(NoPublishPlugin)
  .aggregate(
    `sbt-tpolecat-plugin`,
    `sbt-tpolecat-scalafix`.all
  )

lazy val `sbt-tpolecat-plugin` = project
  .in(file("plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    name                   := "sbt-tpolecat",
    moduleName             := "sbt-tpolecat",
    description            := "scalac options for the enlightened",
    Compile / headerCreate := { (Compile / headerCreate).triggeredBy(Compile / compile).value },
    Test / headerCreate    := { (Test / headerCreate).triggeredBy(Test / compile).value },
    scalacOptions += "-Xlint:unused",
    libraryDependencies ++= Seq(
      "org.typelevel"     %% "scalac-options"  % "0.1.7",
      "org.scalatest"     %% "scalatest"       % "3.2.19"   % Test,
      "org.scalacheck"    %% "scalacheck"      % "1.18.1"   % Test,
      "org.scalatestplus" %% "scalacheck-1-16" % "3.2.14.0" % Test
    ),
    mimaPreviousArtifacts := Set(
    ),
    mimaBinaryIssueFilters ++= Seq(
    ),
    scriptedBufferLog := false,
    scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + version.value
    ),
    test := {
      (Test / test).value
      scripted.toTask("").value
    },
    sbtVersion := { if (scalaVersion.value.startsWith("3.")) "2.0.0-M2" else "1.10.2" }
  )

lazy val `sbt-tpolecat-scalafix` = scalafixProject("sbt-tpolecat")
  .rulesConfigure(project =>
    project.settings(
      mimaPreviousArtifacts := Set(
      ),
      crossScalaVersions := Seq("2.12.19")
    )
  )
  .inputSettings(addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.0"))
  .inputSettings(tlFatalWarnings := false)
  .inputConfigure(_.enablePlugins(SbtPlugin))
  .outputSettings(tlFatalWarnings := false)
  .outputConfigure(_.dependsOn(`sbt-tpolecat-plugin`))
  .outputConfigure(_.enablePlugins(SbtPlugin))
