package recfun

object Main {

  /** Exercise 1: Pascal’s Triangle */
  def pascal(c: Int, r: Int): Int =
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)

  /** Exercise 2: Parentheses Balancing */
  def balance(chars: List[Char]): Boolean = {
    @annotation.tailrec
    def loop(rest: List[Char], open: Int): Boolean =
      if (open < 0) false
      else rest match {
        case Nil        => open == 0
        case '(' :: t   => loop(t, open + 1)
        case ')' :: t   => loop(t, open - 1)
        case _   :: t   => loop(t, open)
      }
    loop(chars, 0)
  }

  /** Exercise 3: Counting Change */
  def countChange(money: Int, coins: List[Int]): Int =
    if (money == 0) 1
    else if (money < 0 || coins.isEmpty) 0
    else countChange(money - coins.head, coins) + countChange(money, coins.tail)
}
