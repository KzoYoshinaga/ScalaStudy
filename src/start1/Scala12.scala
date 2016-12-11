package start1

class Scala12 {
  /* 2.10 型やメンバのインポート
   *
   * import java.awt._        // (_) をワイルドカードとして派ケージ内の全ての型をインポート
   * import java.io.File      // 型をインポート
   * import java.io.File._    // File型の全てのスタティックメソッドとスタティックフィールドをインポート
   * import java.util.{Map, HashMap} // 関心のある型をまとめてインポート
   *
   */

  /* より高度なimport
   *
   * java.math.BigIntegerには定数値が３つ定義されている
   * ONE
   * TEN
   * ZERO
   */
  def writeAboutBigInteger() = {
    import java.math.BigInteger.{      // importのスコープを制御できる
      ONE => _,         // ONEの名前を(_)ワイルドカードに変更
      TEN,              // TENはそのままインポート
      ZERO => JAVAZERO} // imiportした項目に別名を付けることができる
                        // このスコープで記述されない定数はインポートされない

    // ONEは事実上、未定義
    // println("ONE: " + ONE)
    println("TEN:" + TEN)
    println("ZERO: " + JAVAZERO)
  }
  /* writeAboutBigInteger()
   * // TEN: 10
   * // ZERO: 0
   */

  /* 2.10.1 インポートは相対的
   */

  import scala.collection.mutable._
  import collection.mutable._  // scalaは既にインポートされているため
  import _root_.scala.collection._ // 実際のルートからのフルパス
  /* package akka.actor {          // akka.actorのスコープ内にいる
   *   import ActorSystem          // はずだが上手くいかない
   * }
   */

  /* 2.11 抽象型とパラメータ化された型
   * List[+A] : 任意の B が A のサブタイプであるなら List[B] は List[A] のサブタイプである
   * List[-A] : 任意の B が A のスーパタイプであるなら List[B] は List[A] のスーパタイプである
   */
  import java.io._

  abstract class BulkReader {
    type In
    val source: In
    def read: String
  }

  class StringBulkReader(val source: String) extends BulkReader {
    type In = String
    def read = source
  }

  class FileBulkReader(val source: File) extends BulkReader {
    type In = File
    def read = {
      val in = new BufferedInputStream(new FileInputStream(source))
      val numBytes = in.available()
      val bytes = new Array[Byte](numBytes)
      in.read(bytes, 0, numBytes)
      new String(bytes)
    }
  }
  /* 出力結果
   * println(new StringBulkReader("Hello Scala!").read)
   * // Hello Scala!
   *
   * println(new FileBulkReader(new File("""D:\workspase-scala\ScalaStudy\test.txt""")).read)
   * // asdvnakjnakjnva]
   * // asdvnasoivna
   * // sva
   * // psovaskvms
   * // dva
   * // sd vm
   * // aosdvmas
   * // dvmoa
   *
   * 以下同義
   */
  abstract class BulkReader2[In] {
    val source: In
    // ...
  }
  class StringBulkReader2(val source: String) extends BulkReader2[String] { /*... */ }
  class FileBulkReader2(val source: File) extends BulkReader2[File] { /* ... */ }

  /* 2.12 予約語(キーワード)
   *
-> * abstract  : 宣言を抽象にする、抽象メンバに対しては必要ない
-> * case      : match式の中でcase節を開始する
   * catch     : スローされた例外を補足するための節を開始する
   * class     : クラス宣言を開始する
-> * def       : メソッド宣言を開始する
   * do        : do...whileループを開始する
   * else      : if節に対するelse節を開始する
   * extends   : クラスやトレイトが、宣言しているクラスやトレイトの親の型であることを示す
   * fales     : Boolean型の偽
   * final     : クラスやトレイトに対しては、子供の型を派生することを禁止
   *             メンバに対して使った場合は、派生したクラスやトレイトでのオーバーライド禁止
   * finaly    : try節で例外がスローされるか同化に関係なく、対応するtry節の後に実行される節を開始する
   * for       : for内包表記(ループ)を開始する
-> * forSome   : 使用可能な具象型を制限するために存在型の宣言で使用する
   * if        : if節を開始する
-> * implicit  : メソッドに付与すると、暗黙的な型の変換として使うことができる
   *             メソッドパパラメータに付与するとメソッドが呼ばれるスコープ内に存在し、
   *             型互換性があり置換可能なオブジェクトであれば、省略可能である
   * import    : １個以上の型や型のメンバを現在のスコープにインポートする
-> * lazy      : val変数の評価を遅らせる
-> * match     : match節を開始する
   * new       : クラスの新しいインスタンスを生成する
   * null      : 値が代入されていない参照変数の値
-> * object    : シングルトンの宣言を開始する。シングルトンは、１つだけのインスタンスを持つクラスのこと
-> * override  : ものとメンバにfinalがついていなければ、クラスやトレイトの具象メンバをオーバーライドする
   * package   : パッケージスコープ宣言を開始する
   * private   : 宣言の可視性を制限する
   * protected : 宣言の可視性を制限する
-> * requires  : 非推奨。自分型に使われていた
   * return    : 関数からリターンする
-> * sealed    : 親クラスに適用すると、全ての直接派生したクラスを親クラスと同じソースファイルで宣言しなければならない
   * super     : thisに似ているが親の型に束縛される
   * this      : オブジェクトが自分自身を参照する方法。補助コンストラクタのメソッド名
   * throw     : 例外をスローする
-> * trait     : クラスのインスタンスに対して状態や振る舞いを追加するミックスインモジュール
   * try       : 例外をスローする可能性のあるブロックを開始する
   * true      : Boolean型の真
-> * type      : 型宣言を開始する
-> * val       : 読み込み専用の「変数」の宣言を開始する
-> * var       : 読み書き可能な変数の宣言を開始する
   * while     : whileループを開始する
-> * with      : クラスの宣言やオブジェクトのインスタンス化宣言にトレイトを含める
-> * yield     : for内包表記の中で、シーケンスの一部となる要素を返す
-> * _         : import文や関数リテラルなどで使われるプレースホルダ
-> * :         : 識別子と型アノテーションの間の区切り文字
   * =         : 代入
-> * =>        : 関数リテラルで関数の本体と引数リストを分離するために使われる
-> * <-        : for内包表記のジェネレータ式で使われる
-> * <:        : パラメータ化された型と抽象型の宣言で使われ、使用できる型を制限する
-> * <%        : パラメータ化された型と抽象型の「可視境界」の宣言で使われる
-> * >:        : パラメータ化された型と抽象型の宣言で使われ、使用できる型を制限する
-> * #         : 型射影に使われる
-> * @         : アノテーションを付ける
-> * ⇒        : (Unicodeの\u21D2) => と同じ
-> * ←        : (Unicodeの\u2190) <- と同じ
   */
  
  /* java.util.Scanner
   * Javaのメソッドで
   */
}


