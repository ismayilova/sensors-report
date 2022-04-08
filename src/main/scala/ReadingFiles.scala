import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import ClassMapper._
import ReadingFiles.lines
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.collection.{Set, _}

/**
 * @author KamilaI
 */
object ReadingFiles extends App {
 val config = ConfigFactory.load()

 val filePath = config.getString("file-path")

  case class Sensor(id:String ,min:Int,abg:Int,max:Int )
  case class Humidity(sensorId: String,humidityLevel:Option[Int])
  case class Response(filesCoun:Int,failed:Int,allMeasurments:Int,list: List[Sensor])

  def readFiles(path:String) ={
  val files = new File(path).listFiles.filter(_.getName.endsWith(".csv")).toList

 val res=  files.map(f =>f.read())
    .flatten.map(line=>
    line.split(",").toList
  )
    .map(e=> Humidity(e.head,e.last.toOptionInt()))


  (files.size,res)
}

  def computeSensorData(sensorId:String ,reports: List[Humidity]) ={
   val noneEmptyMesaurments = reports.filter(mes =>mes.humidityLevel!=None)
   noneEmptyMesaurments match {
     case ::(head, next) =>

       val avg =  noneEmptyMesaurments.map(_.humidityLevel match {
       case Some(value) => value
       case None =>0
     }).sum/noneEmptyMesaurments.length

       val max =  noneEmptyMesaurments.map(_.humidityLevel
       match {
         case Some(value) =>value
         case None => 0
       }
       ).max


       val min =  noneEmptyMesaurments.map(_.humidityLevel
       match {
         case Some(value) =>value
         case None => 0
       }
       ).min
       Sensor(sensorId,min,avg,max)
     case Nil => Sensor(sensorId,0,0,0)
   }
 }

  def getMesaurments(lines:(Int, List[Humidity])) = {
    println(lines)
  val filesCount = lines._1
  val failed = lines._2.filter(p=> p.humidityLevel==None).size
  val reportsCounts = lines._2.size

  val grouped = lines._2.groupBy(l=>l.sensorId)

  val res  = for((k,v)<-grouped)yield { computeSensorData(k,v)}

   Response(filesCount,failed,reportsCounts,res.toList)
}


  val lines: (Int, List[Humidity]) = readFiles(filePath)
  val result = getMesaurments(lines)
  println(result)


}
