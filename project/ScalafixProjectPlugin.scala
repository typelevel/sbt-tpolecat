import sbt._, Keys._

import com.typesafe.tools.mima.plugin.MimaPlugin.autoImport._
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import scalafix.sbt._
import ScalafixPlugin.autoImport._
import ScalafixTestkitPlugin.autoImport._

object ScalafixProjectPlugin extends AutoPlugin {
  object autoImport {
    lazy val V                        = _root_.scalafix.sbt.BuildInfo
    def scalafixProject(name: String) = ScalafixProject(name)
  }
}

class ScalafixProject private (
  val name: String,
  val rules: Project,
  val input: Project,
  val output: Project,
  val tests: Project
) extends CompositeProject {

  lazy val componentProjects = Seq(all, rules, input, output, tests)

  lazy val all = Project(s"$name-scalafix", file(s"target/$name-scalafix-aggregate"))
    .aggregate(rules, input, output, tests)
    .settings(
      publish                := {},
      publishLocal           := {},
      publishArtifact        := false,
      publish / skip         := true,
      mimaReportBinaryIssues := {}
    )

  def rulesSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    rulesConfigure(_.settings(ss: _*))

  def inputSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    inputConfigure(_.settings(ss: _*))

  def outputSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    outputConfigure(_.settings(ss: _*))

  def testsSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    testsConfigure(_.settings(ss: _*))

  def rulesConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules.configure(transforms: _*),
      input,
      output,
      tests
    )

  def inputConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules,
      input.configure(transforms: _*),
      output,
      tests
    )

  def outputConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules,
      input,
      output.configure(transforms: _*),
      tests
    )

  def testsConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules,
      input,
      output,
      tests.configure(transforms: _*)
    )

}

object ScalafixProject {
  def apply(name: String): ScalafixProject = {

    lazy val rules = Project(s"$name-scalafix-rules", file(s"scalafix/rules"))
      .settings(
        moduleName := s"$name-scalafix",
        libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % _root_.scalafix.sbt.BuildInfo.scalafixVersion
      )

    lazy val input = Project(s"$name-scalafix-input", file(s"scalafix/input"))
      .settings(
        headerSources / excludeFilter := AllPassFilter,
        scalacOptions ~= { opts => opts.filterNot(Set("-Xfatal-warnings", "-Werror")) }
      )
      .settings(
        publish                := {},
        publishLocal           := {},
        publishArtifact        := false,
        publish / skip         := true,
        mimaReportBinaryIssues := {}
      )

    lazy val output = Project(s"$name-scalafix-output", file(s"scalafix/output"))
      .settings(
        headerSources / excludeFilter := AllPassFilter,
        scalacOptions ~= { opts => opts.filterNot(Set("-Xfatal-warnings", "-Werror")) }
      )
      .settings(
        publish                := {},
        publishLocal           := {},
        publishArtifact        := false,
        publish / skip         := true,
        mimaReportBinaryIssues := {}
      )

    lazy val tests = Project(s"$name-scalafix-tests", file(s"scalafix/tests"))
      .settings(
        scalafixTestkitOutputSourceDirectories := (output / Compile / unmanagedSourceDirectories).value,
        scalafixTestkitInputSourceDirectories := (input / Compile / unmanagedSourceDirectories).value,
        scalafixTestkitInputClasspath     := (input / Compile / fullClasspath).value,
        scalafixTestkitInputScalacOptions := (input / Compile / scalacOptions).value,
        scalafixTestkitInputScalaVersion  := (input / Compile / scalaVersion).value
      )
      .dependsOn(rules)
      .enablePlugins(ScalafixTestkitPlugin)
      .settings(
        publish                := {},
        publishLocal           := {},
        publishArtifact        := false,
        publish / skip         := true,
        mimaReportBinaryIssues := {}
      )

    new ScalafixProject(name, rules, input, output, tests)
  }
}
