val scala2Versions = Seq(
  "2.10.7",
  "2.11.12",
  "2.12.12",
  "2.13.3",
  "2.13.4",
  "2.13.5",
  "2.13.6"
)

val scala3Versions = Seq(
  "3.0.0-RC3",
  "3.0.0"
)

crossScalaVersions := (CrossVersion.sbtApiVersion(sbtVersion.value) match {
  case Some((1, 5)) => scala2Versions ++ scala3Versions
  case _ => scala2Versions
})
