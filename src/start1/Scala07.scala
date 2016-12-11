package start1

/**
 * 2.5 型情報の推論
 * 一意に定まるものは省略できる
 *
 */

/**
 *  **** 明示的な型アノテーションが必要なとき ****
 * 実用的な観点から次のような状況では明示的な型アノテーションが必要となる
 *
 * １．変数を宣言する場合。ただし、値の代入の場合を除く
 *     e.g. val name = "Scala"
 *
 * ２．全てのメソッド引数
 *     e.g. def deposit(amount:Money, ...)
 *
 * ３．次の場合のメソッドの戻り値
 *     a. メソッド内で明示的にreturnを呼ぶとき(最終行でreturnを呼ぶ場合も含む)
 *     b. メソッドが再帰的である場合
 *     c. メソッドが多重定義されており、多重定義されたメソッドの1つが別の多重定義された
 *        メソッドを呼び出すとき(「呼び出し側の」メソッドに戻り値の型アノテーションが必要)
 *     d. 推論された戻り値の型が意図した型よりも汎用的な型、たとえばAny型になるかもしれない場合
 *
 */
object Scala7 {

  // new HashMap[Integer, String] => new HashMap  // 右辺の型アノテーションは省略できる
  val intToStringMap:java.util.Map[Integer, String] = new java.util.HashMap

  // 型情報が完全に指定できていれば左辺の型指定も省略できる
  val intToStringMap2 = new java.util.HashMap[Integer,String]

 /* d.推論された戻り値の型が意図した型よりも汎用的な型の場合
  *
  * 意図した戻り値が List[String] なのだが
  *  makeList: (strings: String*)List[Any]
  *
  *  List(0) はInt型の0が１つ入った List[Int]
  *  strings.toList は List[String]
  *  なので推論される型は共通の List[Any] になる
  *
  *  List(0) -> List() にすべきであったがコンパイル時にはエラーにならない
  *
  *  公開されるAPIには全て戻り値の型宣言をすべき
  *  可能な限り一般的な戻り値の型を指定
  *  e.g. Array型とList型の共通の親であるSeq型
  *       変更可能性に配慮
  */
  def makeList(strings:String*) = {
    if (strings.length == 0)
      List(0)
    else
      strings.toList
  }

  /*
   * map1.update("book", "Scala") // => エラー
   *
   * Map[Nothing, Nothing] // Nothing型として推論されていた
   */
  val map1 = Map()                        // Nothing型に推論される
  val map2 = Map[String,String]()         // 明示的にString型を指定する
  val map3 = Map("Programing" -> "Scala") // String型が推論される初期値を与える

  /*
   * 等号記号がない場合全てUnitと推論される
   * print(double1(2))   // => ()  <- "()"はUnit型シングルトンインスタンスの名前(関数型プログラミングの慣習)
   *                           "{}"の方がわかりやすくない？
   * print(double2(2))   // => 4
   */
  def double1(n:Int) {n*2}                // Unit型と推論される -> (Command)手続きと解釈される
  def double2(n:Int) = {n*2}              // Int型と推論される  -> (Query)関数と解釈される


}