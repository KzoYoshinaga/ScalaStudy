package start1

/**
 * 2.4.2 入れ子のメソッド定義
 *
 * print(Scala6.factorial(3))  // => 3 * 2 * 1 = 6
 * print(Scala6.factorial(10)) // => 10 * 9 * 8 * 7 * 6 * 5 * 4 * 3 * 2 *  1 = 3628800
 */
object Scala6 {
  def factorial(i: Int): Int = {
    def fact(i: Int, accumulator: Int): Int = {
      if (i <= 1){
        print(" 1 = ")
        accumulator
      }
      else{
        print (i + " * ")
        fact(i - 1, i * accumulator)
      }
    }
    fact(i, 1)
  }
}

/**
 * クロージャを使ったインナーメソッドでの再帰
 *
 * Clojure.countTo(10) // => 1 2 3 4 5 6 7 8 9
 */
object Clojure {
  def countTo(n:Int):Unit = {
    def count(i:Int):Unit = {
      if (i < n) {
        print(i + " ")
        count(i+1)
      }
    }
    count(1)
  }
}

/**
 * 改変
 * Clojure2.count(2,10,2) => 2 4 6 8
 * Clojure2.count(3,1,2)  => (なし)
 * Clojure2.count(from=2,to=100,step=10) // => 2 12 22 32 42 52 62 72 82 92
 */
object Clojure2 {
  def count(from:Int, to:Int, step:Int) {
    def counter(i:Int) {
      if (i < to) {
        print(i + " ")
        counter(i + step)
      }
    }
    counter(from)
  }
}