package start2

object Testing2 {

}

// ScalaTestのFlatSpecでふるまいを規定してテストする
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import Element.elem
class ElemetnSpec extends FlatSpec with Matchers {
  "A UniformElement" should
       "have a width equal to the passed value" in {
     val ele = elem('X', 2 ,3)
     ele.width should be (2)
  }
  it should "have a height equal to the passed value" in {
    val ele = elem('x', 2, 3)
    ele.height should be (3)
  }
  it should "throw an IAE if passed a negative width" in {
    an [IllegalArgumentException] should be thrownBy {
      elem('x', -2, 3)
    }
  }
}

import org.scalatest._
class TVSetSpec extends FeatureSpec with GivenWhenThen {
  feature("TV power button") {
    scenario("User pressed power button when TV is off") {
      Given("a Tb set that is switched off")
      When("the power button is pressed")
      Then("the TV shold switch on")
      pending  // テストも実際の動作もまだ実装されていないことを示す
    }
  }
}