package objsets

import org.junit.Test
import org.junit.Assert._

class TweetSetSuite {
  @Test def `filter on empty set`: Unit = {
    assertTrue(new Empty().filter(t => t.retweets > 10).isInstanceOf[Empty])
  }

  @Test def `union contains all elements`: Unit = {
    val t1 = new Tweet("a", "tweet1", 5)
    val t2 = new Tweet("b", "tweet2", 10)
    val set1 = new Empty().incl(t1)
    val set2 = new Empty().incl(t2)
    val u = set1.union(set2)
    assertTrue(u.contains(t1))
    assertTrue(u.contains(t2))
  }

  @Test def `mostRetweeted returns correct tweet`: Unit = {
    val t1 = new Tweet("a", "tweet1", 5)
    val t2 = new Tweet("b", "tweet2", 15)
    val set = new Empty().incl(t1).incl(t2)
    assertEquals(t2, set.mostRetweeted)
  }

  @Test def `descendingByRetweet orders correctly`: Unit = {
    val t1 = new Tweet("a", "tweet1", 5)
    val t2 = new Tweet("b", "tweet2", 15)
    val set = new Empty().incl(t1).incl(t2)
    val list = set.descendingByRetweet
    assertEquals(t2, list.head)
    assertEquals(t1, list.tail.head)
  }

  @Test def `google vs apple trending non-empty`: Unit = {
    val list = GoogleVsApple.trending
    assert(!list.isEmpty)
  }
}
