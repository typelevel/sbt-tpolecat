val scala2Versions = Seq(
  "2.10.7",
  "2.11.12",
  "2.12.12",
  "2.13.3",
  "2.13.4",
  "2.13.5"
)

crossScalaVersions := (CrossVersion.partialVersion(sbtVersion.value) match {
  case Some((1, _)) => scala2Versions :+ "0.26.0" :+ "0.27.0-RC1" :+ "3.0.0-M1" // the dotty plugin isn't available for sbt 0.13.x, so we can only add the dotty version here
  case _ => scala2Versions
})
