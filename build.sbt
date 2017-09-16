import de.heikoseeberger.sbtheader.license._

// Common settings

name := "sbt-tpolecat"

description := "scalac options for the enlightened"

organization := "io.github.davidgregory084"

version := "0.1.3"

crossSbtVersions := Seq("0.13.16", "1.0.1")

sbtPlugin := true

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// Dependencies

libraryDependencies += Defaults.sbtPluginExtra(
  "org.lyranthe.sbt" % "partial-unification" % "1.1.0",
  (sbtBinaryVersion in pluginCrossBuild).value,
  (scalaBinaryVersion in pluginCrossBuild).value
)

// License headers

headers := Map("scala" -> Apache2_0("2017", "David Gregory"))

createHeaders.in(Compile) := { createHeaders.in(Compile).triggeredBy(compile.in(Compile)).value }

// Publishing

publishMavenStyle := false

bintrayRepository := "sbt-plugins"

bintrayOrganization in bintray := None

// Testing

scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
  "-Xmx1024M", "-Dplugin.version=" + version.value
)

test := {
  (test in Test).value
  scripted.toTask("").value
}
