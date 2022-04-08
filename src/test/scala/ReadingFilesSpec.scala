import ReadingFiles.{Humidity, Response, Sensor}
import org.scalatest.funspec.AnyFunSpec

/**
 * @author KamilaI
 */
class ReadingFilesSpec extends AnyFunSpec {
  val readingFiles = ReadingFiles



    val fakeReports =(1,
      List(Humidity("s1",Some(2)),
        Humidity("s2",Some(6))))

    val fakeResponse  = Response(1,0,2,List(Sensor("s1",2,4,6)))


  describe("FileReader") {
    describe("when reports is sent") {

      it("correctly compute Response") {
        val response =  readingFiles.getMesaurments(fakeReports._1,fakeReports._2)
        assert(response.allMeasurments ==2)
      }
    }
  }

}



