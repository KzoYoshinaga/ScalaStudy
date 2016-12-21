package start2

class Testing {

}

import org.scalatest.Assertions._

object Testing extends App {

  // scala組み込み
  if(true) {"aa"} ensuring(_.length <= 2)
  else {"adfsdf"} ensuring(_.length >= 4)

  // scalaTest
  assert(List(1,2,3).contains(3))
  assertResult(2) { 2}
  // 期待された例外が投げられなかった場合にアサーションエラー
  assertThrows[IndexOutOfBoundsException] {List(1,2,3)(3)}
  // 期待された例外が投げられた場合にメッセージを返す
  println (intercept[IndexOutOfBoundsException] {List(1,2,3)(4)}.getMessage)
}

