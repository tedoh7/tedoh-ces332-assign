package objsets

object GoogleVsApple {
  val google = List("android", "galaxy", "nexus", "pixel", "google")
  val apple  = List("ios", "iphone", "ipad", "mac", "apple")

  lazy val googleTweets: TweetSet =
    TweetReader.allTweets.filter(t =>
      google.exists(k => t.text.toLowerCase.contains(k.toLowerCase))
    )

  lazy val appleTweets: TweetSet =
    TweetReader.allTweets.filter(t =>
      apple.exists(k => t.text.toLowerCase.contains(k.toLowerCase))
    )

  lazy val trending: TweetList =
    (googleTweets union appleTweets).descendingByRetweet
}
