package start1{

  import akka.actor._

  class Value(val money: Integer) {
    override def toString() = "Value: " + money + "$"
  }

  abstract class Thing {
    def price()
  }

  class Present(val value:Value) extends Thing {
    def price() = println("its " + value)
  }

  class Actress extends Actor {
    def receive = {
      case "Kill" => println("F**k"); postStop
      case "I Love U" => println("Me too!")
      case "Present For U" => println("Thanks!")
      case present: Present => present.price()
      case x: Unit => println("What?")
    }
  }

  object Main {
    def main(args:Array[String]) {
      val system = ActorSystem("Propose")
      val target = system.actorOf(Props[Actress])
      target ! "Yo!"
      target ! "I Love U"
      target ! "Present For U"
      target ! new Present(new Value(100))
      target ! "Kill"
    }
  }
}