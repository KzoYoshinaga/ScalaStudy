package start2

// スタンドアロンシングルトンオブジェクト
object Singleton {
  // シングルトンオブジェクト内のmainエントリポイント
  def main(args: Array[String]) {
    val css = new ChecksumAccumulator
    var sca = new ChecksumAccumulator

  }
}

class ChecksumAccumulator {
  // Scalaのデフォルトアクセスレベルはpublic
  private var sum = 0
  // メソッド引数はデフォルトでval
  // パブリックメソッドのパラメータ、結果値の型を明示するのは適切
  // 結果値がUnitのメソッドは副作用(メソッド外の状態の変更、入出力)
  // を発生させるために作られる
  // 副作用だけを目的として実行されるメソッドを、手続き(procedure)
  def add(b: Byte) : Unit = sum += b
  def checksum() : Int = ~(sum & 0xFF) + 1
}

// ChecksumAccumulatorクラスのコンパニオンオブジェクト
// クラスとコンパニオンオブジェクトは同じソースファイルで定義する
// クラスとコンパニオンオブジェクトはお互いのprivateなメンバにアクセスできる
// シングルトンオブジェクトはパラメータを取れない
// 何らかのコードから亜アクセスされたときに初期化される(自動生成クラス)
object  ChecksumAccumulator {
  import scala.collection.mutable
  // 計算してきたチェックサムをキャッシュしておく
  // チェックサム計算部分のようにのコストが高い計算値の場合に有効
  private val cache = mutable.Map.empty[String, Int]
  def calculate(s: String): Int =
    if (cache.contains(s))
      cache(s)
    else {
      // new キーワードはクラスを指すので衝突しない
      val acc = new ChecksumAccumulator
      for (c <- s)
        acc.add(c.toByte)
      val cs = acc.checksum()
      cache += (s -> cs)
      cs
    }
}
