addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % System.getProperty("plugin.version"))

libraryDependencies ++= {
  CrossVersion.partialVersion(sbtVersion.value) match {
    case Some((1, _)) => // the dotty plugin isn't available for sbt 0.13.x
      Seq(Defaults.sbtPluginExtra("ch.epfl.lamp" % "sbt-dotty" % "0.4.0", (sbtBinaryVersion in update).value, (scalaBinaryVersion in update).value))
    case _ =>
      Seq.empty
  }
}
