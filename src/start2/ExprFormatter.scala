package jp.ritz.expr

sealed abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BiOp(operator: String, left: Expr, right: Expr) extends Expr

class ExprFormatter {
  // 優先順位の昇順でグループにまとめた演算子を格納する
  private val opGroups = Array (
      Set("|", "||"),
      Set("&", "&&"),
      Set("^"),
      Set("==", "!="),
      Set("<", "<=", ">", "=>"),
      Set("+", "-"),
      Set("*", "%"))
  // 演算子から優先順位を導き出すマップ
  private val precedence = {
    val assocs =
      for {
        i <- 0 until opGroups.length
        op <- opGroups(i)
      } yield op -> i
    assocs.toMap
  }
  private val unaryPrecedence = opGroups.length // どの二項演算子の優先順位よりも高い
  private val fractionPrecedence = -1

  import start2.Element
  import start2.Element.elem
  private def format(e: Expr, enclPrec: Int): Element =
    e match {
      case Var(name) => elem(name)
      case Number(num) =>
        def stripDot(s: String) =
          if (s endsWith ".0") s.substring(0, s.length -2)
          else s
        elem(stripDot(num.toString()))
      case UnOp(op, arg) =>
        elem(op) beside format(arg, unaryPrecedence)
      case BiOp("/", left, right) =>
        val top = format(left, fractionPrecedence)
        val bot = format(right, fractionPrecedence)
        val line = elem('-', top.width max bot.width, 1)
        val frac = top above line above bot
        if (enclPrec != fractionPrecedence) frac
        else elem(" ") beside frac beside frac beside elem(" ")
      case BiOp(op, left, right) =>
        val opPrec = precedence(op)
        val l = format(left, opPrec)
        val r = format(right, opPrec + 1)
        val oper = l beside elem(" " + op + " ") beside r
        if (enclPrec <= opPrec) oper
        else elem("(") beside oper beside elem(")")
    }
  def format(e: Expr): Element = format(e, 0)
}

object Express extends App {
  val f = new ExprFormatter
  val e1 = BiOp("*", BiOp("/", Number(1), Number(2)), BiOp("+", Var("x"), Number(2)))
  val e2 = BiOp("+", BiOp("/", Var("x"), Number(2)), BiOp("/", Number(1.5), Var("x")))
  val e3 = BiOp("/", e1, e2)

  def show(e: Expr) = println(f.format(e) + "\n\n")

  for (e <- Array(e1,e2,e3)) show(e)
}
