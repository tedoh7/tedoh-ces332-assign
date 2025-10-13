package streams

/** 간단한 레벨 예시 */
object Level1 extends StringParserTerrain {
  // 각 줄이 행(x), 열이 y. (S/T/o/-)
  val level: String =
    """------
      |--ST--
      |--oo--
      |--oo--
      |------
      |""".stripMargin
}
