import java.io.File

import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.ConfigFactory
import ClassMapper._
import akka.{Done, NotUsed}
import akka.actor.ActorSystem

import scala.concurrent.Future
/**
 * @author KamilaI
 */
object StreamFileReader extends App {
implicit val system = ActorSystem("sys")
  val config = ConfigFactory.load()

  val filePath = config.getString("file-path")
  case class Sensor(id:String ,min:Int,avg:Int,max:Int )
  case class Humidity(sensorId: String,humidityLevel:Option[Int])
  case class Response(filesCount:Int, failed:Int, allMeasurements:Int, list: List[Sensor])

  def computeSensorData(sensorId:String ,reports: List[Humidity]): Sensor ={
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
  val files = new File(filePath).listFiles.filter(_.getName.endsWith(".csv")).toList
  val source: Source[(String, List[Humidity]), NotUsed] = Source(files
      .flatMap(_.read())
      .map(_.split(",").toList)
      .map(e=>Humidity(e.head,e.last.toOptionInt()))
      .groupBy(_.sensorId)
  )

  val sink: Sink[Any, Future[Done]] = Sink.foreach(println)
  val flow: Flow[(String, List[Humidity]), Sensor, NotUsed] = Flow[(String, List[Humidity])].map(e=> computeSensorData(e._1,e._2))


  source.via(flow).to(sink).run()

}
