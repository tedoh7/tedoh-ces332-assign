package patmat

import org.scalatest.funsuite.AnyFunSuite

class HuffmanSuite extends AnyFunSuite {
  import Huffman._

  test("weight/chars of simple tree") {
    val t = makeCodeTree(Leaf('a',2), Leaf('b',3))
    assert(weight(t) == 5)
    assert(chars(t).toSet == Set('a','b'))
  }

  test("times preserves first-appearance order") {
    val res = times("aabbbc".toList)
    assert(res == List(('a',2),('b',3),('c',1)))
  }

  test("makeOrderedLeafList sorts ascending") {
    val leaves = makeOrderedLeafList(List(('t',2), ('e',1), ('x',1)))
    assert(leaves.map(_.char) == List('e','x','t'))
    assert(leaves.map(_.weight) == List(1,1,2))
  }

  test("combine merges two smallest and keeps order") {
    val leaves: List[CodeTree] = List(Leaf('e',1), Leaf('x',1), Leaf('t',2))
    val combined = combine(leaves)
    // after one combine, list size decreases by 1
    assert(combined.size == 2)
    // combined head should have weight 2 (1+1), then 2
    assert(weight(combined.head) == 2)
  }

  test("until + createCodeTree build a single optimal tree") {
    val text = "huffmanestcool".toList
    val tree = createCodeTree(text)
    assert(chars(tree).toSet.subsetOf(text.toSet))
    assert(weight(tree) == text.length)
  }

  test("encode/quickEncode agree and decode returns original") {
    val text = "scala huffman test!".toList
    val tree = createCodeTree(text)
    val bits1 = encode(tree)(text)
    val bits2 = quickEncode(tree)(text)
    assert(bits1 == bits2)
    val decoded = decode(tree, bits1)
    assert(decoded == text)
  }
}
