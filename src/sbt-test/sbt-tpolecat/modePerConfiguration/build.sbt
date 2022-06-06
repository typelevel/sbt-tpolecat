import _root_.io.github.davidgregory084.CiMode
import _root_.io.github.davidgregory084.DevMode
import munit.Assertions._
import scala.util.Try

val Scala211 = "2.11.12"
val Scala212 = "2.12.15"
val Scala213 = "2.13.8"
val Scala30  = "3.0.2"
val Scala31  = "3.1.1"

crossScalaVersions := Seq(
  Scala211,
  Scala212,
  Scala213,
  Scala30,
  Scala31
)

Compile / tpolecatOptionsMode := CiMode
Test / tpolecatOptionsMode := DevMode

TaskKey[Unit]("checkCompileOptions") := {
  val hasFatalWarnings =
    scalacOptions.value.contains("-Xfatal-warnings")

  assert(hasFatalWarnings)
}

TaskKey[Unit]("checkTestOptions") := {
  val hasFatalWarnings =
    (Test / scalacOptions).value.contains("-Xfatal-warnings")

  assert(!hasFatalWarnings)
}
