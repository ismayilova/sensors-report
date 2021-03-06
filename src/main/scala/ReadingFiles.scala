import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import ClassMapper._
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.collection.{Set, _}

/**
 * @author KamilaI
 */
object ReadingFiles  {
 val config = ConfigFactory.load()

 val filePath = config.getString("file-path")

  case class Sensor(id:String ,min:Int,avg:Int,max:Int )
  case class Humidity(sensorId: String,humidityLevel:Option[Int])
  case class Response(filesCount:Int, failed:Int, allMeasurements:Int, list: List[Sensor])

  def readFiles(path:String) ={
    val files = new File(path).listFiles.filter(_.getName.endsWith(".csv")).toList

    val res=  files.flatMap(_.read()).map(line=>
      line.split(",").toList
    )
      .map(e=> Humidity(e.head,e.last.toOptionInt()))


    (files.size,res)
  }

  def computeSensorData(sensorId:String ,reports: List[Humidity]) ={
   val noneEmptyMeasurements = reports.filter(_.humidityLevel.isDefined)
   noneEmptyMeasurements match {
     case ::(head, next) =>
       val avg =  noneEmptyMeasurements.map(_.humidityLevel match {
       case Some(value) => value
       case None =>0
     }).sum/noneEmptyMeasurements.length

       val max =  noneEmptyMeasurements.map(_.humidityLevel
       match {
         case Some(value) =>value
         case None => 0
       }
       ).max


       val min =  noneEmptyMeasurements.map(_.humidityLevel
       match {
         case Some(value) =>value
         case None => 0
       }
       ).min
       Sensor(sensorId,min,avg,max)
     case Nil => Sensor(sensorId,0,0,0)
   }
 }

  def getMeasurements(lines:(Int, List[Humidity])) = {
    println(lines)
  val filesCount = lines._1
  val failed = lines._2.count(_.humidityLevel.isEmpty)
  val reportsCounts = lines._2.size

  val grouped = lines._2.groupBy(_.sensorId)

  val res  = for((k,v)<-grouped)yield { computeSensorData(k,v)}

   Response(filesCount,failed,reportsCounts,res.toList.sortBy(_.avg)(Ordering[Int].reverse))
}



  def main(args: Array[String]): Unit = {
    val lines: (Int, List[Humidity]) = readFiles(filePath)
    val result = getMeasurements(lines)
    println(result)
  }

}
