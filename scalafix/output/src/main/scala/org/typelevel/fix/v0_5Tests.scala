// format: off
import org.typelevel.sbt.tpolecat.{ DevMode => Dev, TpolecatPlugin, _ }
import org.typelevel.sbt.tpolecat.TpolecatPlugin.{ autoImport, _ }
import org.typelevel.sbt.tpolecat.TpolecatPlugin.autoImport.{ scalacOptionsFor, tpolecatCiModeEnvVar, _ }
import org.typelevel.scalacoptions.{ ScalaVersion => Version, ScalacOption, ScalacOptions, _ }
import org.typelevel.scalacoptions.ScalaVersion.{ V2_11_0, _ }
import org.typelevel.scalacoptions.ScalacOptions.{ checkInit, _ }
// format: on

object Tests
