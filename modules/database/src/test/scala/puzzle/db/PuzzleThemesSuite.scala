package puzzle
package db
package tests

import weaver.*
import eu.timepit.refined.types.string.NonEmptyString

object PuzzleThemesSuite extends SimpleIOSuite:

  val name1 = NonEmptyString.unsafeFrom("test1")
  val name2 = NonEmptyString.unsafeFrom("test2")

  private def resource =
    Fixture.createRepositoryResource

  // test("create success"):
  //   resource.use: reposity =>
  //     reposity.themes.
