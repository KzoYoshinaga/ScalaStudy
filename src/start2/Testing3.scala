package start2

class Testing3 {

}

import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks
import org.scalatest.MustMatchers._
//import Element.elem

class ElementSpec extends WordSpec with PropertyChecks {
  "elem result" must {
    "have passed width" in {
      // 自動的に数百のwを生成しテストする
      //forAll { (w: Int) => whenever (w > 0) { elem('x', w, 3).width must equal (w) } }
      forAll {(n: Int) => whenever (n > 0) { n  > 0 }}
    }
  }
}