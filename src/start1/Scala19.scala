package start1

class Scala19 {

  /* 3.8 パターンマッチ
   *
   * 関数型言語から取り入れたアイデア
   *
   * プログラミングによる複数の条件から選択を行う方法
   *
   * Scalaのパターンマッチにおけるcaseは
   * 型、ワイルドカード、シーケンス、正規表現、オブジェクト変数の深い探索
   * まで含むことができる
   */

  /* 3.8.1 シンプルなマッチ
   */
  def simpleMatch {
    val bools = List(true, false)
    for (bool <- bools) {
      bool match {
        case true => println("heads")
        case false => println("tails")
        // case _ => println("something othe than heads or tails (yikes!)") // 到達不能コード
        // yikes -> yipe(s) ヤップス！
      }
    }
    // heads
    // tails
    // bools: List[Boolean] = List(true, false)
  }
  /* (_) はJavaやC♯のswitch文におけるdefaultキーワードと同じ役割
   *
   * パターンマッチは先行評価なので最初にマッチしたものが選択される
   * (_)ワイルドカードは全てをキャッチするので最後に書く
   */

  /* 3.8.2 マッチ式の中の変数
   *
   * ワイルドカードのcaseをotherNumberという変数に割り当てて、続く式でそれを出力
   */
  def innerVariable {
    import scala.util.Random
    val randomInt = new Random().nextInt(10)
    randomInt match {
      case 7 => println("lucky seven")
      case otheNumber => println("boo, got boring ol' " + otheNumber) // 指定した変数がキャッチし後の式に使える
    }
  }

  /* 3.8.3 型に対するマッチ
   */
  def typeMatch {
    val sundries = List(23, "Hello", 8.5, 'q') // Any型の要素を持つリスト
    for (sundry <- sundries) {
      sundry match {
        case i:Int    => println("got an Integer: " + i)
        case s:String => println("got a String: " + s)
        case f:Double => println("got a Double: " + f)
        case other => println("got something else: " + other)
      }
    }
    // got an Integer: 23
    // got a String: Hello
    // got a Double: 8.5
    // got something else: q
    // sundries: List[Any] = List(23, Hello, 8.5, q)
  }

  /* 3.8.4 シーケンスに対するマッチ
   *
   * リストの配列の長さと内容に対してマッチできる
   */
  def sequeceMatch {
    val willWork = List(1, 3, 23, 90)
    val willNotWork = List(4, 18, 52)
    val empty = List()
    for (l <- List(willWork, willNotWork, empty)) {
      l match {
        case List(_,3,_,_) => println("Four elements, with the 2nd being '3'.")
        case List(_*) => println("Any other list with o or more elemrnts") // シーケンス自体のパタンマッチにつかえる
      }
    }
    // Four elements, with the 2nd being '3'.
    // Any other list with o or more elemrnts
    // Any other list with o or more elemrnts
  }
  // リストの先頭や末尾を抽出するときにも使える
  def reduce {
    val willWork = List(1, 3, 23, 90)
    val willNotWork = List(4, 18, 52)
    val empty = List()

    def processList(l:List[Any]):Unit = l match {
      case head :: tail =>                              // head に先頭要素が格納され
        printf("%s ", head)                             // tail に残りの要素が格納される final case class ::
        processList(tail)                               // 再帰的に先頭を除いたシーケンスを自身に渡し
      case Nil => println("")                           // 要素が空(scala.collection.immutable.Nil)になったら改行
      // JavaScript
    }

    for (l <- List(willWork, willNotWork, empty)) {
      print("List: ")
      processList(l)
    }
    // List: 1 3 23 90
    // List: 4 18 52
    // List:
  }
  /* リストから先頭と残りを抽出するときには(::)ケースクラスのコンパニオンオブジェクトを使用する
   */

  /* 3.8.5 タプル（およびガード）に対するマッチ
   */
  def tupleMatch {
    val tupA = ("Good", "Morning!")
    val tupB = ("Guten", "Tag!")

    for (tup <- List(tupA, tupB)) {
      tup match {
        case (thingOne, thingTwo) if thingOne == "Good" =>        // ガード
          println("A two-tuple starting with 'Good'.")
        case (thingOne, thingTwo) =>
          println("This has two things: " + thingOne + " and " + thingTwo)
      }
    }
  }
  // 変数を宣言するときに型を明示しないか、Anyとして宣言することで
  // なんにでもマッチさせることができる

  // 期待している特定の項目だけを受け取り、全てをキャッチするようなcase節をけるようにコードを設計する

  /* 3.8.6 ケースクラスに対するマッチ
   *
   * パターンマッチでオブジェクトの中身を調べる「深いマッチ」
   */
  def caseClassMatch {
    case class Person(name: String, age: Int)  // ケースクラス
                                               // あらかじめ定義されたいくつかのメソッドを持つシンプルなオブジェクト
    val alice = new Person("Alice", 25)
    val bob   = new Person("Bob", 32)
    val charlie = new Person("Charlie", 32)

    for (person <- List(alice, bob, charlie)) {
      person match {
        case Person("Alice", 25) => println("Hi Alice!")
        case Person("Bob", 32)   => println("Hi Bob!")
        case Person(name, age) =>
          println("Who are you, " + age + " year-old person named " + name + "?")
      }
    }
  // Hi Alice!
  // Hi Bob!
  // Who are you, 32 year-old person named Charlie?
  }

