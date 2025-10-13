package streams

import org.scalatest.funsuite.AnyFunSuite

class BloxorzSuite extends AnyFunSuite {

  // Level1은 object이므로 그냥 가져와서 사용
  import Level1._

  // 타입 별칭은 GameDef 내부에 있으므로 Level1로 한정
  private implicit val ter: Level1.Terrain = terrain

  /** 헬퍼: 이동 적용 (타입도 Level1.Block으로 한정) */
  private def applyMoves(b: Level1.Block, ms: List[Move]): Level1.Block =
    ms.foldLeft(b) { (cur, m) =>
      m match {
        case Left  => cur.left
        case Right => cur.right
        case Up    => cur.up
        case Down  => cur.down
      }
    }

  test("terrain parsing / start / goal positions are valid") {
    assert(terrain(startPos))
    assert(terrain(goal))
    assert(startBlock.isStanding)
    assert(goal != startPos)
  }

  test("legalNeighbors never leaves terrain") {
    val allLegals = startBlock.legalNeighbors
    assert(allLegals.forall { case (b, _) => b.isLegal(terrain) })
  }

  test("BFS produces a shortest solution (length == 3 for Level1)") {
    val sol = solution
    assert(sol.nonEmpty, "Expected a solution for Level1, but got empty.")
    assert(sol.length == 3, s"Expected shortest length 3, got ${sol.length}.")
  }

  test("Applying solution reaches goal standing and stays legal on every step") {
    val pathBlocks = solution.scanLeft(startBlock){ (b, m) =>
      m match {
        case Left  => b.left
        case Right => b.right
        case Up    => b.up
        case Down  => b.down
      }
    }
    assert(pathBlocks.forall(_.isLegal(terrain)))
    assert(done(pathBlocks.last))
  }

  test("neighborsWithHistory + newNeighborsOnly respect explored set") {
    val n0 = neighborsWithHistory(startBlock, Nil)
    val explored = Set(startBlock)
    val onlyNew = newNeighborsOnly(n0, explored)
    assert(onlyNew.forall { case (b, _) => !explored(b) })
    assert(onlyNew.nonEmpty)
  }
}
