import _root_.org.typelevel.sbt.tpolecat._

lazy val subProject = project
  .settings(tpolecatOptionsMode := CiMode)

lazy val subProjectWithConfig = project
  .settings(Test / tpolecatOptionsMode := CiMode)

@transient
lazy val check = taskKey[Unit]("check")
check := {
  assert(tpolecatOptionsMode.value == DevMode)
  assert((subProject / tpolecatOptionsMode).value == DevMode)
  assert((subProjectWithConfig / Test / tpolecatOptionsMode).value == DevMode)
}
