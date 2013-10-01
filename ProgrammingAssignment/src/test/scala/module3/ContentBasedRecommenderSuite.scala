package module3

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

@RunWith(classOf[JUnitRunner])
class ContentBasedRecommenderSuite extends FunSuite {

  test("createItemMatrix") {
    val tags = List((1, "asiatique"), (1, "asiatique"), (2, "grosse"), (5, "indienne"), (12, "blonde"), (10, "rousse"), (2, "rousse"), (5, "grande"))

    val expected = (DenseMatrix(
      (1d, 0d, 0d, 0d, 0d, 0d),
      (0d, 0.8690301050924811d, 0d, 0d, 0.49475921056909233d, 0d),
      (0d, 0d, 0.7071067811865476d, 0d, 0d, 0.7071067811865476d),
      (0d, 0d, 0d, 1d, 0d, 0d),
      (0d, 0d, 0d, 0d, 1d, 0d)),
      Map(5 -> 2, 10 -> 4, 1 -> 0, 2 -> 1, 12 -> 3))
    val actual = ContentBasedRecommender.createItemMatrix[Int, String](tags)

    assert(actual._1 === expected._1)
    assert(actual._2 === expected._2)

  }

  test("CreateUserRating") {

  }

  test("CreateWeightedUserRating") {
    val ratings = List((10, (1, 5d)), (10, (2, 2d)), (10, (3, 2d)), (2, (1, 5d)), (2, (2, 5d)), (2, (3, 4d)), (3, (1, 5d)), (3, (2, 5d)), (3, (3, 3d)))
    val items = Map(1 -> 0, 2 -> 1, 3 -> 2)
    val itemMatrix = DenseMatrix((0.3, 0.7, 0d, 0d, 0d, 0d), (0d, 0d, 1d, 0.1d, 0d, 0d), (0d, 0d, 0d, 0d, 0.002d, 0.99d))

    val actual = ContentBasedRecommender.createWeightedUserRating(10, ratings, items, itemMatrix)

    val expected = DenseVector[Double](0.6, 1.4, -1, -0.1, -0.002, -0.99)

    assert(actual === expected)

  }

  test("Score") {

  }

  test("recommender unweighted") {
    val expected = Map(4045 -> List((11, 0.3596), (63, 0.2612), (807, 0.2363), (187, 0.2059), (2164, 0.1899)),
      144 -> List((11, 0.3715), (585, 0.2512), (38, 0.1908), (141, 0.1861), (807, 0.1748)),
      3855 -> List((1892, 0.4303), (1894, 0.2958), (63, 0.2226), (2164, 0.2119), (604, 0.1941)),
      1637 -> List((2164, 0.2272), (141, 0.2225), (745, 0.2067), (601, 0.1995), (807, 0.1846)),
      2919 -> List((11, 0.3659), (1891, 0.3278), (640, 0.1958), (424, 0.1840), (180, 0.1527)))
    val actual = ContentBasedRecommender.recommend(getClass().getResource("ratings.csv").getPath(), getClass().getResource("movie-tags.csv").getPath(), List(4045, 144, 3855, 1637, 2919), false)

    assert(actual === expected)
  }

  test("recommender weighted") {
    val expected = Map(4045 -> List((807, 0.1932), (63, 0.1438), (187, 0.0947), (11, 0.0900), (641, 0.0471)),
      144 -> List((11, 0.1394), (585, 0.1229), (671, 0.1130), (672, 0.0878), (141, 0.0436)),
      3855 -> List((1892, 0.2243), (1894, 0.1465), (604, 0.1258), (462, 0.1050), (10020, 0.0898)),
      1637 -> List((393, 0.1976), (24, 0.1900), (2164, 0.1522), (601, 0.1334), (5503, 0.0992)),
      2919 -> List((180, 0.1454), (11, 0.1238), (1891, 0.1172), (424, 0.1074), (2501, 0.0973)))
    val actual = ContentBasedRecommender.recommend(getClass().getResource("ratings.csv").getPath(), getClass().getResource("movie-tags.csv").getPath(), List(4045, 144, 3855, 1637, 2919), true)

    assert(actual === expected)
  }
  
  test("Deliverable for unweightedRecommender") {
    ContentBasedRecommender.unweightedRecommenderDeliverable(
      getClass().getResource("ratings.csv").getPath(),
      getClass().getResource("movie-tags.csv").getPath(),
      List(1435, 4215, 3363, 2982, 975))
  }

  test("Deliverable for weightedRecommender") {
    ContentBasedRecommender.weightedRecommenderDeliverable(
      getClass().getResource("ratings.csv").getPath(),
      getClass().getResource("movie-tags.csv").getPath(),
      List(1435, 4215, 3363, 2982, 975))
  }

}