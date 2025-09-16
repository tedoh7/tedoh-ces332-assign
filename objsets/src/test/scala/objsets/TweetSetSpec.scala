package objsets

import org.scalatest.funsuite.AnyFunSuite

class TweetSetSpec extends AnyFunSuite {

  test("filter on empty set") {
    assert(new Empty().filter(_.retweets > 10).isInstanceOf[Empty])
  }

  test("union contains all elements") {
    val t1 = new Tweet("a", "tweet1", 5)
    val t2 = new Tweet("b", "tweet2", 10)
    val set1 = new Empty().incl(t1)
    val set2 = new Empty().incl(t2)
    val u = set1.union(set2)
    assert(u.contains(t1))
    assert(u.contains(t2))
  }

  test("mostRetweeted returns correct tweet") {
    val t1 = new Tweet("a", "tweet1", 5)
    val t2 = new Tweet("b", "tweet2", 15)
    val set = new Empty().incl(t1).incl(t2)
    assert(set.mostRetweeted eq t2)
  }

  test("descendingByRetweet orders correctly") {
    val t1 = new Tweet("a", "tweet1", 5)
    val t2 = new Tweet("b", "tweet2", 15)
    val set = new Empty().incl(t1).incl(t2)
    val list = set.descendingByRetweet
    assert(list.head eq t2)
    assert(list.tail.head eq t1)
  }

  test("google vs apple trending non-empty") {
    val list = GoogleVsApple.trending
    assert(!list.isEmpty)
  }
}
