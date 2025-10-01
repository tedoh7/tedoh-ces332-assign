package forcomp

object Anagrams {

  type Word = String
  type Sentence = List[Word]
  type Occurrences = List[(Char, Int)]

  // 사전은 클래스패스 리소스에서 읽음
  val dictionary: List[Word] = loadDictionary

  /** 단어 -> 발생 리스트 */
  def wordOccurrences(w: Word): Occurrences =
    w.toLowerCase
      .filter(_.isLetter)
      .groupBy(identity)
      .map { case (c, cs) => (c, cs.length) }
      .toList
      .sortBy(_._1)

  /** 문장 -> 발생 리스트 */
  def sentenceOccurrences(s: Sentence): Occurrences =
    wordOccurrences(s.mkString)

  /** 발생 리스트별 사전 단어들 */
  lazy val dictionaryByOccurrences: Map[Occurrences, List[Word]] =
    dictionary.groupBy(wordOccurrences).withDefaultValue(Nil)

  /** 단어 애너그램들 */
  def wordAnagrams(word: Word): List[Word] =
    dictionaryByOccurrences(wordOccurrences(word))

  /** 발생 리스트의 모든 부분집합 */
  def combinations(occurrences: Occurrences): List[Occurrences] = occurrences match {
    case Nil => List(Nil)
    case (ch, cnt) :: rest =>
      val restCombs = combinations(rest)
      val withThis =
        (for {
          n <- 1 to cnt
          comb <- restCombs
        } yield (ch, n) :: comb).toList
      restCombs ++ withThis
  }

  /** x 에서 y 를 뺀 발생 리스트 (전제: y ⊆ x) */
  def subtract(x: Occurrences, y: Occurrences): Occurrences = {
    val yMap = y.toMap.withDefaultValue(0)
    x.map { case (ch, n) => (ch, n - yMap(ch)) }
      .filter { case (_, n) => n > 0 }
  }

  /** 문장 애너그램 전부 */
  def sentenceAnagrams(sentence: Sentence): List[Sentence] = {
    val occ = sentenceOccurrences(sentence)
    def loop(rem: Occurrences): List[Sentence] = rem match {
      case Nil => List(Nil)
      case _ =>
        for {
          combo <- combinations(rem) if combo.nonEmpty
          word  <- dictionaryByOccurrences(combo)
          tail  <- loop(subtract(rem, combo))
        } yield word :: tail
    }
    loop(occ)
  }

  /** 클래스패스 리소스에서 사전 읽기 */
  def loadDictionary: List[Word] = {
    val is = Option(getClass.getResourceAsStream("/forcomp/linuxwords.txt"))
      .getOrElse(throw new RuntimeException("Dictionary resource not found: forcomp/linuxwords.txt"))
    val src = scala.io.Source.fromInputStream(is)
    try src.getLines().toList
    finally src.close()
  }
}