  /* 3.8.7 正規表現に対するマッチ
   *
   * 一般にregex(Regular expression)と呼ばれる
   * 非形式的な構造を持ち「構造化データ」ではない文字列からデータを抽出するのに便利
   */
  def regexMatch {
    val BookExtractorRE     = """Book: title=([^,]+),\s+authors=(.+)""".r    // .r 正規表現に変換
    val MagazineExtractorRE = """Magazine: title=([^,]+),\s+issue=(.+)""".r  //

    val catalog = List(
        "Book: title=Programing Scara, authors=Dean Wampler, Alex Payne",
        "Magazin: title=The New Yorker, issue=January 2009",
        "Book: title=War and Peace, authors=Leo Tolstoy",
        "Magazine: title=The Atlantic, issue=February 2009",
        "BadData: text=Who put this here??"
    )

    for (item <- catalog) {
      item match {
        case BookExtractorRE(title, authors) =>
          println("Book \"" + title + "\", written by " + authors)
        case MagazineExtractorRE(title, issue) =>
          println("Magazine \"" + title + "\", issue " + issue)
        case entry => println("Unrecognized entry: " + entry)
      }
    }
   // Book "Programing Scara", written by Dean Wampler, Alex Payne
   // Unrecognized entry: Magazin: title=The New Yorker, issue=January 2009
   // Book "War and Peace", written by Leo Tolstoy
   // Magazine "The Atlantic", issue February 2009
   // Unrecognized entry: BadData: text=Who put this here??
  }
  def regexNew {
    // new を使ったマッチグループの作成
    val newRegex = new scala.util.matching.Regex("""\W""")
  }

  /* 3.8.8 case節内における入れ子の変数の束縛
   *
   * マッチ内のオブジェクトを変数に束縛、その入れ子のオブジェクトに対するマッチ条件を指定
   */
  {
    class Role
    case object Manager extends Role
    case object Developer extends Role

    case class Person(name: String, age: Int, role: Role)

    val alice = new Person("Alice", 25, Developer)
    val bob = new Person("Bob", 32, Manager)
    val charlie = new Person("Charlie", 32, Developer)

    for (item <- Map(1 -> alice, 2 -> bob, 3 -> charlie)) {
      item match {
        // p にマッチしたPersonが入る
        case (id, p @ Person(_,_,Manager)) => printf("%s is overpaid. \n", p)
        case (id, p @ Person(_,_,_)) => printf("%s is underpaid.\n", p)
      }
      // 等価 入れ子マッチャー
      item match {
        case (id, p) => p.role match {
          case Manager => printf("%s is overpaid. 2 \n" ,p)
          case _ => printf("%s is underpaid. 2 \n", p)
        }
      }
    }
    // Person(Alice,25,Developer) is underpaid.
    // Person(Alice,25,Developer) is underpaid. 2
    // Person(Bob,32,Manager) is overpaid.
    // Person(Bob,32,Manager) is overpaid. 2
    // Person(Charlie,32,Developer) is underpaid.
    // Person(Charlie,32,Developer) is underpaid. 2
  }

  /* 3.8.9 try-catch-finally 節の使用
   *
   * Scalaでは例外処理をただのパターンマッチとして扱う
   */

  {
    import java.util.Calendar

    val then = null
    val now = Calendar.getInstance()

    try {
      now.compareTo(then)
    } catch {
      case e: NullPointerException => println("One was null!"); System.exit(-1)
      case unknown: Throwable => println("Unknown exception " + unknown); System.exit(-1)
    } finally {
      println("It all worked out")
      System.exit(0)
    }

    try {
      throw new Exception
    } catch {
      case e:Exception => println("exception")
    }
  }

  /* 3.8.10 パターンマッチのまとめ
   *
   * switch文がクラス階層に依存する問題を避けたい <= クラスが変更されるたびに修正が必要になる
   *
   * オブジェクトの中のデータを取得するという設計課題に大しても有効
   * JavaBeans使用の弊害 setter,getter をデフォルトの判断にするべきではない
   *
   * 「状態情報」へのアクセスはカプセル化されるべきであり、その型が表現している抽象概念からみて
   * 合理性のある場合にだけ公開するべき
   *
   * 管理された方法で情報を抽出する必要があるという「まれ」な状況の場合、代わりにパターンマッチを
   * 利用することを検討 => 実装の詳細を隠したまま情報を抽出することもできる
   *
   * パターンマッチ文を設計するときは、発生する可能性のある全てのマッチを出来るだけ性格に把握する
   * デフォルトのcase節を当てにしない(「上記に該当しない」が正しい場合はどんな状況)
   *
   */

}