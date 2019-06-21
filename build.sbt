// Common settings

name := "sbt-tpolecat"
description := "scalac options for the enlightened"
organization := "io.github.davidgregory084"

organizationName := "David Gregory"
startYear := Some(2019)
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

crossSbtVersions := Seq("0.13.18", "1.2.8")

enablePlugins(SbtPlugin)

// Dependencies

addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")

// License headers

Compile / headerCreate := { (Compile / headerCreate).triggeredBy(Compile / compile).value }

// Publishing

publishMavenStyle := false
bintrayRepository := "sbt-plugins"
bintrayOrganization in bintray := None

// Testing

scriptedBufferLog := false

scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
  "-Xmx1024M", "-Dplugin.version=" + version.value
)

test := {
  (Test / test).value
  scripted.toTask("").value
}
