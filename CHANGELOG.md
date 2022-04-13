# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Early Semantic Versioning](https://docs.scala-lang.org/overviews/core/binary-compatibility-for-library-authors.html#recommended-versioning-scheme) in addition to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.3] - 2022-04-14

### Added

* Begin keeping [this changelog](./CHANGELOG.md).
* Added `-Xsource` (Scala 2.x) and `-source` (Scala 3.x) early migration settings to the ScalacOptions DSL.

### Fixed

* [#60](https://github.com/DavidGregory084/sbt-tpolecat/issues/60) - a bug in setting `scalacOptions` where it was set using `:=` rather than appended to via `++=`. This prevented scope delegation via `ThisBuild / scalacOptions` from working for some users.

## [0.2.2] - 2022-03-30

### Fixed

* Ensure that all keys with dependencies are derived settings, so that e.g. `Test / tpolecatScalacOptions` can be used to manipulate `Test / scalacOptions`.
* Add a `toString` to `ScalacOption`.

## [0.2.1] - 2022-03-30

### Added

* Enable [MiMa](https://github.com/lightbend/mima) checks in GitHub Actions workflows.
* Set `versionScheme` to clarify version compatibility claims.

### Fixed

* Apply `-Xfatal-warnings` regardless of version once more. Applying `-Werror` for Scala 2.13.x causes problems for users who currently filter out `-Xfatal-warnings` from `scalacOptions` explicitly.

### Changed

* Expanded usage instructions to guide users toward the `ScalacOptions` DSL.

## [0.2.0] - 2022-03-30

### Added

* Development, CI and release modes for setting options differently according to the context.
* Add a simple `ScalacOptions` DSL for setting options in each mode.
* Add mode-setting commands `tpolecatDevMode`, `tpolecatCiMode`, `tpolecatReleaseMode`.
* Environment variable checks in order to decide which mode to enable on startup.

### Changed

* The signature of `scalacOptionsFor` exported via this plugin's `autoImport` - it now requires a `Set` of all selected `ScalacOptions` for the current mode in addition to the current Scala version.
* The `filterConsoleScalacOptions` function exported via this plugin's `autoImport` was renamed to `tpolecatConsoleOptionsFilter` for consistency with other keys provided by the plugin.

### Removed

* The `validFor` function that was previously exported via this plugin's `autoImport`.

[Unreleased]: https://github.com/DavidGregory084/sbt-tpolecat/compare/v0.2.3...HEAD
[0.2.3]: https://github.com/DavidGregory084/sbt-tpolecat/compare/v0.2.2...v0.2.3
[0.2.2]: https://github.com/DavidGregory084/sbt-tpolecat/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/DavidGregory084/sbt-tpolecat/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/DavidGregory084/sbt-tpolecat/compare/v0.1.22...v0.2.0
