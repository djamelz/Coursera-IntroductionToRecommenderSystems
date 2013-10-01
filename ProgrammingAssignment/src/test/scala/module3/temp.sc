package module2

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import scala.reflect.ClassTag
import scala.math

object temp {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  val tags = List((1, "asiatique"), (1, "asiatique"), (2, "grosse"), (5, "indienne"), (12, "blonde"), (10, "rousse"), (2, "rousse"), (5, "grande"))
                                                  //> tags  : List[(Int, String)] = List((1,asiatique), (1,asiatique), (2,grosse),
                                                  //|  (5,indienne), (12,blonde), (10,rousse), (2,rousse), (5,grande))
  
  val t = tags.groupBy(x => x._2).map(x => (x._1, x._2.distinct.size))
                                                  //> t  : scala.collection.immutable.Map[String,Int] = Map(grosse -> 1, indienne 
                                                  //| -> 1, asiatique -> 1, grande -> 1, blonde -> 1, rousse -> 2)
  
  val tt = tags.groupBy(x => x._1).map(x => (x._1, x._2.distinct))
                                                  //> tt  : scala.collection.immutable.Map[Int,List[(Int, String)]] = Map(5 -> Lis
                                                  //| t((5,indienne), (5,grande)), 10 -> List((10,rousse)), 1 -> List((1,asiatique
                                                  //| )), 2 -> List((2,grosse), (2,rousse)), 12 -> List((12,blonde)))
  val groupby = tags.groupBy(x => (x._1, x._2)).map(x => (x._1, x._2.size))
                                                  //> groupby  : scala.collection.immutable.Map[(Int, String),Int] = Map((2,rousse
                                                  //| ) -> 1, (1,asiatique) -> 2, (2,grosse) -> 1, (5,grande) -> 1, (10,rousse) ->
                                                  //|  1, (12,blonde) -> 1, (5,indienne) -> 1)
  
  val v = DenseVector[Double](3,2,1,4,5,6)        //> v  : breeze.linalg.DenseVector[Double] = DenseVector(3.0, 2.0, 1.0, 4.0, 5.0
                                                  //| , 6.0)
  val xxx = DenseMatrix((10d,10d,10d), (22d,22d,10d))
                                                  //> xxx  : breeze.linalg.DenseMatrix[Double] = 10.0  10.0  10.0  
                                                  //| 22.0  22.0  10.0  
  
  val www = DenseVector.zeros[Double](xxx.cols)   //> www  : breeze.linalg.DenseVector[Double] = DenseVector(0.0, 0.0, 0.0)
  
  (0 until xxx.rows).foreach(x => www := xxx(x, ::).toDenseVector + www)
  
  www                                             //> res0: breeze.linalg.DenseVector[Double] = DenseVector(32.0, 32.0, 20.0)
  
  val n = 1/v.norm(2)                             //> n  : Double = 0.10482848367219183
  val norm = v :* n                               //> norm  : breeze.linalg.DenseVector[Double] = DenseVector(0.3144854510165755, 
                                                  //| 0.20965696734438366, 0.10482848367219183, 0.4193139346887673, 0.524142418360
                                                  //| 9591, 0.628970902033151)
  

  def createItemVectors[I: ClassTag, C: ClassTag](contentByItem: List[(I, C)]): (DenseMatrix[Double], (Array[I], Array[C])) = {

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
    itemMatrix(items(x._1), contents(x._2)) =
    groupbyItemContent(x) *
    math.log((itemsCount/groupbyContent(x._2)).toDouble))
    
    def unitVector(v : DenseVector[Double]) : DenseVector[Double] = v :* (1/v.norm(2))
    
    
    (0 until itemMatrix.rows).foreach(x => itemMatrix(x, ::) := unitVector(itemMatrix(x, ::).toDenseVector).asDenseMatrix)
    

    (itemMatrix, (items.toArray.sortBy(x => x._2).map(x => x._1).toArray, contents.toArray.sortBy(x => x._2).map(x => x._1).toArray))
  }                                               //> createItemVectors: [I, C](contentByItem: List[(I, C)])(implicit evidence$3:
                                                  //|  scala.reflect.ClassTag[I], implicit evidence$4: scala.reflect.ClassTag[C])
                                                  //| (breeze.linalg.DenseMatrix[Double], (Array[I], Array[C]))
  
  createItemVectors[Int, String](tags)            //> res1: (breeze.linalg.DenseMatrix[Double], (Array[Int], Array[String])) = (1
                                                  //| .0  0.0                 0.0                 0.0  0.0                 0.0   
                                                  //|               
                                                  //| 0.0  0.9184435390665682  0.0                 0.0  0.3955521021899355  0.0  
                                                  //|                
                                                  //| 0.0  0.0                 0.7071067811865476  0.0  0.0                 0.707
                                                  //| 1067811865476  
                                                  //| 0.0  0.0                 0.0                 1.0  0.0                 0.0  
                                                  //|                
                                                  //| 0.0  0.0                 0.0                 0.0  1.0                 0.0  
                                                  //|                ,(Array(1, 2, 5, 12, 10),Array(asiatique, grosse, indienne, 
                                                  //| blonde, rousse, grande)))

  val userRatings = List("1,601,5.0","1,238,5.0", "1,664,1.5", "1,3049,3.0", "2,121,4.5", "1,85,4.5", "2,4327,2.5", "1,329,5.0", "2,13,4.0", "1,63,3")
                                                  //> userRatings  : List[String] = List(1,601,5.0, 1,238,5.0, 1,664,1.5, 1,3049,
                                                  //| 3.0, 2,121,4.5, 1,85,4.5, 2,4327,2.5, 1,329,5.0, 2,13,4.0, 1,63,3)
  val ratedItems = userRatings.view.map(x => x.split(",")).map(y => (y(0).toInt, (y(1).toInt, y(2).toDouble))).filter(z => z._1 == 1 && z._2._2 >= 3.5).map(_._2._1).toArray
                                                  //> ratedItems  : Array[Int] = Array(601, 238, 85, 329)

}