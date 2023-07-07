import _root_.io.github.davidgregory084._

lazy val subProject = project
  .settings(tpolecatOptionsMode := CiMode)

lazy val subProjectWithConfig = project
  .settings(Test / tpolecatOptionsMode := CiMode)

TaskKey[Unit]("check") := {
  assert(tpolecatOptionsMode.value == DevMode)
  assert((subProject / tpolecatOptionsMode).value == DevMode)
  assert((subProjectWithConfig / Test / tpolecatOptionsMode).value == DevMode)
}
