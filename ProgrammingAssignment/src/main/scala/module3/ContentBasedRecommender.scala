package module3

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import scala.reflect.ClassTag
import scala.annotation.tailrec
import scala.io.Source
import java.io.PrintWriter
import java.io.FileWriter

object ContentBasedRecommender {
  
  def recommend(ratingsFilePath : String, contentFilePath : String, userIds : List[Int], weighted : Boolean) : Map[Int, List[(Int, Double)]] ={
    val contentByItem = Source.fromFile(contentFilePath).getLines.map(x => x.split(",")).map(x => (x(0).toInt, x(1))).toList
    val x = createItemMatrix[Int, String](contentByItem)
    val itemMatrix = x._1
    val items = x._2
    val itemsInv = items.map(_.swap)
    
    val ratings = Source.fromFile(ratingsFilePath).getLines.toList.view.map(x => x.split(",")).map(y => (y(0).toInt, (y(1).toInt, y(2).toDouble))).toList
    
    var result = Map[Int, List[(Int, Double)]]()
    
    userIds.foreach({userId => 
    val ratedItems = ratings.par.filter(z => z._1 == userId).map(_._2._1).toSet
    val domainToScore = items.filterNot(z => ratedItems.contains(z._1)).map(x => x._2).toList
    
    val userRatings = if (weighted) createWeightedUserRating(userId, ratings, items, itemMatrix) else createUnweightedUserRating(userId, ratings, items, itemMatrix)
    
    val score = scoreItems(userRatings, itemMatrix, domainToScore)
    result += userId -> score.sortBy(x => -1 * x._2).take(5).map(x => (itemsInv(x._1), BigDecimal(x._2).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble))
    })
    
    result
  }
  
  def createItemMatrix[I: ClassTag, C: ClassTag](contentByItem: List[(I, C)]): (DenseMatrix[Double], Map[I, Int]) = {
    @tailrec	  
    def getItemsAndContents(contentByItem: List[(I, C)], items: Map[I, Int] = Map(), contents: Map[C, Int] = Map()): (Map[I, Int], Map[C, Int]) = contentByItem match {
      case Nil => (items, contents)
      case head :: tail if (items.contains(head._1) && contents.contains(head._2)) => getItemsAndContents(tail, items, contents)
      case head :: tail if (!items.contains(head._1) && contents.contains(head._2)) => getItemsAndContents(tail, items + (head._1 -> (items.size)), contents)
      case head :: tail if (items.contains(head._1) && !contents.contains(head._2)) => getItemsAndContents(tail, items, contents + (head._2 -> (contents.size)))
      case head :: tail if (!items.contains(head._1) && !contents.contains(head._2)) => getItemsAndContents(tail, items + (head._1 -> (items.size)), contents + (head._2 -> (contents.size)))
    }
    val lists = getItemsAndContents(contentByItem)
    val items = lists._1
    val contents = lists._2
    val itemsCount = items.size


    val itemMatrix = DenseMatrix.zeros[Double](itemsCount, contents.size)
    val groupbyItemContent = contentByItem.groupBy(x => (x._1, x._2)).map(x => (x._1, x._2.size))
    val groupbyContent = contentByItem.groupBy(x =>  x._2).map(x => (x._1, x._2.distinct.size))
    groupbyItemContent.keySet.foreach(x =>
    itemMatrix(items(x._1), contents(x._2)) = groupbyItemContent(x) * math.log((itemsCount/groupbyContent(x._2).toDouble)))
    
    def unitVector(v : DenseVector[Double]) : DenseVector[Double] = v :* (1/v.norm(2))
    
    
    (0 until itemMatrix.rows).foreach(x => itemMatrix(x, ::) := unitVector(itemMatrix(x, ::).toDenseVector).asDenseMatrix)
    

    (itemMatrix, items)
  }
  
  def createUnweightedUserRating(userId : Int, ratings : List[(Int, (Int, Double))], items : Map[Int, Int], itemMatrix : DenseMatrix[Double] ) : DenseVector[Double] = {
    val ratedItems = ratings.par.filter(z => z._1 == userId && z._2._2 >= 3.5).map(_._2._1).toArray  
    val userVector = DenseVector.zeros[Double](itemMatrix.cols)	
    
    ratedItems.foreach(x => userVector := itemMatrix(items(x), ::).toDenseVector + userVector)
    
    userVector
  }
  def createWeightedUserRating(userId : Int, ratings : List[(Int, (Int, Double))], items : Map[Int, Int], itemMatrix : DenseMatrix[Double] ) : DenseVector[Double] = {
    val ratedItems = ratings.filter(z => z._1 == userId).map(x => (x._2._1, x._2._2))
    val averageRate = ratedItems.map(x => x._2).sum / ratedItems.size
    
    val userVector = DenseVector.zeros[Double](itemMatrix.cols)
    
    ratedItems.foreach(x => userVector := userVector + (itemMatrix(items(x._1), ::).toDenseVector * (x._2 - averageRate)))

    userVector
  }
  
  @tailrec
  def scoreItems(userRatings : DenseVector[Double], itemMatrix : DenseMatrix[Double], domainToScore : List[Int], score : List[(Int, Double)]=List()) : List[(Int, Double)] =  domainToScore match{
    case Nil => score
    case head :: tail =>
      val cos = (itemMatrix(head, ::) * userRatings) / (itemMatrix(head, ::).toDenseVector.norm(2) * userRatings.norm(2))
      scoreItems(userRatings, itemMatrix, tail, (head, cos(0)) :: score)
  }
  
  def unweightedRecommenderDeliverable(ratingsFilePath : String, contentFilePath : String, userIds : List[Int]) =
  {
    writeFile("unweighted.txt", userIds, recommend(ratingsFilePath, contentFilePath, userIds, false))
    
  }
  
  def weightedRecommenderDeliverable(ratingsFilePath : String, contentFilePath : String, userIds : List[Int]) =
  {
    writeFile("weighted.txt", userIds, recommend(ratingsFilePath, contentFilePath, userIds, true))
  }
  
  private def writeFile(fileName : String, userIds : List[Int], result : Map[Int, List[(Int, Double)]]){
    val writer = new PrintWriter(new FileWriter("/projects/Coursera-IntroductionToRecommenderSystems/Module3/ProgrammingAssignment2/" + fileName))
    
    userIds.foreach({userId =>
      writer.println("recommendations for user "+ userId +":")
      result(userId).foreach({y =>
        writer.println("  " + y._1 + ": " + y._2)
      })
    })
    writer.close()
  }
  
}