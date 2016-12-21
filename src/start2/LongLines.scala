package start2

import scala.io.Source

object LongLines {
  def processFile(filename: String, width: Int) = {
    def processLine(line: String) = {
      if (line.length > width)
        println(filename + ": " + line)
    }
    val source = Source.fromFile(filename)
    for (line <- source.getLines())
       processLine(line)
  }
}

import java.io.File

object FindLongLines {
  def main(args: Array[String]) {
    // println(new File("").getAbsolutePath)
    LongLines.processFile(""".\src\start2\LongLines.scala""", 40)

  }
}
