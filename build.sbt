ThisBuild / tlBaseVersion := "0.5"

ThisBuild / organization     := "org.typelevel"
ThisBuild / organizationName := "Typelevel"

ThisBuild / startYear := Some(2022)
ThisBuild / licenses  := Seq(License.Apache2)

ThisBuild / developers ++= List(
  tlGitHubDev("DavidGregory084", "David Gregory")
)

ThisBuild / semanticdbEnabled := {
  scalaBinaryVersion.value match {
    case "2.12" => true
    case _      => false
  }
}
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

ThisBuild / versionScheme := Some(VersionScheme.EarlySemVer)

ThisBuild / scalaVersion       := "2.12.21"
ThisBuild / crossScalaVersions := Seq("2.12.21", "3.8.3")
ThisBuild / tlJdkRelease       := {
  scalaBinaryVersion.value match {
    case "2.12" => Some(8)
    case _      => Some(17)
  }
}

ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("17")
)

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
    scalacOptions ++= {
      scalaBinaryVersion.value match {
        case "2.12" => Seq("-Xlint:unused")
        case _      => Seq.empty
      }
    },
    libraryDependencies ++= Seq(
      "org.typelevel" %% "scalac-options" % "0.1.10",
      "org.scalatest" %% "scalatest"      % "3.2.20" % Test
    ),
    mimaPreviousArtifacts := Set(
    ),
    mimaBinaryIssueFilters ++= Seq(
    ),
    scriptedBufferLog  := false,
    scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + version.value
    ),
    test := {
      (Test / test).value
      scripted.toTask("").value
    },
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.11.4"
        case _      => "2.0.0-RC12"
      }
    }
  )

// Skip scalafix tasks unless the build's active Scala (driven by `++` on the
// plugin project, which is the only project that actually cross-builds) is 2.12.
// The scalafix sub-projects' own scalaBinaryVersion is always 2.12 because they
// pin crossScalaVersions, so we have to read the plugin's value instead.
lazy val skipOnNonScala212: Seq[Setting[?]] = Seq(
  Compile / compile / skip := (`sbt-tpolecat-plugin` / scalaBinaryVersion).value != "2.12",
  Test / compile / skip    := (`sbt-tpolecat-plugin` / scalaBinaryVersion).value != "2.12",
  Test / test / skip       := (`sbt-tpolecat-plugin` / scalaBinaryVersion).value != "2.12",
  publish / skip           := (`sbt-tpolecat-plugin` / scalaBinaryVersion).value != "2.12"
)

lazy val `sbt-tpolecat-scalafix` = scalafixProject("sbt-tpolecat")
  .rulesConfigure(project =>
    project.settings(
      mimaPreviousArtifacts := Set.empty,
      crossScalaVersions    := Seq("2.12.20")
    )
  )
  .rulesConfigure(_.settings(skipOnNonScala212))
  .inputSettings(
    addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.0"),
    tlFatalWarnings    := false,
    crossScalaVersions := Seq("2.12.20")
  )
  .inputConfigure(_.enablePlugins(SbtPlugin))
  .inputConfigure(_.settings(skipOnNonScala212))
  .outputSettings(
    addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.3"),
    tlFatalWarnings    := false,
    crossScalaVersions := Seq("2.12.20")
  )
  .outputConfigure(_.enablePlugins(SbtPlugin))
  .outputConfigure(_.settings(skipOnNonScala212))
  .testsSettings(
    scalaVersion       := _root_.scalafix.sbt.BuildInfo.scala212,
    crossScalaVersions := Seq(_root_.scalafix.sbt.BuildInfo.scala212),
    semanticdbEnabled  := false
  )
  .testsConfigure(_.settings(skipOnNonScala212))
