package objsets

abstract class TweetSet {
  def filter(p: Tweet => Boolean): TweetSet =
    filterAcc(p, new Empty)

  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet
  def union(that: TweetSet): TweetSet
  def mostRetweeted: Tweet
  def descendingByRetweet: TweetList

  def incl(tweet: Tweet): TweetSet
  def remove(tweet: Tweet): TweetSet
  def contains(tweet: Tweet): Boolean
  def foreach(f: Tweet => Unit): Unit
}

class Empty extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = acc
  def union(that: TweetSet): TweetSet = that
  def mostRetweeted: Tweet =
    throw new java.util.NoSuchElementException("Empty.mostRetweeted")
  def descendingByRetweet: TweetList = Nil

  def contains(tweet: Tweet): Boolean = false
  def incl(tweet: Tweet): TweetSet = new NonEmpty(tweet, new Empty, new Empty)
  def remove(tweet: Tweet): TweetSet = this
  def foreach(f: Tweet => Unit): Unit = ()
}

class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = {
    val acc2 = if (p(elem)) acc.incl(elem) else acc
    val acc3 = left.filterAcc(p, acc2)
    right.filterAcc(p, acc3)
  }

  def union(that: TweetSet): TweetSet =
    left.union(right.union(that)).incl(elem)

  def mostRetweeted: Tweet = {
    def max(a: Tweet, b: Tweet): Tweet =
      if (a.retweets >= b.retweets) a else b
    val leftMax  = try left.mostRetweeted catch { case _: Throwable => elem }
    val rightMax = try right.mostRetweeted catch { case _: Throwable => leftMax }
    max(leftMax, rightMax)
  }

  def descendingByRetweet: TweetList = {
    val top = mostRetweeted
    new Cons(top, this.remove(top).descendingByRetweet)
  }

  def contains(x: Tweet): Boolean =
    if (x.text < elem.text) left.contains(x)
    else if (elem.text < x.text) right.contains(x)
    else true

  def incl(x: Tweet): TweetSet =
    if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
    else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
    else this

  def remove(tw: Tweet): TweetSet =
    if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
    else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
    else left.union(right)

  def foreach(f: Tweet => Unit): Unit = {
    left.foreach(f); f(elem); right.foreach(f)
  }
}

abstract class TweetList {
  def head: Tweet
  def tail: TweetList
  def isEmpty: Boolean
  def foreach(f: Tweet => Unit): Unit =
    if (!isEmpty) { f(head); tail.foreach(f) }
}

object Nil extends TweetList {
  def head = throw new java.util.NoSuchElementException("head of EmptyList")
  def tail = throw new java.util.NoSuchElementException("tail of EmptyList")
  def isEmpty = true
}

class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
  def isEmpty = false
}
