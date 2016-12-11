package start1

class Scala9 {
  /* 2.7 タプル
   *
   * タプル: ２つ以上の項目をもつグループ
   *
   * （x1, x2 , x3 ...）といった独立したxnの組をまとめる scala.Tuple1 から scala.Tuple22 までのクラスが用意されている
   * タプルは不変でファーストクラスの値なので
   * ・変数に代入
   * ・関数に値として渡す
   * ・関数の戻り値となる
   */
  def tupleator(x1: Any, x2: Any, x3: Any) = (x1, x2, x3)
  val t = tupleator("Hello", 1, 2.3)
  /*
   *  println("Print the whole tuple : " + t)
   *  println("Print the first item  : " + t._1) // タプルの値には 1 からアクセスする
   *  println("Print the second item : " + t._2)
   *  println("Print the third item  : " + t._3)
   *
   *  // Print the whole tuple : (Hello,1,2.3)
   *  // Print the first item  : Hello
   *  // Print the second item : 1
   *  // Print the third item  : 2.3
   *
   */
   val (t1, t2, t3)  = tupleator("World", '!', 0x22)
   /* println(t1 + " " + t2 + " " + t3)
    *
    * // World ! 34
    */
    val t4 = 1 -> 2        // アロー演算子を使った生成
    val t5 = Tuple2(1, 2)  // ファクトリメソッド
    val t6 = Pair(1, 2)    // ファクトリメソッド
    val t7 = 1 -> 2 -> 3   // ((Int, Int), Int) = ((1, 2), 3)
    val t8 = 1 -> 2 -> 3 -> 4  // (((Int, Int), Int), Int) = (((1, 2), 3), 4)
}