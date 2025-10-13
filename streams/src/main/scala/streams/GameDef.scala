package streams

/** 좌표 */
case class Pos(x: Int, y: Int) {
  def delta(dx: Int, dy: Int): Pos = Pos(x + dx, y + dy)
}

/** 이동 */
sealed abstract class Move
case object Left  extends Move
case object Right extends Move
case object Up    extends Move
case object Down  extends Move

/** 게임 공통 정의 */
trait GameDef {
  type Terrain = Pos => Boolean

  /** 레벨에서 파싱된 값 */
  val terrain: Terrain
  val startPos: Pos
  val goal: Pos

  /** 블록(2칸짜리 직육면체)을 두 꼭짓점으로 표현 */
  case class Block(b1: Pos, b2: Pos) {
    def isStanding: Boolean = {
      // TODO: 두 좌표가 동일하면 선 상태
      b1 == b2
    }

    /** 블록 이동: 왼/오/위/아래 */
    def left: Block = if (isStanding)
      Block(b1.delta(0, -2), b2.delta(0, -1))
    else if (b1.x == b2.x) // 가로로 누움
      Block(b1.delta(0, -1), b2.delta(0, -2))
    else                    // 세로로 누움
      Block(b1.delta(0, -1), b2.delta(0, -1))

    def right: Block = if (isStanding)
      Block(b1.delta(0, 1), b2.delta(0, 2))
    else if (b1.x == b2.x)
      Block(b1.delta(0, 2), b2.delta(0, 1))
    else
      Block(b1.delta(0, 1), b2.delta(0, 1))

    def up: Block = if (isStanding)
      Block(b1.delta(-2, 0), b2.delta(-1, 0))
    else if (b1.x == b2.x)
      Block(b1.delta(-1, 0), b2.delta(-1, 0))
    else
      Block(b1.delta(-1, 0), b2.delta(-2, 0))

    def down: Block = if (isStanding)
      Block(b1.delta(1, 0), b2.delta(2, 0))
    else if (b1.x == b2.x)
      Block(b1.delta(1, 0), b2.delta(1, 0))
    else
      Block(b1.delta(2, 0), b2.delta(1, 0))

    /** 지형 위에 완전히 존재하는가 */
    def isLegal(implicit t: Terrain): Boolean = {
      // TODO: 두 좌표 모두 지형 내부여야 함
      t(b1) && t(b2)
    }

    /** 이 블록에서 가능한 이웃 블록들과 그에 해당하는 이동 */
    def neighbors: List[(Block, Move)] = List(
      (left,  Left),
      (right, Right),
      (up,    Up),
      (down,  Down)
    )

    /** 합법 이웃만 필터링 */
    def legalNeighbors(implicit t: Terrain): List[(Block, Move)] =
      neighbors.filter { case (nb, _) => nb.isLegal(t) }
  }

  /** 시작 블록 (시작 좌표 위에 세워진 상태) */
  def startBlock: Block = Block(startPos, startPos)
}
