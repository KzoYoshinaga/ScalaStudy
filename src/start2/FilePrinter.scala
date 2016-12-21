package start2

object FilePrinter {
   def main(args: Array[String]) {
    withPipe(args(0))
  }

  def withPipe(filename: String) {
    import scala.io.Source

    // ファイルの各行を一旦メモリに確保
    val lines = Source.fromFile(filename).getLines().toList

    // 行の文字列の長さを表す数字を文字列に変換した長さ
    def widthOfLength(s: String) = s.length.toString.length

    // 上記の最大値
    val maxWidth = lines.map(s => widthOfLength(s)).max

    // パッディングを決める
    def padding(s: String): String = " " * (maxWidth - widthOfLength(s))

    // 表示用文字列作る
    def mkString(s: String): String = padding(s) + s.length + "|" + s

    // 副作用がまとまった小さなコード
    lines.foreach(l => println(mkString(l)))
  }

  def withLineLength(filename: String) {
    import scala.io.Source

    if (filename.length > 0) {

      for (line <- Source.fromFile(filename).getLines())
          println(line.length + " " + line)
    }
    else
      Console.err.println("Plese enter filename")
  }
}