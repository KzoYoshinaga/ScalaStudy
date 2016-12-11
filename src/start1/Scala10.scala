package start1

class Scala10 {
  /* 2.8 Option、Some、None : nullの回避
   *
   * 変数や関数の戻り値が値を参照しない場合Option型を返すことが推奨される
   * Some型,None型はOption型のサブクラス
   *
   * 値がない場合: None型(実際はクラスではなくオブジェクト)
   * 値がある場合: Some型(その値をラップする)
   */
  val stateCapitals = Map(
      "Alabama" -> "Montgomery",
      "Alaska"  -> "Juneau",
      "Wyomin"  -> "Cheyenne")
  /*
   * println("Alabama: " + stateCapitals.get("Alabama"))  // 取得時にSomeでラップする
   * // Alabama: Some(Montgomery)
   *
   * println("Alabama: " + stateCapitals.get("Unknown"))
   * // Alabama: None
   *
   * println("Alabama: " + stateCapitals.get("Alabama").get)
   * // Alabama: Montgomery
   *
   * println("Alabama: " + stateCapitals.get("Unknown").getOrElse("Oops!"))  // java.util.Optonal.orElseGet(Suplier)
   * // Alabama: Oops!
   */

  /* = {
   *     if (値が存在する場合)
   *         new Some(値)
   *     else
   *         None
   *    }
   */



}