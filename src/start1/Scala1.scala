class Scala1 {

  // いくつかの文字列を受け大文字に変換
  def upper(strings: String*) : Seq[String] = {
    strings.map((s:String) => s.toUpperCase())
  }

  // 推論により短くできる
  def lower(strings:String*) = strings.map((s:String) => s.toLowerCase())

}
