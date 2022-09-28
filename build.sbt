import com.typesafe.tools.mima.core._

ThisBuild / organization     := "io.github.davidgregory084"
ThisBuild / organizationName := "David Gregory"

ThisBuild / startYear := Some(2022)
ThisBuild / licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/DavidGregory084/sbt-tpolecat"),
    "scm:git:git@github.com:DavidGregory084/sbt-tpolecat.git"
  )
)
ThisBuild / developers := List(
  Developer(
    "DavidGregory084",
    "David Gregory",
    "davidgregory084@gmail.com",
    url("https://github.com/DavidGregory084")
  )
)

ThisBuild / homepage := scmInfo.value.map(_.browseUrl)

ThisBuild / semanticdbEnabled                              := true
ThisBuild / semanticdbVersion                              := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

ThisBuild / versionScheme := Some(VersionScheme.EarlySemVer)

lazy val `sbt-tpolecat` = project
  .in(file("."))
  .aggregate(
    `sbt-tpolecat-plugin`,
    `sbt-tpolecat-scalafix`.rules,
    `sbt-tpolecat-scalafix`.input,
    `sbt-tpolecat-scalafix`.tests
    /* TODO: change individual Scalafix project dependencies to `sbt-tpolecat-scalafix`.all when the package rename is in main;
     * the Scalafix `output` project will not compile until the package renaming is done.
     */
  )
  .settings(
    publish                := {},
    publishLocal           := {},
    publishArtifact        := false,
    publish / skip         := true,
    mimaReportBinaryIssues := {}
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
      "org.scalatest"     %% "scalatest"       % "3.2.13"   % Test,
      "org.scalacheck"    %% "scalacheck"      % "1.17.0"   % Test,
      "org.scalatestplus" %% "scalacheck-1-16" % "3.2.14.0" % Test
    ),
    mimaPreviousArtifacts := Set(
      projectID.value.withRevision("0.4.0").withExplicitArtifacts(Vector.empty)
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
    }
  )

lazy val `sbt-tpolecat-scalafix` = scalafixProject("sbt-tpolecat")
  .rulesConfigure(project =>
    project.settings(
      mimaPreviousArtifacts := Set(
        (project / projectID).value.withRevision("0.4.1").withExplicitArtifacts(Vector.empty)
      )
    )
  )
  .inputSettings(
    libraryDependencies += (`sbt-tpolecat-plugin` / projectID).value.withRevision("0.4.0")
  )
  .outputConfigure(_.dependsOn(`sbt-tpolecat-plugin`))
