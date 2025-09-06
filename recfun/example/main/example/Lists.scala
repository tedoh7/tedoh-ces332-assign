package example

object Lists {

  /** Returns the sum of all elements in a list of integers. */
  def sum(xs: List[Int]): Int = xs match {
    case Nil       => 0
    case h :: tail => h + sum(tail)
  }

  /** Returns the maximum element in a list of integers.
    * Throws NoSuchElementException if the list is empty.
    */
  def max(xs: List[Int]): Int = xs match {
    case Nil       => throw new NoSuchElementException("max of empty list")
    case h :: Nil  => h
    case h :: tail =>
      val m = max(tail)
      if (h > m) h else m
  }
}
