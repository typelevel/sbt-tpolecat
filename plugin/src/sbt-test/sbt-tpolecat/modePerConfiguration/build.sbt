import _root_.org.typelevel.sbt.tpolecat.CiMode
import _root_.org.typelevel.sbt.tpolecat.DevMode
import munit.Assertions._
import scala.util.Try

val Scala211 = "2.11.12"
val Scala212 = "2.12.17"
val Scala213 = "2.13.8"
val Scala30  = "3.0.2"
val Scala31  = "3.1.3"
val Scala33  = "3.3.0"
val Scala331 = "3.3.1"

crossScalaVersions := Seq(
  Scala211,
  Scala212,
  Scala213,
  Scala30,
  Scala31,
  Scala33,
  Scala331
)

Compile / tpolecatOptionsMode := CiMode
Test / tpolecatOptionsMode    := DevMode

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
