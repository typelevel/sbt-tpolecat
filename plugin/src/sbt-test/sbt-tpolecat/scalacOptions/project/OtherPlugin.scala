package build

import sbt.Keys._
import sbt._

object OtherPlugin extends AutoPlugin {
  object autoImport {
    val otherPluginActivate = settingKey[Boolean]("activate the options of OtherPlugin")
  }

  import autoImport._

  override def globalSettings: Seq[Setting[_]] = Seq(
    otherPluginActivate := false
  )

  override def projectSettings: Seq[Setting[_]] = Seq(
    scalacOptions ++= {
      if (otherPluginActivate.value) Seq("other-plugin-option-1")
      else Nil
    },
    Compile / scalacOptions ++= {
      if (otherPluginActivate.value) Seq("other-plugin-option-2")
      else Nil
    }
  )
}
