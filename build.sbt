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
ThisBuild / crossScalaVersions := Seq("2.12.21", "3.9.0")
ThisBuild / tlJdkRelease       := Some(8)

ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("17"),
  JavaSpec.temurin("21"),
  JavaSpec.temurin("25")
)
ThisBuild / githubWorkflowGeneratedCI ~= (_.map { job =>
  if (job.id == "publish") job.withJavas(List(JavaSpec.temurin("17"))) else job
})
ThisBuild / githubWorkflowBuildMatrixExclusions := Nil
ThisBuild / githubWorkflowBuild                 := Seq(
  // The scripted matrix deliberately compiles historical Scala releases.
  WorkflowStep.Sbt(
    List("test"),
    name = Some("Full tests, including historical compilers"),
    cond = Some("matrix.java == 'temurin@17'")
  ),
  WorkflowStep.Sbt(
    List("sbt-tpolecat-plugin/Test/test"),
    name = Some("Plugin tests on current JDKs"),
    cond = Some("matrix.java != 'temurin@17'")
  )
)

githubWorkflowAddedJobs := {
  val config = tlCiStewardValidateConfig.value
  githubWorkflowAddedJobs.value.map { job =>
    if (job.id == "validate-steward") config.fold(job) { file =>
      val java = List(JavaSpec.temurin("17"))
      job
        .withJavas(java)
        .withSteps(
          WorkflowStep.Checkout :: WorkflowStep.SetupJava(java, false) ::: List(
            WorkflowStep
              .Use(UseRef.Public("coursier", "setup-action", "v1"), Map("apps" -> "scala-steward")),
            WorkflowStep.Run(List(s"scala-steward validate-repo-config $file"))
          )
        )
    }
    else job
  }
}

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
    tlJdkRelease           := (if (scalaBinaryVersion.value == "2.12") Some(8) else Some(17)),
    Compile / headerCreate := { (Compile / headerCreate).triggeredBy(Compile / compile).value },
    Test / headerCreate    := { (Test / headerCreate).triggeredBy(Test / compile).value },
    scalacOptions ++= {
      scalaBinaryVersion.value match {
        case "2.12" => Seq("-Xsource:3", "-Xlint:unused")
        case _      => Seq.empty
      }
    },
    libraryDependencies ++= Seq(
      "org.typelevel" %% "scalac-options" % "0.1.11",
      "org.scalatest" %% "scalatest"      % "3.2.20" % Test
    ),
    addSbtPlugin("com.github.sbt" % "sbt2-compat" % "0.1.0"),
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
        case _      => "2.1.0"
      }
    },
    scriptedSbt := (if (scalaBinaryVersion.value == "2.12") "1.13.1" else "2.1.0")
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
      crossScalaVersions    := Seq("2.12.21")
    )
  )
  .rulesConfigure(_.settings(skipOnNonScala212))
  .inputSettings(
    addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.0"),
    tlFatalWarnings    := false,
    crossScalaVersions := Seq("2.12.21")
  )
  .inputConfigure(_.enablePlugins(SbtPlugin))
  .inputConfigure(_.settings(skipOnNonScala212))
  .outputSettings(
    addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.2"),
    tlFatalWarnings    := false,
    crossScalaVersions := Seq("2.12.21")
  )
  .outputConfigure(_.enablePlugins(SbtPlugin))
  .outputConfigure(_.settings(skipOnNonScala212))
  .testsSettings(
    scalaVersion       := _root_.scalafix.sbt.BuildInfo.scala212,
    crossScalaVersions := Seq(_root_.scalafix.sbt.BuildInfo.scala212),
    semanticdbEnabled  := false
  )
  .testsConfigure(_.settings(skipOnNonScala212))
