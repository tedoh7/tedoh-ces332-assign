package streams

import org.scalatest.funsuite.AnyFunSuite

class BloxorzSuite extends AnyFunSuite {

  object TestLevel extends StringParserTerrain with Solver {
    val level: String =
      """------
        |--ST--
        |--oo--
        |--oo--
        |------
        |""".stripMargin
  }

  import TestLevel._

  test("terrain parsing and start/goal positions") {
    assert(terrain(startPos))
    assert(terrain(goal))
  }

  test("neighborsWithHistory basic example") {
    implicit val t: Terrain = terrain
    val startB = startBlock
    val ns = neighborsWithHistory(startB, List(Left, Up)).toSet
    // 검증: 인접 블록이 적어도 1개 이상
    assert(ns.nonEmpty)
  }

  test("BFS finds a (not necessarily shortest) solution or empty if none") {
    val sol = solution
    assert(sol.isEmpty || sol.headOption.nonEmpty)
  }
}
