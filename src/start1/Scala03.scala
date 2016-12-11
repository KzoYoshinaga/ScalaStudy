/**
 * Scalaのmainメソッドはobjectのメソッドでなければならない
 */
object Scala3 {
  def main(args: Array[String]) = {
    args.map(_.toUpperCase()).foreach(printf("%s",_))
    println("")
  }
}