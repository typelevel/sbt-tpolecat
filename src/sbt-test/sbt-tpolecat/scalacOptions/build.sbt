import scala.util.Try

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

crossScalaVersions := {
  object ExtractVersion {
    val sbtVersionMatch = raw"(\d+)\.(\d+)\.(\d+)".r
    def unapply(sbtVersion: String): Option[(Long, Long, Long)] = {
      sbtVersion match {
        case sbtVersionMatch(major, minor, patch) =>
          for {
            maj <- Try(major.toLong).toOption
            min <- Try(minor.toLong).toOption
            p <- Try(patch.toLong).toOption
          } yield (maj, min, p)

        case _ =>
          None
      }
    }
  }

  def supportedSbtVersions(major: Long, minor: Long, patch: Long): Boolean =
    (major, minor, patch) match {
      case (1, 5, z) if z >= 2 => true
      case (x, y, _) if x == 1 && y > 5 => true
      case (x, _, _) if x > 1 => true
      case _ => false
    }

  sbtVersion.value match {
    case ExtractVersion(major, minor, patch) if supportedSbtVersions(major, minor, patch) =>
      scala2Versions ++ scala3Versions

    case _ =>
      scala2Versions
  }
}
