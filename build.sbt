name := "sbt-tpolecat"

organization := "io.github.davidgregory084"

scalaVersion := "2.10.6"

sbtPlugin := true

scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
  "-Xmx1024M", "-Dplugin.version=" + version.value
)

test := {
  (test in Test).value
  scripted.toTask("").value
}

addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.0.0")
