package recfun

import org.scalatest.funsuite.AnyFunSuite

class RecFunSuite extends AnyFunSuite {

  test("pascal: col=0,row=2 should be 1") {
    assert(Main.pascal(0, 2) === 1)
  }

  test("pascal: col=1,row=2 should be 2") {
    assert(Main.pascal(1, 2) === 2)
  }

  test("pascal: col=1,row=3 should be 3") {
    assert(Main.pascal(1, 3) === 3)
  }

  test("balance: (if (zero? x) max (/ 1 x)) is balanced") {
    assert(Main.balance("(if (zero? x) max (/ 1 x))".toList))
  }

  test("balance: :-) is unbalanced") {
    assert(!Main.balance(":-)".toList))
  }

  test("countChange: change for 4 with coins 1 and 2 should be 3") {
    assert(Main.countChange(4, List(1, 2)) === 3)
  }
}
