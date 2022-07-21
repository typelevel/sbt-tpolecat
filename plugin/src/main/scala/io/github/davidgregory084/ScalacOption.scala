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

/** A Scala compiler option.
  *
  * @param option
  *   The flag that is used to declare this option.
  * @param args
  *   The arguments provided to this option.
  * @param isSupported
  *   A predicate function determining whether the provided Scala compiler version supports this
  *   option.
  */
class ScalacOption(
  val option: String,
  val args: List[String],
  val isSupported: ScalaVersion => Boolean
) {
  override def hashCode(): Int =
    41 * option.hashCode

  override def equals(other: Any): Boolean =
    other match {
      case that: ScalacOption => this.option == that.option
      case _                  => false
    }

  override def toString =
    (option :: args).mkString("ScalacOption(", " ", ")")
}

object ScalacOption {
  def apply(option: String, isSupported: ScalaVersion => Boolean): ScalacOption =
    new ScalacOption(option, Nil, isSupported)

  def apply(
    option: String,
    args: List[String],
    isSupported: ScalaVersion => Boolean
  ): ScalacOption =
    new ScalacOption(option, args, isSupported)
}
