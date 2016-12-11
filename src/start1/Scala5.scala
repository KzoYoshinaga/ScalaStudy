package start1

/**
 * Scala 2.8 からメソッドのデフォルト引数が指定できる
 * 以下等価なメソッド呼び出し
 *
 * import Scala5._
 *
 * print(joiner(List("aa","bb"), ","))   // => aa,bb
 *
 * print(joiner(List("aa",bb)))          // => aa bb
 *
 * print(joiner(strings = List("",""), "/"))   // => aa/bb
 *
 * print(joiner(List("aa","bb"), separator = "_")) // => aa_bb
 *
 * 名前付き引数渡しの場合、任意の順番で引数指定が出来る
 * print(joiner(separator = "_", strings = List("aa","bb"))) // => aa_bb
 *
 *
 */
object Scala5 {
  def joiner(strings: List[String], separator: String = " "): String =
    strings.mkString(separator)
}

/**
 * 名前とデフォルト値を持つクラスを作る
 * 以下適格なインスタンス生成
 * new OptionalUserProfileInfo
 * new OptionalUserProfileInfo(age = 29)
 * new OptionalUserProfileInfo(age = 29, location="Earth")
 */
object OptionalUserProfileInfo {
  val UnknownLocation = " "
  val UnknownAge = -1
  val UnknownWebSite = " "
}

class OptionalUserProfileInfo(
    location: String = OptionalUserProfileInfo.UnknownLocation,
    age: Int         = OptionalUserProfileInfo.UnknownAge,
    website: String  = OptionalUserProfileInfo.UnknownWebSite)
