/*
 * Copyright 2022 David Gregory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.davidgregory084

import org.scalacheck.Gen
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import scala.Ordering.Implicits._

class TpolecatPluginSuite extends AnyFunSuite with ScalaCheckDrivenPropertyChecks {
  val versionGen = Gen.chooseNum(0L, 20L)

  test("valid when no version predicate") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", _ => true)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when neither addedIn nor removedIn"
        )
    }
  }

  test("valid when added in past major release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val addedVersion   = ScalaVersion(currentMaj - 1, 0, 0)
        val scalacOption   = ScalacOption("-some-opt", version => version >= addedVersion)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when addedIn matches past major release"
        )
    }
  }

  test("valid when added in past minor release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val addedVersion   = ScalaVersion(currentMaj, currentMin - 1, 0)
        val scalacOption   = ScalacOption("-some-opt", version => version >= addedVersion)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when addedIn matches past minor release"
        )
    }
  }

  test("valid when added in past patch release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val addedVersion   = ScalaVersion(currentMaj, currentMin, currentPatch - 1)
        val scalacOption   = ScalacOption("-some-opt", version => version >= addedVersion)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when addedIn matches past patch release"
        )
    }
  }

  test("valid when added in this minor/patch release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", version => version >= currentVersion)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when addedIn matches this minor/patch release"
        )
    }
  }

  test("not valid when added in a future major release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val addedVersion   = ScalaVersion(currentMaj + 1, currentMin, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", version => version >= addedVersion)
        assert(
          !scalacOption.isSupported(currentVersion),
          "Should not be valid when addedIn matches a future major release"
        )
    }
  }

  test("not valid when added in a future minor release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val addedVersion   = ScalaVersion(currentMaj, currentMin + 1, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", version => version >= addedVersion)
        assert(
          !scalacOption.isSupported(currentVersion),
          "Should not be valid when addedIn matches a future minor release"
        )
    }
  }

  test("not valid when added in a future patch release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val addedVersion   = ScalaVersion(currentMaj, currentMin, currentPatch + 1)
        val scalacOption   = ScalacOption("-some-opt", version => version >= addedVersion)
        assert(
          !scalacOption.isSupported(currentVersion),
          "Should not be valid when addedIn matches a future patch release"
        )
    }
  }

  test("valid when removed in next major release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val removedVersion = ScalaVersion(currentMaj + 1, 0, 0)
        val scalacOption   = ScalacOption("-some-opt", version => version < removedVersion)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when removedIn matches next major release"
        )
    }
  }

  test("valid when removed in next minor release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val removedVersion = ScalaVersion(currentMaj, currentMin + 1, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", version => version < removedVersion)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when removedIn matches next minor release"
        )
    }
  }

  test("valid when removed in next patch release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val removedVersion = ScalaVersion(currentMaj, currentMin, currentPatch + 1)
        val scalacOption   = ScalacOption("-some-opt", version => version < removedVersion)
        assert(
          scalacOption.isSupported(currentVersion),
          "Should be valid when removedIn matches next patch release"
        )
    }
  }

  test("not valid when removed in this minor/patch release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", version => version < currentVersion)
        assert(
          !scalacOption.isSupported(currentVersion),
          "Should not be valid when removedIn matches this minor/patch release"
        )
    }
  }

  test("not valid when removed in an old major release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val removedVersion = ScalaVersion(currentMaj - 1, currentMin, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", version => version < removedVersion)
        assert(
          !scalacOption.isSupported(currentVersion),
          "Should not be valid when removedIn matches an old major release"
        )
    }
  }

  test("not valid when removed in an old minor release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val removedVersion = ScalaVersion(currentMaj, currentMin - 1, currentPatch)
        val scalacOption   = ScalacOption("-some-opt", version => version < removedVersion)
        assert(
          !scalacOption.isSupported(currentVersion),
          "Should not be valid when removedIn matches an old minor release"
        )
    }
  }

  test("not valid when removed in an old patch release") {
    forAll(versionGen, versionGen, versionGen) {
      (currentMaj: Long, currentMin: Long, currentPatch: Long) =>
        val currentVersion = ScalaVersion(currentMaj, currentMin, currentPatch)
        val removedVersion = ScalaVersion(currentMaj, currentMin, currentPatch - 1)
        val scalacOption   = ScalacOption("-some-opt", version => version < removedVersion)
        assert(
          !scalacOption.isSupported(currentVersion),
          "Should not be valid when removedIn matches an old patch release"
        )
    }
  }
}
