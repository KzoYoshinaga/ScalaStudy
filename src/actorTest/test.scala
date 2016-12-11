package actorTest

import akka.actor._

object test {
  def main(args:Array[String]) {
   implicit val system = ActorSystem("IloveU")
   implicit val target = system.actorOf(Props[Actress])
   target ! "IloveU"
   target ! "kill"
  }
}

class Actress extends Actor {
  def receive = {
    case "kill" => postStop
    case x:Any => println("Receive!")
  }
}