import java.io.File

import scala.io.Source

/**
 * @author KamilaI
 */
object ClassMapper {
  implicit class FileHandler(file:File){
    def read() = Source.fromFile(file).getLines.drop(1)
  }

  implicit  class ToIntHandler(t: String){
    def toOptionInt() = try {Some(t.toInt)} catch {case e:Exception =>None}

  }


}
