package funsets

import org.scalatest.funsuite.AnyFunSuite
import FunSets._

class FunSetSuite extends AnyFunSuite {

  test("singletonSet contains its element") {
    val s = singletonSet(1)
    assert(contains(s, 1))
    assert(!contains(s, 2))
  }

  test("union contains all elements of each set") {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val u = union(s1, s2)
    assert(contains(u, 1))
    assert(contains(u, 2))
    assert(!contains(u, 3))
  }

  test("intersect keeps only common elements") {
    val a = union(singletonSet(1), singletonSet(2)) // {1,2}
    val b = union(singletonSet(2), singletonSet(3)) // {2,3}
    val i = intersect(a, b) // {2}
    assert(contains(i, 2))
    assert(!contains(i, 1))
    assert(!contains(i, 3))
  }

  test("diff removes elements of second set") {
    val a = union(singletonSet(1), singletonSet(2)) // {1,2}
    val b = singletonSet(2) // {2}
    val d = diff(a, b) // {1}
    assert(contains(d, 1))
    assert(!contains(d, 2))
  }

  test("filter keeps elems satisfying predicate") {
    val s = union(union(singletonSet(1), singletonSet(2)), singletonSet(3)) // {1,2,3}
    val f = filter(s, _ % 2 == 1) // odd numbers
    assert(contains(f, 1))
    assert(contains(f, 3))
    assert(!contains(f, 2))
  }

  test("forall and exists work as expected") {
    val s = union(union(singletonSet(-1), singletonSet(0)), singletonSet(2)) // {-1,0,2}
    assert(forall(s, _ <= 2))
    assert(!forall(s, _ >= 0))
    assert(exists(s, _ == -1))
    assert(!exists(s, _ == 3))
  }

  test("map applies function to all elements") {
    val s = union(singletonSet(1), singletonSet(2)) // {1,2}
    val m = map(s, x => x * x) // {1,4}
    assert(contains(m, 1))
    assert(contains(m, 4))
    assert(!contains(m, 2))
  }
}
