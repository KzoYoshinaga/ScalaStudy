package start3

class Collections {
  {  // シーケンス
    // リスト ****
    // 先頭項目の追加・削除が高速だが、添え字を使った任意のアクセスはリストを線形にたどるので遅い
    val colors = List("red", "blue", "green")

    // 配列 ****
    // 要素のシーケンスを保存でき、要素の取得・変更がともに０を戦闘とする添え字によって任意の位置の要素に
    // 効率的にアクセスできる
    val fiveInts = new Array[Int](5) // Array[Int] = Array(0, 0, 0, 0, 0)
    val fiveToOne = Array(5, 4, 3, 2, 1)
    fiveInts(0) = fiveToOne(4)
    // fiveInts: Array[Int] = Array(1, 0, 0, 0, 0)
    for (i <- fiveInts) {
      println(i)
    }

    // リストバッファー ****
    // Listクラスは末尾へのアクセスが遅いので、末尾に要素を加える場合には以下の手順を検討する
    // 要素の順序を反転 -> 先頭に要素を挿入 -> 要素の順序を反転
    // しかし、ListBufferを使えばreverse呼び出しを避けられる
    import scala.collection.mutable.ListBuffer
    val listBuf = new ListBuffer[Int]
    listBuf += 1 // listBuf.type = ListBuffer(1)
    listBuf += 2 // listBuf.type = ListBuffer(1, 2)
    3 +=: listBuf // listBuf.type = ListBuffer(3, 1, 2)
    listBuf.toList // List[Int] = List(3, 1, 2)
    // 末尾再帰が使えないときはfor式,while式とListBufferの組み合わせを使う
    // スタックからヒープへ

    // 配列バッファー ****
    // 配列に似ているが、シーケンスの先頭と末尾で要素を追加・削除できる
    // 全ての配列操作が実行できるが、若干速度が遅い
    // 追加削除は平均では一定時間になるが、バッファーの内容を保存する新しい配列を確保するために、
    // サイズに比例する時間が必要になる場合がある
    import scala.collection.mutable.ArrayBuffer
    val arrBuff = new ArrayBuffer[Int]()
    arrBuff += 12 // arrBuff.type = ArrayBuffer(12)
    arrBuff += 15 // arrBuff.type = ArrayBuffer(12, 15)
    6 +=: arrBuff // arrBuff.type = ArrayBuffer(6, 12, 15)
    arrBuff.length // Int = 3
    arrBuff(0) // Int = 6

