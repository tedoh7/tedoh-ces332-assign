package streams

/** 간단하지만 검증 가능한 레벨
  * 행/열 인덱스:
  *   0: ------  (벽)
  *   1: --ST--
  *   2: --oo--
  *   3: --oo--
  *   4: ------  (벽)
  *
  * S=(1,2)에서 시작해 아래(Down)→오른쪽(Right)→위(Up)로 진행하면
  * T=(1,3) 위에 서서 도달. 최단 길이 = 3.
  */
object Level1 extends StringParserTerrain with Solver {
  val level: String =
    """------
      |--ST--
      |--oo--
      |--oo--
      |------""".stripMargin
}
