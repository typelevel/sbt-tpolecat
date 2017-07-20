import de.heikoseeberger.sbtheader.license._

name := "sbt-tpolecat"

organization := "io.github.davidgregory084"

scalaVersion := "2.10.6"

headers := Map("scala" -> Apache2_0("2017", "David Gregory"))

createHeaders.in(Compile) := { createHeaders.in(Compile).triggeredBy(compile.in(Compile)).value }

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
