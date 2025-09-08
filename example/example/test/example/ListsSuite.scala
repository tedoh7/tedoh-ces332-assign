package example

import org.scalatest.funsuite.AnyFunSuite

class ListsSuite extends AnyFunSuite {

  test("sum of List(1,2,3) is 6") {
    assert(Lists.sum(List(1, 2, 3)) === 6)
  }

  test("max of List(1,3,2) is 3") {
    assert(Lists.max(List(1, 3, 2)) === 3)
  }

  test("sum of Nil is 0") {
    assert(Lists.sum(Nil) === 0)
  }

  test("max of single element list is that element") {
    assert(Lists.max(List(42)) === 42)
  }

  test("max of empty list throws NoSuchElementException") {
    intercept[NoSuchElementException] {
      Lists.max(Nil)
    }
  }
}
