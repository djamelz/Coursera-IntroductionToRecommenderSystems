package module1

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NonPersonnalizedRecommenderSuite extends FunSuite {
  test("simple with movie 11") {
    val actual = NonPersonnalizedRecommender.recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        11, true)
    assert(actual === Array((603, 0.96), (1891, 0.94), (1892, 0.94), (120, 0.93), (1894, 0.93)))    
  }
  
  test("simple with movie 121") {
    val actual = NonPersonnalizedRecommender.recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        121, true)
    assert(actual === Array((120,0.95),(122,0.95),(603,0.94),(597,0.89),(604,0.88)))    
  }
  
  test("simple with movie 8587") {
    val actual = NonPersonnalizedRecommender.recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        8587, true)
    assert(actual === Array((603,0.92),(597,0.90),(607,0.87),(13,0.86),(120,0.86)))    
  }
  
  test("Deliverable1")
  {
    NonPersonnalizedRecommender.simpleRecommenderDeliverable
  }
  
  test("advanced with movie 11") {
    val actual = NonPersonnalizedRecommender.recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        11, false)
    assert(actual === Array((1891,5.69),(1892,5.65),(243,5.00),(1894,4.72),(2164,4.11)))    
  }
  
  test("advanced with movie 121") {
    val actual = NonPersonnalizedRecommender.recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        121, false)
    assert(actual === Array((122,4.74),(120,3.82),(2164,3.40),(243,3.26),(1894,3.22)))    
  }
  
  test("advanced with movie 8587") {
    val actual = NonPersonnalizedRecommender.recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        8587, false)
    assert(actual === Array((10020,4.18),(812,4.03),(7443,2.63),(9331,2.46),(786,2.39)))    
  }
  
  test("Deliverable2")
  {
    NonPersonnalizedRecommender.advancedRecommenderDeliverable
  }

}