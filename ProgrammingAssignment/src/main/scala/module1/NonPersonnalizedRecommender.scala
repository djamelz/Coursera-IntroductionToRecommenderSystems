package module1

import scala.io.Source
import breeze.linalg.DenseMatrix
import java.io.PrintWriter
import java.io.FileWriter

object NonPersonnalizedRecommender {
  
  def recommender(moviesPath : String, usersPath : String, ratingsPath : String, movie : Int, simple : Boolean) : Array[(Int, Double)] ={
    val movies = Source.fromFile(moviesPath).getLines().map(_.split(",")).map(x => x(0).toInt).zipWithIndex.toMap
    val users = Source.fromFile(usersPath).getLines().map(_.split(",")).map(x => x(0).toInt).zipWithIndex.toMap
    
    val ratings = DenseMatrix.zeros[Int](users.size, movies.size)
    
    Source.fromFile(ratingsPath).getLines().foreach({x =>
      val t = x.split(",")
      ratings(users(t(0).toInt), movies(t(1).toInt)) = 1
      })
      
      val invMovies = movies.map(_.swap)
      
      val rating = ratings(::, movies(movie))
      val r = (rating.asDenseMatrix * ratings).toDenseVector.toArray.zipWithIndex.filterNot(x => x._2 == movies(movie))
      
      if (simple)
        r.map(x => (invMovies(x._2), (x._1/BigDecimal(rating.sum)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)).sortBy(x => -1 * x._2).take(5)
      else
      {
        val nonRating = rating.map(x => if(x==1) 0 else 1)
        val nonR = (nonRating.asDenseMatrix * ratings).toDenseVector.toArray
        r.map(x => (invMovies(x._2), ((x._1/BigDecimal(rating.sum))/(nonR(x._2)/BigDecimal(nonRating.sum))).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)).sortBy(x => -1 * x._2).take(5)
      }
      
  }
  
  def simpleRecommenderDeliverable(){
    recommenderDeliverable("simple_delivrable.txt", true)
  }
  
  def advancedRecommenderDeliverable(){
    recommenderDeliverable("advanced_delivrable.txt", false)
  }
  
  def recommenderDeliverable(fileName :String, simple : Boolean ){
    val writer = new PrintWriter(new FileWriter("/projects/Coursera-IntroductionToRecommenderSystems/Module2/ProgrammingAssignment1/"+fileName))
    val t =format(280, recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        280, simple))
    writer.println(t)
    writer.println(format(585, recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        585, simple)))
        
    writer.println(format(680, recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        680, simple)))
        
    writer.close()
  }
  
  def format(id : Int, data : Array[(Int, Double)]) : String = {
    val sb = new StringBuilder()
    sb.append(id)
    sb.append(",")
    data.foreach({x => 
      sb.append(x._1)
      sb.append(",")
      sb.append(x._2)
      sb.append(",")})
    
    sb.deleteCharAt(sb.size -1).toString
  }

}