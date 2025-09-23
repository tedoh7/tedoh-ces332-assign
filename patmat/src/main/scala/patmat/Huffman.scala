package patmat

object Huffman {

  /** A Huffman coding tree is either a leaf or a fork node. */
  abstract class CodeTree
  case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
  case class Leaf(char: Char, weight: Int) extends CodeTree

  /** The weight of a code tree is the sum of the weights of its leaves. */
  def weight(tree: CodeTree): Int = tree match {
    case Leaf(_, w)              => w
    case Fork(l, r, _, _)        => weight(l) + weight(r)
  }

  /** The list of characters defined by a code tree. */
  def chars(tree: CodeTree): List[Char] = tree match {
    case Leaf(c, _)              => List(c)
    case Fork(l, r, _, _)        => chars(l) ::: chars(r)
  }

  /** Helper to build a fork, computing chars and weight automatically. */
  def makeCodeTree(left: CodeTree, right: CodeTree): CodeTree =
    Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))

  // ---------------------------------------------------------------------------
  // Building Huffman trees
  // ---------------------------------------------------------------------------

  /** Compute the frequency of each character in the given text. */
  def times(cs: List[Char]): List[(Char, Int)] = {
    val counts = cs.foldLeft(Map.empty[Char, Int]) { (m, c) => m.updated(c, m.getOrElse(c, 0) + 1) }
    // keep first-appearance order (nice-to-have)
    cs.distinct.map(c => (c, counts(c)))
  }

  /** Make ordered list of leaves from (char, freq) pairs, ascending by weight. */
  def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] =
    freqs.map { case (c, w) => Leaf(c, w) }.sortBy(_.weight)

  /** Check whether the list contains only one single tree. */
  def singleton(trees: List[CodeTree]): Boolean = trees match {
    case _ :: Nil => true
    case _        => false
  }

  /** Insert a tree into an ordered (by weight) list of trees. */
  private def insertOrdered(t: CodeTree, ts: List[CodeTree]): List[CodeTree] = {
    val w = weight(t)
    val (front, back) = ts.span(weight(_) <= w)
    front ::: t :: back
  }

  /**
    * Combine the two trees with the smallest weight into a single fork,
    * and insert it back into the list, keeping the list ordered by weight.
    */
  def combine(trees: List[CodeTree]): List[CodeTree] = trees match {
    case t1 :: t2 :: rest =>
      val merged = makeCodeTree(t1, t2)
      insertOrdered(merged, rest)
    case _ => trees
  }

  /**
    * Repeatedly apply `combine` until the list is a singleton according to `singleton`.
    */
  def until(done: List[CodeTree] => Boolean, step: List[CodeTree] => List[CodeTree])(trees: List[CodeTree]): List[CodeTree] =
    if (done(trees)) trees else until(done, step)(step(trees))

  /**
    * Create an optimal Huffman code tree for the given text.
    */
  def createCodeTree(cs: List[Char]): CodeTree = {
    val leaves = makeOrderedLeafList(times(cs))
    until(singleton, combine)(leaves) match {
      case tree :: Nil => tree
      case _           => throw new IllegalStateException("createCodeTree: unexpected state")
    }
  }

  // ---------------------------------------------------------------------------
  // Decoding
  // ---------------------------------------------------------------------------

  type Bit = Int

  /** Decode a list of bits using the given code tree. */
  def decode(tree: CodeTree, bits: List[Bit]): List[Char] = {
    @annotation.tailrec
    def loop(node: CodeTree, bs: List[Bit], acc: List[Char]): List[Char] = node match {
      case Leaf(c, _) =>
        bs match {
          case Nil      => acc :+ c
          case _        => loop(tree, bs, acc :+ c) // emit and restart at root
        }
      case Fork(l, r, _, _) =>
        bs match {
          case 0 :: tail => loop(l, tail, acc)
          case 1 :: tail => loop(r, tail, acc)
          case Nil       => acc
          case _         => throw new IllegalArgumentException("Invalid bit sequence")
        }
    }
    loop(tree, bits, Nil)
  }

  // ---------------------------------------------------------------------------
  // Encoding (naive: traverse tree per-char)
  // ---------------------------------------------------------------------------

  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    def pathFor(node: CodeTree, target: Char, prefix: List[Bit]): Option[List[Bit]] = node match {
      case Leaf(c, _) if c == target => Some(prefix)
      case Leaf(_, _)                => None
      case Fork(l, r, _, _) =>
        pathFor(l, target, prefix :+ 0).orElse(pathFor(r, target, prefix :+ 1))
    }
    text.flatMap(c => pathFor(tree, c, Nil).getOrElse(throw new NoSuchElementException(s"'$c' not in tree")))
  }

  // ---------------------------------------------------------------------------
  // Encoding (fast: via code table)
  // ---------------------------------------------------------------------------

  type CodeTable = List[(Char, List[Bit])]

  def codeBits(table: CodeTable)(char: Char): List[Bit] =
    table.collectFirst { case (c, bits) if c == char => bits }
      .getOrElse(throw new NoSuchElementException(s"'$char' not in code table"))

  def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = a ::: b

  def convert(t: CodeTree): CodeTable = {
    def walk(node: CodeTree, prefix: List[Bit]): CodeTable = node match {
      case Leaf(c, _)        => List((c, prefix))
      case Fork(l, r, _, _)  => mergeCodeTables(walk(l, prefix :+ 0), walk(r, prefix :+ 1))
    }
    walk(t, Nil)
  }

  def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    val table = convert(tree)
    text.flatMap(codeBits(table))
  }
}