    // 文字列(StringOps) ****
    // PredefがStringからStringOpsへの暗黙の型変換を提供しているので
    // すべての文字列はシーケンスのように扱うことができる
    def hasUpperCase(s: String) = s.exists(_.isUpper) // 暗黙的にStringOpsに変換されそのexistsメソッドが呼ばれる
    hasUpperCase("Robert Frost") // true
    hasUpperCase("e e cummings") // false
  }
  {  // 集合(Set)とマップ
    // 集合の使い方 ****
    // 集合の最も重要な使い方は、== で比較して等価とされる要素が１つしかない(重複する要素がない)と言うこと
    // e.g. 文字列中の単語を数える
    import scala.collection.mutable
    val text = "See Spot run. Run, Spot. Run!"
    val wordsArray = text.split("[ !,.]+")
    val words = mutable.Set.empty[String]
    for (word <- wordsArray)
      words += word.toLowerCase()  // 重複を省きながら集合に入れられる
    // words: scala.collection.mutable.Set[String] = Set(see, run, spot)

    // イミュータブルな集合を作る
    val nums = Set(1,2,3) // Set(1, 2, 3)
    val nums2 = Set(1,2,3,3) // Set(1, 2, 3)
    nums + 5 // Set(1, 2, 3, 5) // 要素の追加
    nums - 3 // Set(1, 2) // 要素の削除
    nums - 9 // Set(1,2,3)
    nums ++ List(5,6) // Set(5, 1, 6, 2, 3) // 順序は保証されない？
    nums -- List(1,2) // Set(3)
    nums & Set(1,3,5,7) // Set(1, 3) // 他の集合との積集合
    nums.size // Int = 3
    nums.contains(3) // true
    // ミュータブルな集合を作る
    import scala.collection.mutable // ミュータブル版コレクションのインポート
    val mSet = mutable.Set.empty[String] // 空のミュータブルセット
    mSet += "the" // Set(the)
    mSet -= "the" // Set()
    mSet ++= List("do", "re", "mi") // Set(re, do, mi)
    mSet --= List("do", "re") // et(mi)
    mSet.clear // 全ての要素を取り除く
    mSet.toString // Set()

    // マップの使い方 ****
    // ミュータブルなマップの生成
    import scala.collection.mutable
    val map = mutable.Map.empty[String, Int]
    map("hello") = 1 // Map(hello -> 1)
    map("there") = 2 // Map(hello -> 1, there -> 2)
    map("hello") // Int = 1
    // map("what?") // java.util.NoSuchElementException
    // 文字列内に個々の単語が何個ずつ含まれているかカウントするメソッド
    def countWords(text: String) = {
      val counts = mutable.Map.empty[String, Int]
      for (rawWord <- text.split("[ ,!.]+")) {
        val word = rawWord.toLowerCase
        val oldCount =
          if (counts.contains(word)) counts(word)
          else 0
        counts += (word -> (oldCount + 1))
      }
      counts
    }
    countWords("Hi, my name is is your my own Hi, so so")
    // Map(is -> 2, name -> 1, my -> 2, own -> 1, so -> 2, hi -> 2, your -> 1)

    // イミュータブルなマップの生成
    val numbers = Map("i" -> 1, "ii" -> 2) // Map(i -> 1, ii -> 2)
    numbers + ("vi" -> 6) // Map(i -> 1, ii -> 2, vi -> 6)
    numbers - "ii" // Map(i -> 1)
    numbers ++ List("iii" -> 3, "v" -> 5) //  Map(i -> 1, ii -> 2, iii -> 3, v -> 5)
    numbers -- List("i","ii") //  Map()
    numbers.size // Int = 2
    numbers.contains("ii") // true
    numbers("ii") // Int = 2
    numbers.keys // Iterable[String] = Set(i, ii)
    numbers.keySet // scala.collection.immutable.Set[String] = Set(i, ii)
    numbers.values // Iterable[Int] = MapLike(1, 2)
    numbers.isEmpty // false
    // ミュータブルなマップ
    import scala.collection.mutable
    val wordword = mutable.Map.empty[String, Int] // Map()
    wordword += ("one" -> 1) // Map(one -> 1)
    wordword -= ("one") // Map()
    wordword ++= List("one" -> 1, "two" -> 2, "three" -> 3) // Map(one -> 1, three -> 3, two -> 2)
    wordword --= List("one", "two") // Map(three -> 3)

    // デフォルトの集合とマップ
    // ミュータブルなSetとMapのファクトリメソッドは内部でハッシュテーブルを使っている
    // mutable.Set() は HashSet、mutable.Map() は HashMap を返す

    // 一方イミュータブルな集合とマップは引数として渡した要素の数に応じて変わる
    // 要素が5個未満の集合では、最大限のパフォーマンスを引き出すために、個々のサイズごとに
    // 作られた専用のクラスが使われる、5個以上の場合はハッシュトライを使った実装を返す
    /* 要素数				実装
     * 0						scala.collection.immutable.EmptySet
     * 1						scala.collection.immutable.Set1
     * 2						scala.collection.immutable.Set2
     * 3						scala.collection.immutable.Set3
     * 4						scala.collection.immutable.Set3
     * 5個以上			scala.collection.immutable.HashSet
     */
    /* 要素数				実装
     * 0						scala.collection.immutable.EmptyMap
     * 1						scala.collection.immutable.Map1
     * 2						scala.collection.immutable.Map2
     * 3						scala.collection.immutable.Map3
     * 4						scala.collection.immutable.Map3
     * 5個以上			scala.collection.immutable.HashMap
     */

    // ソートされた集合とマップ: SortedSet, SortedMapトレイト
    // これらのトレイトは、要素の順序を管理するTreeSetクラスや、
    // キーの順序を管理するTreeMapクラスによって赤黒木を使って実装されている
    // 要素の順序はOrderdトレイトによって定義されているので、集合の要素型やマップのキー型は、
    // このトレイトをミックスインするか、暗黙のうちに型変換可能なものにする
    // TreeSetの使用例
    import scala.collection.immutable.TreeSet
    val ts = TreeSet(9, 3, 1, 8, 0, 2, 7, 4, 6, 5) // TreeSet(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    val cs = TreeSet('f','u','n') // TreeSet(f, n, u)
    // TreeMapの使用例
    import scala.collection.immutable.TreeMap
    val tm = TreeMap(3 -> 'x', 1 -> 'x', 4 -> 'x') // TreeMap[Int,Char] = Map(1 -> x, 3 -> x, 4 -> x)
    tm + (2 -> 'x') //  Map(1 -> x, 2 -> x, 3 -> x, 4 -> x)

    // ミュータブルとイミュータブルのどちらを使うべきか？
    val people1 = Set("Nancy", "Jane")
    // people1 += "Bob" // イミュータブル版に += はサポートされない
    var people2 = Set("Nancy, Jane")
    people2 += "Bob" // しかしvarで宣言すると新しい要素を加えた新しいセットが代入される
    people2 -= "Jane"
    people2 ++= List("Tom", "Harry")

    val ImMapVal = Map("a" -> 1) // 参照付け替えが出来ないイミュータブルなマップ
    var ImMapVar = Map("a" -> 1) // 参照付け替えが出来るイミュータブルなマップ
    import scala.collection.mutable
    val muMapVal = Map("b" -> 2) // 参照付け替えが出来ないミュータブルなマップ
    var muMapVar = Map("b" -> 2) // 参照付け替えが出来るミュータブルなマップ

    var rouphlyPi = 3.0
    rouphlyPi += 0.1

    // ＝で終わる全ての演算子でこの効果は適用される
  }
  {  // コレクションの初期化
    // コレクションを生成・初期化する最も一般的な方法は、コレクションのコンパニオンオブジェクトの
    // ファクトリメソッドに初期要素を渡すこと
    // コンパニオンオブジェクト名の後ろに括弧をつけ、その中に要素を書き込めばScalaコンパイラが
    // それをコンパニオンオブジェクトのapplyメソッド呼出に変換する
    Array(1.0, 2.0)
    List(1,2,3)
    Set('a','b','c')
    Map("hi" -> 2, "there" -> 5)

    // コンパイラの推測とは異なる型を指定してコレクションを作りたい場合
    import scala.collection.mutable
    val stuff1 = mutable.Set(42)
    // stuff1 += "abracadabra" // error: type mismatch
    val stuff2 = mutable.Set[Any](42)
    stuff2 += "abracadabra" // Any型には何でも入る

    // コレクションを他のコレクションで初期化したいとき
    val colors = List("blue", "red", "green", "yellow")
    import scala.collection.immutable.TreeSet
    // val treeSet = TreeSet(colors) // error: No implicit  Ordering defind for List[String]
    // この場合はTreeSet[String]を作ってから ++ 演算子を使ってリストの要素を追加する必要がある
    val treeSet = TreeSet[String]() ++ colors

    // 配列やリストへの変換 ****
    // 他のコレクションを使って、リストや配列を初期化する場合
    treeSet.toList
    treeSet.toArray
    // treeSetのtoListを呼び出して作ったリストはアルファベット順に並んでいる
    // コレクションのtoListやtoArrayを呼び出した時に作られるリストや配列の要素の順序は
    // 元のコレクションのiterator呼出によって得られるイテレータが要素を生成する順序と同じになる
    // TreeSetのイテレータは文字列をアルファベット順に生成する

    // 集合・マップのミュータブル版とイミュータブル版の相互変換 ****
    // リストの要素を使ってTreeSetを初期化するテクニックが使える
    // 空の集合を作って ++ か ++= のいずれかを使って、新しい要素を追加する
    // イミュータブルなTreeSetをミュータブルな集合に変換し、さらにイミュータブルな集合に再変換する例
    import scala.collection.mutable
    treeSet // scala.collection.immutable.TreeSet[String] = TreeSet(blue, green, red, yellow)
    val mubaSet = mutable.Set.empty ++= treeSet
    // scala.collection.mutable.Set[String] = Set(red, blue, green, yellow)

    val muta = mutable.Map("i" -> 1, "ii" -> 2)
    val immu = Map.empty ++ muta
  }
  {  // タプル
    // tuple: あらかじめ決められた数の項目を結合して１単位として渡せるようにしたもの
    (1, "hello", Console) // Int, String, Console.type) = (1,hello,scala.Console$@63fdbe14)
    // タプルは異なる型のオブジェクトを結合できるのでTraversableを継承しない
    def longestWord(words: Array[String]) = {
      var word = words(0)
      var idx = 0
      for (i <- 1 until words.length)
        if (words(i).length > word.length) {
          word = words(i)
          idx = i
        }
      (word, idx)
    }

    // 変数名を指定したタプルで受ける
    val longest = longestWord("The quick brown fox".split(" "))
    // longestWord: (words: Array[String])(String, Int)
    // longest: (String, Int) = (quick,1)
    println(longest._1) // quick
    println(longest._2) // 1

    // 要素に名前を付けた無名タプルとして受ける
    // 実装はパターンマッチの特殊ケース
    val (word, idx) = longestWord("The quick brown fox".split(" "))
    println(word) // quick
    println(idx) // 1

    // 括弧を省略すると同じタプルのデータを持った変数が複数できる
    // 同じ式を使った同時定義(multiple definitions)
    val word2, idx2 = longestWord("The quick brown fox".split(" "))
    println(word2) // (quick,1)
    println(idx2) // (quick,1)

    // 組み合わせに何らかの意味があるときや、何らかのメソッドを追加したいときにはクラスを使った方がよい
    // １つの概念に１つのクラス





  }
}






















