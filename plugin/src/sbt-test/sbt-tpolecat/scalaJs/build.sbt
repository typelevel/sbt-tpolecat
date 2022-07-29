import munit.Assertions._

val Scala211 = "2.11.12"
val Scala212 = "2.12.16"
val Scala213 = "2.13.8"
val Scala30  = "3.0.2"
val Scala31  = "3.1.3"

crossScalaVersions := Seq(
  Scala211,
  Scala212,
  Scala213,
  Scala30,
  Scala31
)

enablePlugins(ScalaJSPlugin)

TaskKey[Unit]("checkScalaJsOption") := {
  val actualOptions = (Compile / scalacOptions).value

  scalaVersion.value match {
    case Scala30 =>
      assert(actualOptions.contains("-scalajs"), "The -scalajs option was not present on Scala 3.0")
    case Scala31 =>
      assert(actualOptions.contains("-scalajs"), "The -scalajs option was not present on Scala 3.1")
    case _ =>
      assert(!actualOptions.contains("-scalajs"), "The -scalajs option was present on Scala 2.x")
  }
}
