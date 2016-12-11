package start1

class Scala20 {
  /* 3.9 列挙
   *
   * Scalaでは標準ライブラリ内のクラスとして実装
   * Enumerationクラスを拡張したオブジェクトを定義するだけ
   * バイトコードレベルではJavaやC#のenum構文の間に関係がない
   */
  {
    object Breed extends Enumeration {
      val doberman = Value("Doberman Pinscher")  // 実際にはValueと言うメソッドを呼び出している
      val yorkie = Value("Yorkchire Terrier")    // 返り値はBread.Value型であり
      val scottie = Value("Scottich Terrier")    // 指定した文字列はValue.toStringの戻り値となる
      val done = Value("Great Dane")             // メソッドと型が同じValueと言う名前だが衝突はしない
      val portie = Value("Portuguese Water Dog") // Value(), Value(id:Int), Value(id:Int, String)
    }
    println("ID\tBreed")

    // ID値を明示してValueメソッドに渡さなかった場合
    // 自動的的に代入される
    for (breed <- (Breed.values)) println(breed.id + "\t" + breed) // Enum in Scala is Iterable

    println("\nJust Terriers")
    Breed.values.filter(_.toString().endsWith("Terrier")).foreach(println)
    // ID	Breed
    // 0	Doberman Pinscher
    // 1	Yorkchire Terrier
    // 2	Scottich Terrier
    // 3	Great Dane
    // 4	Portuguese Water Dog
  }

  { // IDを飛ばして代入した場合はどうなるか？  => 飛ばした番号から続く
    object Bread extends Enumeration {
      val butter = Value("Butter Torst")
      val jam = Value(8, "Jam Torst")
      val peanut = Value("Peanut Torst")
    }
    println("ID  Bread")
    for (bread <- (Bread.values)) println(bread.id + "  " + bread)
    // ID  Bread
    // 0  Butter Torst
    // 8  Jam Torst
    // 9  Peanut Torst
  }

  { // int 限界値付近のIDの連番はどうか？
    object Friend extends Enumeration {
      // val bob = Value(Int.MaxValue -2, "Bob Dylan") // java.lang.OutOfMemoryError: Java heap space
      val bob = Value(1002, "Bob Dylan") // スキップされた分もヒープに確保されるっぽい
      val soy = Value(-1,"Soy Sauce")    // マイナスの番号も指定できる
      val mochi = Value(20, "Omochi!")   // スキップされた番号にも指定できる
    }
    println("ID  Friend")
    for(friend <- (Friend.values)) println(friend.id + "  " + friend)
    // ID    Friend
    // -1    Soy Sauce
    // 20    Omochi!
    // 1002  Bob Dylan
  }

  { // コンパイラにより合成された型名と、自動的に生成されたID値を出力する
    object WeekDay extends Enumeration {
      type WeekDay = Value   // scala.Enumeration.Value
      val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value  // WeekDay.Value
    }
    import WeekDay._  // インポートすることで「型の別名」としてtype WeekDay = Valueを作る
                      // d:WeekDay.Value は d:WeekDay と書ける
    def isWorkingDay(d:WeekDay) = ! (d == Sat || d == Sun) // WeekDay._ をインポートすることで Sat, Sunと書ける
    WeekDay.values filter isWorkingDay foreach println     // インポートしない場合 WeekDay.Sat, WeekDay.Sun
    // Mon
    // Tue
    // Wed
    // Thu
    // Fri
  }
}