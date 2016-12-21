package start2

class Rational(n: Int, d: Int) {
  // 前提条件
  require(d != 0)
  // フィールドは定義された順番に評価される
  private val g = gcd(n.abs, d.abs)
  // 正規化のために最大公約数で割る
  val numer = n / g
  val denom = d / g
  // 追加コンストラクタ 基本コンストラクタ呼びだしを先頭に置く
  def this(n: Int) = this(n, 1)
  // メソッド上書き
  override def toString = numer + "/" + denom
  // 演算子記号もメソッド名として使用できる
  def +(that: Rational): Rational = new Rational(numer * that.denom + denom * that.numer, denom * that.denom)
  def +(i: Int): Rational = new Rational(numer + i * denom , denom)
  def -(that: Rational): Rational = new Rational(numer * that.denom - denom * that.numer, denom * that.denom)
  def -(i: Int): Rational = new Rational(numer - i * denom, denom)
  def *(that: Rational): Rational = new Rational(numer * that.numer, denom * that.denom)
  def *(i: Int): Rational = new Rational(numer * i, denom)
  def /(that: Rational): Rational = new Rational(numer * that.denom, denom * that.numer)
  def /(i: Int): Rational = new Rational(numer, denom * i)
  def lessThan(that: Rational): Boolean = this.numer * that.denom < that.numer * this.denom
  def max(that: Rational): Rational = if (this.lessThan(that)) that else this
  // スタックを使った最大公約数(greatest common divisor)
  def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  // ヒープを使った最大公約数 関数型スタイルではループ(Unit値)より再帰(値を返す式)を使う
  def gcdLoop(x: Long, y: Long): Long = {
    var a = x
    var b = y
    while(a != 0) {
      val temp = a
      a = b % a
      b = temp
    }
    b
  }

}