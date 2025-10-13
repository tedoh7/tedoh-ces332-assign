package streams

/** 격자 좌표: x = 행(row), y = 열(col) */
case class Pos(x: Int, y: Int) {
  def delta(dx: Int, dy: Int): Pos = Pos(x + dx, y + dy)
}

/** 이동 방향 */
sealed abstract class Move
case object Left  extends Move
case object Right extends Move
case object Up    extends Move
case object Down  extends Move

/** 공통 게임 정의 */
trait GameDef {
  type Terrain = Pos => Boolean

  /** 레벨에서 파싱된 값 */
  val terrain: Terrain
  val startPos: Pos
  val goal: Pos

  /** 1x1x2 블록을 두 격자 좌표로 표현 (두 좌표가 같으면 선 상태) */
  case class Block(b1: Pos, b2: Pos) {
    /** 좌표 표준화: (x, y) 사전순 작은 게 b1이 되도록 */
    private def normalize: Block =
      if (b1.x < b2.x || (b1.x == b2.x && b1.y <= b2.y)) this else Block(b2, b1)

    /** 선(서있는) 상태인지 */
    def isStanding: Boolean = b1 == b2

    /** 왼쪽으로 구르기 */
    def left: Block = {
      val nb =
        if (isStanding) Block(b1.delta(0, -2), b2.delta(0, -1))
        else if (b1.x == b2.x) Block(b1.delta(0, -1), b2.delta(0, -2))   // 가로: 서게 됨
        else Block(b1.delta(0, -1), b2.delta(0, -1))                      // 세로: 그대로 세로
      nb.normalize
    }

    /** 오른쪽으로 구르기 */
    def right: Block = {
      val nb =
        if (isStanding) Block(b1.delta(0, 1), b2.delta(0, 2))
        else if (b1.x == b2.x) Block(b1.delta(0, 2), b2.delta(0, 1))      // 가로: 서게 됨
        else Block(b1.delta(0, 1), b2.delta(0, 1))                        // 세로: 그대로 세로
      nb.normalize
    }

    /** 위로 구르기 */
    def up: Block = {
      val nb =
        if (isStanding) Block(b1.delta(-2, 0), b2.delta(-1, 0))
        else if (b1.x == b2.x) Block(b1.delta(-1, 0), b2.delta(-1, 0))    // 가로: 그대로 가로
        else Block(b1.delta(-1, 0), b2.delta(-2, 0))                      // 세로: 서게 됨
      nb.normalize
    }

    /** 아래로 구르기 */
    def down: Block = {
      val nb =
        if (isStanding) Block(b1.delta(1, 0), b2.delta(2, 0))
        else if (b1.x == b2.x) Block(b1.delta(1, 0), b2.delta(1, 0))      // 가로: 그대로 가로
        else Block(b1.delta(2, 0), b2.delta(1, 0))                        // 세로: 서게 됨
      nb.normalize
    }

    /** 현재 블록이 지형 위에 완전히 존재하는가 (두 좌표 모두 유효) */
    def isLegal(implicit t: Terrain): Boolean = t(b1) && t(b2)

    /** 이 블록에서 가능한 이웃들과 해당 이동 */
    def neighbors: List[(Block, Move)] =
      List( (left, Left), (right, Right), (up, Up), (down, Down) )

    /** 합법 이웃만 */
    def legalNeighbors(implicit t: Terrain): List[(Block, Move)] =
      neighbors.filter { case (nb, _) => nb.isLegal(t) }
  }

  /** 시작 블록: 시작 좌표 위에 선 상태 */
  def startBlock: Block = Block(startPos, startPos)
}
