package start2

object Main {
  def main(args: Array[String]) {
    // 任意のオブジェクトからメンバをインポートできる
    import ChecksumAccumulator.calculate
    Array("dafsdf","fasdf","fadf","f67adf").foreach(s => println(s + " : " + calculate(s)))
  }
}


