import scala.util.Try

val scala2Versions = Seq(
  "2.11.12",
  "2.12.15",
  "2.13.8"
)

val scala3Versions = Seq(
  "3.0.2",
  "3.1.1"
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
      case (1, 5, patch) if patch >= 2 => true
      case (1, minor, _) if minor > 5 => true
      case (major, _, _) if major > 1 => true
      case _ => false
    }

  sbtVersion.value match {
    case ExtractVersion(major, minor, patch) if supportedSbtVersions(major, minor, patch) =>
      scala2Versions ++ scala3Versions

    case _ =>
      scala2Versions
  }
}
