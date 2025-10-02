package forcomp

import org.scalatest.funsuite.AnyFunSuite

class AnagramsSuite extends AnyFunSuite {
  import Anagrams._

  test("wordOccurrences basic") {
    assert(wordOccurrences("Scala") === List(('a',2), ('c',1), ('l',1), ('s',1)))
  }

  test("sentenceOccurrences merges words") {
    // "You olive" 에서 u는 총 1번 등장
    assert(sentenceOccurrences(List("You", "olive")) ===
      List(('e',1), ('i',1), ('l',1), ('o',2), ('u',1), ('v',1), ('y',1)))
  }

  test("combinations of List(('a',2),('b',2)) size check") {
    val combos = combinations(List('a' -> 2, 'b' -> 2))
    // 0..2 for a (3가지) × 0..2 for b (3가지) = 9
    assert(combos.size == 9)
    assert(combos.contains(Nil))
    assert(combos.contains(List('a' -> 1)))
    assert(combos.contains(List('b' -> 2)))
  }

  test("subtract example") {
    val x = List(('a',1),('d',1),('l',1),('r',1))
    val y = List(('r',1))
    assert(subtract(x,y) === List(('a',1),('d',1),('l',1)))
  }

  test("sentenceAnagrams short sentence (terminates quickly)") {
    val result = sentenceAnagrams(List("You","olive"))
    assert(result.exists(_.map(_.toLowerCase) == List("i","love","you")))
  }
}
