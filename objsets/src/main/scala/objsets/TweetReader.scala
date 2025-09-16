package objsets

object TweetReader {
  // 로컬 테스트용 작은 데이터셋(핵심 로직 검증 목적)
  private val seed: List[Tweet] = List(
    new Tweet("tech", "Google announces new Pixel phone", 180),
    new Tweet("fan",  "I love my iPhone so much", 220),
    new Tweet("dev",  "Android 15 beta is out!", 140),
    new Tweet("news", "Apple event highlights iPad and Mac", 160),
    new Tweet("user", "Nexus nostalgia thread", 20),
    new Tweet("user", "Random tweet no brand", 3)
  )

  lazy val allTweets: TweetSet = {
    seed.foldLeft(new Empty: TweetSet){ (s, t) => s.incl(t) }
  }
}
