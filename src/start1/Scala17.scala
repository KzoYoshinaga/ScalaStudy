package start1

class Scala17 {
  /** 3.6 その他のループ構文
   */
  // 3.6.1 Scalaのwhileループ
  def whileSample {
    import java.util.Calendar
    def isFridayThirteen(cal:Calendar):Boolean = {
      val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
      val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
      (dayOfWeek == Calendar.FRIDAY) && (dayOfMonth == 13)
    }
    while (isFridayThirteen(Calendar.getInstance()) == false) {
      println("Today isn't Friday the 13th. Lame.")
      // 1日分スリープする
      Thread.sleep(86400000)
    }
  }

  /* 3.6.2 Scalaのdo-whileループ
   */
  def doWhileSample {
    var count = 0
    do {
      count += 1
      println(count)
    } while (count < 10)
    // 1 から 10 まで表示される(後判定)
  }

  /* 3.6.3 ジェネレータ式
   *
   * for式の中での( <- )左アロー演算子
   */
  def foGeneSample {
    for (i <- 1 to 10) println(i)  // ループ用変数は暗黙的にval 再代入不可
    // Java8: Stream.iterate(i -> {return i + 1;}).limit(10).forEach(System.out::println);
    for (i <- 1 until 10) println(i) // 9までカウントする
  }
  /* Int型の 1 を暗黙的にRichInt型に変換
   *
   * final class RichInt extends AnyVal with ScalaNumberProxy[Int] with RangedProxy[Int]
   *
   * RichInt.to(end:Int): Inclusive
   * RichInt.until(end: Int): collection.immutable.Range
   *
   * class Inclusive extends Range
   * InclusiveはRangeコンパニオンオブジェクトの入れ子のクラス
   *
   * scala.collection.immutable.Range
   * Rangeクラスのサブクラスは、forループ内で使用するのに必要なものも含めて
   * シーケンスや反復可能なデータ構造を扱う多くのメソッドを継承している
   */
   // break文やcontinue文は用意されていない
   // しかしライブラリのメソッドとしてbreakをサポートしている

}