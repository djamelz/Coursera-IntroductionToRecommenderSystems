package module4

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UserUserRecommenderSuite extends FunSuite {
  test("recommender") {
    val usersMovies = List((1024, 77), (1024, 268), (1024, 462), (1024, 393), (1024, 36955), (2048, 77), (2048, 36955), (2048, 788))

    val expected = List(
      (1024, 77, 4.3848, "Memento (2000)"),
      (1024, 268, 2.8646, "Batman (1989)"),
      (1024, 462, 3.1082, "Erin Brockovich (2000)"),
      (1024, 393, 3.8722, "Kill Bill: Vol. 2 (2004)"),
      (1024, 36955, 2.3524, "True Lies (1994)"),
      (2048, 77, 4.8493, "Memento (2000)"),
      (2048, 36955, 3.9698, "True Lies (1994)"),
      (2048, 788, 3.8509, "Mrs. Doubtfire (1993)"))

    val actual = UserUserRecommender.recommender(getClass().getResource("movie-titles.csv").getPath(),
      getClass().getResource("movie-titles.csv").getPath(),
      getClass().getResource("movie-titles.csv").getPath(),
      usersMovies)

    assert(expected === actual)

  }

  test("delivrable test") {
    val usersMovies = List((1024, 77), (1024, 268), (1024, 462), (1024, 393), (1024, 36955), (2048, 77), (2048, 36955), (2048, 788))
    UserUserRecommender.deliverable("test.txt", usersMovies)
  }

  test("delivrable part1") {
    val usersMovies = List(
      (1301, 36657),
      (1301, 954),
      (1301, 11),
      (1301, 120),
      (1301, 38),
      (93, 9331),
      (93, 194),
      (93, 3049),
      (93, 24),
      (93, 424),
      (3788, 38),
      (3788, 9331),
      (3788, 8587),
      (3788, 12),
      (3788, 585),
      (4997, 809),
      (4997, 5503),
      (4997, 414),
      (4997, 854),
      (4997, 1892),
      (648, 414),
      (648, 640),
      (648, 808),
      (648, 9806),
      (648, 857))
    UserUserRecommender.deliverable("part1.txt", usersMovies)
  }

}