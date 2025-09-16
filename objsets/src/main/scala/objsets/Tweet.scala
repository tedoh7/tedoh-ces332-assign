package objsets

class Tweet(val user: String, val text: String, val retweets: Int) {
  override def toString: String = s"User: $user\nText: $text [$retweets]"
}
