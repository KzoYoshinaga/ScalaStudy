package start2

class CaseClassTest extends App {
  def simplifyTop(expr: Expr): Expr = expr match {  // match式は必ず値を返す
      case UnOp("-", UnOp("-", e)) => e
      case BinOp("+", e, Number(0)) => e   // パターン中の変数は結果値に使える
      case BinOp("*", e, Number(1)) => e   // 先行評価：最初にマッチしたものが返される
      case _ => expr  // デフォルトパターンは必須。どれにもマッチしない場合例外
    }
  def matchMethods(expr: Expr): Unit = expr match {
      case BinOp(_, _, _) => println("It's binary operator!")  // ワイルドカードは捉えても結果値に使えないので
      case _ =>                                                // ケアしないパターンマッチに使える
    }

  // 定数パターン
  def descrive(x: Any) = x match {
    case 5 => "five"
    case true => "true"
    case "hello" => "hi!"
    case Nil => "the empty list"
    case _ => "something else"
    }

  // 変数パターン
  def varPattern(x: Any) = x match {
      case something => something.toString  // オブジェクトを変数に確保できる
    }

  // 定数の解釈
  import math.{E, Pi}
  val pi = math.Pi
  def symbolPattern(x: Any) = x match {
      case E => "E = " + E     // 先頭が小文字ならパターン変数 大文字なら定数とみなす
      case Pi => "Pi = " + Pi
      case this.pi => "Pi!"  // プレフィックスがあれば定数として使える
      case `pi` => "Pi!"  // バッククォートで囲むと定数として使える
      case pi => "You input = " + pi  // 別名定数を定義しても先頭が小文字なので変数とみなされる
      case _ => "Oop, something be wrong"
    }

  // シーケンスパタン
  def sequencePattern(x: Any) = x match {  // シーケンスパタン
      case List(0, _, _) => println("Found it!")
      case List(1, _*) => println("So long Sequence!") // 任意のシーケンスとマッチする
      case _ => println("Not found!")
    }

  // タプルパタン
  def tupplePattern(x: Any) = x match {
      case (a, b, c) => println("matched (" + a + ", " + b + ", "+ c + ")")
      case _ => println("Not matched!")
    }

  // 型付パタン 型キャストの代用として使える
  def typePattern(x: Any): Int = x match {
      case s: String => s.length     // s はString型に安全にキャストされている
      case m: Map[_, _] => m.size
      case _ => -1
    }
  def cast() {
    val s = "田中"
    val b: Boolean = s.isInstanceOf[String]  // 型チェック
    println(s + " is instance of String = " + b)
    val ss = s.asInstanceOf[String]          // 型キャスト
  }

  // 消去法
  def isIntIntMap(x: Any) = x match {
      case m: Map[Int, Int] => true   // 実行時に型引数の情報を管理していないので、要素型をパタンマッチに使えない
      case _ => false
    }
  def isStringArray(x: Any) = x match {
      case a: Array[String] => true   // 配列の要素型は配列値とともに格納しているので、要素型をパタンマッチに使える
      case _ => false
    }

  // 変数束縛パタン  変数@パタン
  def bindVarPattern(x: Any) = x match {
      case UnOp("abs", e @ UnOp("abs",_)) => e  // 絶対値演算が１つ少なくなったexprが取り出せる
      case _ =>
    }

  // パタンガード
  def simplifyAdd(e: Expr) = e match {
      case BinOp("+", x, y) if x == y => BinOp("*", x, Number(2))  // ifガードがtrueを返さなければマッチしない
      case _ => e
    }

  // パタンのオーバラップ 順序が重要になる場合
  def simplifyAll(expr: Expr): Expr = expr match {
      case UnOp("-", UnOp("-", e)) => simplifyAll(e)        // 個別的なケース
      case BinOp("+", e, Number(0)) => simplifyAll(e)
      case BinOp("*", e, Number(1)) => simplifyAll(e)
      case UnOp(op, e) => UnOp(op, simplifyAll(e))          // 包括的なケース
      case BinOp(op, l, r) => BinOp(op, simplifyAll(l), simplifyAll(r))
      case _ => expr
    }
  // val e = BinOp("+", UnOp("-", BinOp("*", Number(3), Var("x"))), BinOp("-", BinOp("+", Number(2), Number(3)), UnOp("-", Number(2))))
  // Expr  = BinOp(+,UnOp(-,BinOp(*,Number(3.0),Var(x))),BinOp(-,BinOp(+,Number(2.0),Number(3.0)),UnOp(-,Number(2.0))))

  // シールドされたケースクラスのマッチングにおいて
  // ケース漏れが容認出来る場合に警告を抑える
  def describe(e: Expr): String = (e: @unchecked) match {
    	case Number(_) => "a Number"
    	case Var(_) => "a Variable"
    }

  // Option型 Some(x) or None の２値をあらわす
  // null可能性のあるString型はOptional[String]型となる
  def show(x: Option[String]) = x match {
      case Some(s) => s
      case None => "?"
    }

  // 変数定義におけるパタン
  def varPattern() {
    val myTuple = (123, "abc")
    val (number, string) = myTuple  // タプルを分解し対応する変数を定義できる

    val exp = new BinOp("*", Number(5), Number(1))
    val BinOp(op, left, right) = exp  // ケースクラスのフィールドでそれぞれ定義できる
  }

  // 部分関数としてのケースシーケンス
  def partCase {
    val withDefault: Option[Int] => Int = {
      case Some(x) => x
      case None => 0
    }
    println(withDefault(Some(2)))
    println(withDefault(None))

    val second: List[Int] => Int = {  // List[Int] => Int型は整数リストをパラメータとして整数を生成するすべて
      case x :: y :: _ => y           // 全てのList[Int] => Int型の一部
    }                                 // 網羅的ではないと警告が出る。例外発生可能性
    second(List(1,2,3))
    second(List(1,2))
    // second(List())  // 実行時エラー

    val secondary: PartialFunction[List[Int], Int] = {  // 明示的に部分関数の型を定義
      case x :: y :: _ => y
    }
    secondary.isDefinedAt(List(1,2,3))  // PartialFunction[]型は
    secondary.isDefinedAt(List(1,2))    // 定義されているか調べるメソッドを持っている
    secondary.isDefinedAt(List())

    // このような部分関数値に変換される
    new PartialFunction[List[Int], Int] {
      def apply(xs: List[Int]) = xs match {
        case x :: y :: _ => y
      }
      def isDefinedAt(xs: List[Int]) = xs match {
        case x :: y :: _ => true
        case _ => false
      }
    }
  }

}

// シールドクラス
// 同じファイルで定義されているサブクラス以外は新しいサブクラスを追加できない
// 管理できないケースクラスを作られる恐れがない！
// ケース漏れがあればコンパイルで警告が出る！
sealed abstract class Expr
case class Var(name: String ) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr