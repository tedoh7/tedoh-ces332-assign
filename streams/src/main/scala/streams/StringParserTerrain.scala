package streams

import scala.annotation.tailrec

/** 문자열 지형 파서
  * 'o' = 유효 타일, '-' = 빈공간, 'S' = 시작, 'T' = 목표
  */
trait StringParserTerrain extends GameDef {
  val level: String

  private lazy val levelVector: Vector[Vector[Char]] =
    Vector(level.split("\n").filter(_.nonEmpty): _*).map(_.toVector)

  /** 주어진 좌표가 지형 내부인지 (o, S, T) */
  def terrainFunction(vv: Vector[Vector[Char]]): Pos => Boolean = {
    pos =>
      pos.x >= 0 && pos.x < vv.length &&
      pos.y >= 0 && pos.y < vv(pos.x).length && {
        val c = vv(pos.x)(pos.y)
        c == 'o' || c == 'S' || c == 'T'
      }
  }

  /** 특정 문자(S 또는 T)의 좌표 찾기 */
  def findChar(c: Char, vv: Vector[Vector[Char]]): Pos = {
    val x = vv.indexWhere(_.contains(c))
    val y = if (x >= 0) vv(x).indexOf(c) else -1
    if (x < 0 || y < 0) throw new NoSuchElementException(s"Char '$c' not found")
    Pos(x, y)
  }

  // GameDef 구현 연결
  lazy val terrain: Terrain = terrainFunction(levelVector)
  lazy val startPos: Pos    = findChar('S', levelVector)
  lazy val goal: Pos        = findChar('T', levelVector)
}
