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
        r.map(x => (invMovies(x._2), (x._1/BigDecimal(rating.sum)))).sortBy(x => -1 * x._2).take(5).map(x => (x._1, x._2.setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble))
      else
      {
        val nonRating = rating.map(x => if(x==1) 0 else 1)
        val nonR = (nonRating.asDenseMatrix * ratings).toDenseVector.toArray
        val total = r.map(x => (invMovies(x._2), ((x._1/BigDecimal(rating.sum))/(nonR(x._2)/BigDecimal(nonRating.sum))))).sortBy(x => -1 * x._2).map(x => (x._1, x._2.setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble))
        val ttt= total.take(5)
        //r.map(x => (invMovies(x._2), ((x._1/BigDecimal(rating.sum))/(nonR(x._2)/BigDecimal(nonRating.sum))).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)).sortBy(x => -1 * x._2).take(5)
        r.map(x => (invMovies(x._2), ((x._1/BigDecimal(rating.sum))/(nonR(x._2)/BigDecimal(nonRating.sum))))).sortBy(x => -1 * x._2).take(5).map(x => (x._1, x._2.setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble))
      }
      
  }
  
  def simpleRecommenderDeliverable(movie1 : Int, movie2 : Int, movie3 : Int){
    recommenderDeliverable("simple_delivrable.txt", true, movie1, movie2, movie3)
  }
  
  def advancedRecommenderDeliverable(movie1 : Int, movie2 : Int, movie3 : Int){
    recommenderDeliverable("advanced_delivrable.txt", false, movie1, movie2, movie3)
  }
  
  def recommenderDeliverable(fileName :String, simple : Boolean, movie1 : Int, movie2 : Int, movie3 : Int ){
    val writer = new PrintWriter(new FileWriter("/projects/Coursera-IntroductionToRecommenderSystems/Module2/ProgrammingAssignment1/"+fileName))
    val t =format(movie1, recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        movie1, simple))
    writer.println(t)
    writer.println(format(movie2, recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        movie2, simple)))
        
    writer.println(format(movie3, recommender(
        getClass().getResource("/module1/recsys-data-movie-titles.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-users.csv").getPath(), 
        getClass().getResource("/module1/recsys-data-ratings.csv").getPath(), 
        movie3, simple)))
        
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