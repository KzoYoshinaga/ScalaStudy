package start3

class Lists {

  {  // リストリテラル
    val fruit = List("apples", "oranges", "pears")
    val nums = List(1, 2, 3, 4)
    val diag3 = List(List(1,0,0), List(0,1,0), List(0,0,1))
    val empty = List()
  }
  {  // List型
    val fruit: List[String] = List("apples", "oranges", "pears")
    val nums: List[Int] = List(1, 2, 3, 4)
    val diag3: List[List[Int]] = List(List(1,0,0), List(0,1,0), List(0,0,1))
    val empty: List[Nothing] = List()
    // 共変：TがSのサブクラスならList[T]はList[S]のサブクラス
    val xs: List[String] = empty
  }
  {  // リストの構築 上記リスト構築は下記に展開される
    val fruit = "apples" :: ("oranges" :: ("pears" :: Nil))
    val nums = 1 :: (2 ::(3 :: (4 :: Nil)))
    val diag3 = (1 :: (0 :: (0 :: Nil))) ::
			          (0 :: (1 :: (0 :: Nil))) ::
			          (0 :: (0 :: (1 :: Nil))) :: Nil
		val empty = Nil
		// :: は右束縛なので括弧はずす
		val nums2 = 1 :: 2 :: 3 :: 4 :: Nil
  }
  {  // リストに対する基本操作
    // リストの全ての操作は、次の３つによって表現できる
    // head: リストの先頭要素を返す
    // tail: 先頭要素を除く全ての要素から構築されるリストを返す
    // isEmpty: リストが空ならtrueを返す
  }
  {  // 挿入ソート(insertion sort)の実装
    def isort(xs: List[Int]): List[Int] =
      if (xs.isEmpty) Nil
      else insert(xs.head, isort(xs.tail))
    def insert(x: Int, xs: List[Int]): List[Int] =
      if (xs.isEmpty || x <= xs.head) x :: xs
      else xs.head :: insert(x, xs.tail)
  }
  {  // リストパターン
    val fruit = List("apples", "orenges", "pears")
    // 長さが３のリストにマッチ
    val List(a, b, c) = fruit // a = "apples", b = "oranges", c = "pears"
    // 長さが２以上のリストにマッチ
    val d :: e :: rest = fruit // d = "apples", e = "oranges", rest = List("pears")
  }
  {  // パターンマッチを使った挿入ソート
    def isort(xs: List[Int]): List[Int] = xs match {
      case List() => List()
      case x :: xs1 => insert(x, isort(xs1))
    }
    def insert(x: Int, xs: List[Int]): List[Int] = xs match {
      case List() => List(x)
      case y :: ys => if (x <= y) x :: xs else y :: insert(x, ys)
    }
  }
  {  // Listクラスの一階メソッド(パラメータに関数を取らないメソッド)
    // ２個のリストの連結 :::
    val l1 = List(1, 2) ::: List(2, 3, 4, 45) // List(1, 2, 2, 3, 4, 45)
    val l2 = List() ::: List(1, 2, 3) // List(1, 2, 3)
    // xs ::: ys ::: zs は xs ::: ( ys :::  zs ) と解釈される(右結合)

    // 分割統治原則(divide and conquer principle) リスト結合の実装
    def append[T](xs: List[T], ys: List[T]): List[T] =
      xs match {
        case List() => ys
        case x :: xs1 => x :: append(xs1, ys)
      }

    // リストの長さを計算する
    val length = List(1, 2, 3).length

    // リストの末尾へのアクセス init と last
    // head <-> last , tail <-> last  双対的な操作(dual operation)が存在する
    // リスト全体をたどるのでリストの長さに比例する計算時間を要す
    val abcde = List('a','b','c','d','e')
    val last = abcde.last // last = 'e'
    val init = abcde.init // init = List('a', 'b', 'c', 'd')

    // リストの反転
    val edcba = abcde.reverse
    // reverseの遅い実装
    def rev[T](xs: List[T]): List[T] = xs match {
      case List() => xs
      case x :: xs1 => rev(xs1) ::: List(x)
    }
    // revメソッドの計算量
    // n + (n - 1) + ... + 1 = (1 + n) * n / 2  => O(n^2) 長さの自乗
    // ミュータブルな連結リストの標準的な反転はサイズに比例

    // プレフィックスとサフィックス drop, take, splitAt
    val ab = abcde take 2
    val cde = abcde drop 2
    val abANDcde = abcde splitAt 2 // (List('a', 'b'), List('c', 'd', 'e'))

    // 要素の選択 apply, indices
    val c1 = abcde apply 2  // Scalaではまれ リストの添え字アクセスには先頭からの長さ分時間がかかる
    val c2 = abcde(2)       // Scalaではまれ メソッド呼びだしの位置にオブジェクトがあるとapplyが挿入される
    val index = abcde.indices // Range(0, 1, 2, 3, 4)   indices: index の複数形

    // リストのリストから単層のリストへ flatten
    val flat = List(List(1,2,3), List(3), List(5,6,7)).flatten
    val fruit = List("apples", "orenges", "pears")
    val fruitChars = fruit.map(_.toCharArray()).flatten
    // fruitChars: List[Char] = List(a, p, p, l, e, s, o, r, e, n, g, e, s, p, e, a, r, s)
    val l = List(List(List(1,2,3)), List(List("dfasdf"))).flatten.flatten
    // l: List[Any] = List(1, 2, 3, dfasdf)

    // リストのジッパー操作 zip, unzip
    val a0b1c2d3e4 = abcde zip abcde.indices
    // a0b1c2d3e4: List[(Char, Int)] = List((a,0), (b,1), (c,2), (d,3), (e,4))
    val a0b1 = abcde zip List(0,1)
    // a0b1: List[(Char, Int)] = List((a,0), (b,1)) 長さが異なる場合切捨て
    val abcdeWithIndex = abcde.zipWithIndex
    // abcdeWithIndex: List[(Char, Int)] = List((a,0), (b,1), (c,2), (d,3), (e,4))
    val unzip = List(('a',0), ('b',1), ('c',2)).unzip
    // unzip: (List[Char], List[Int]) = (List(a, b, c),List(0, 1, 2))
    val mktuple = List(('a',0,"AA"), ('b',1,"BB"), ('c',2,"CC")).unzip3
    // mktuple: (List[Char], List[Int], List[String]) = (List(a, b, c),List(0, 1, 2),List(AA, BB, CC))

    // リストの表示 toString, mkString
    println(abcde.toString) // List(a, b, c, d, e)
    println(abcde mkString  "") // abcde
    println(abcde.mkString) // abcde
    println(abcde.mkString(" ")) // a b c d e
    println(abcde.mkString("?", " ", "*")) // ?a b c d e*
    println(abcde.mkString("List(", ", ", ")")) // List(a, b, c, d, e)
    val sBuilder = abcde.addString(new StringBuilder()) // StringBuilderに加える

    // リストの変換 iterator, toArray, copyToArray
    val arr = abcde.toArray // arr: Array[Char] = Array(a, b, c, d, e)
    val lis = arr.toList // lis: List[Char] = List(a, b, c, d, e)
    val arr2 = new Array[Int](10)
    List(1,2,3) copyToArray (arr2, 3)  // arr2のindex=3にリストの中身をコピー
    // arr2: Array[Int] = Array(0, 0, 0, 1, 2, 3, 0, 0, 0, 0)
    val it = abcde.iterator // it: Iterator[Char] = non-empty iterator
    print(it.next) // a
    print(it.next) // b

    // マージソートの実装
    /* アルゴリズム
     * リストの要素が０個または１個なら、すでにソートされているのでそのまま返す。
     * それよりも長いリストは２個の部分リストに分割する。
     * 部分リストは元のリストの約半分ずつの要素を格納する。
     * ソート関数の再帰呼びだしによって個々の部分リストをソートし、得られた２個のソート済みリストをマージ処理で結合する。
     */
    // カリー化によって比較関数のみ指定したマージソートを作ることができる
    def msort[T](less: (T, T) => Boolean)(xs: List[T]): List[T] = {
      def merge(xs: List[T], ys: List[T]): List[T] =
        (xs, ys) match {
          case (Nil, _) => ys
          case (_, Nil) => xs
          case (x :: xs1, y :: ys1) => if (less(x, y)) x :: merge(xs1, ys) else y :: merge(xs, ys1)
        }
      val n = xs.length / 2
      if (n == 0) xs
      else {
        val (ys, zs) = xs splitAt n
        merge(msort(less)(ys), msort(less)(zs))
      }
    }
  }
  {  // Listクラスの高階メソッド
    val nums = List(1,2,3,4,5)
    val abcde = List('a','b','c','d','e')
    val words = List("sldkfja","dkas;dlk","ae","klafv;z","asdfa")

    // リストの各要素のマッピング(変換) map, flatMap, foreach
    val plus1 = nums map (_ + 1) // plus1: List[Int] = List(2, 3, 4, 5, 6)
    val len = words map (_.length) // len: List[Int] = List(7, 8, 2, 7, 5)
    val rev = words map (_.toList.reverse.mkString) // rev: List[String] = List(ajfkdls, kld;sakd, ea, z;vfalk, afdsa)
    // flatMapの右被演算子はリストを生成する関数
    val flat = words flatMap (_.toList) // flat: List[Char] = List(s, l, d, k, f, j, a, d, k, a, s, ;, d, l, k, a, e, k, l, a, f, v, ;, z, a, s, d, f, a)
    val n1 = List.range(1,5) flatMap (i => List.range(1,i) map (j => (i, j)))
    // n1: List[(Int, Int)] = List((2,1), (3,1), (3,2), (4,1), (4,2), (4,3))
    val n2 = for (i <- List.range(1,5); j <- List.range(1, i)) yield (i, j)
    // n2: List[(Int, Int)] = List((2,1), (3,1), (3,2), (4,1), (4,2), (4,3))
    // foreachの右被演算子は手続き(procedure:結果型がUnitの関数)
    var accum = 0
    List(1,2,3,4,5,6,7) foreach (accum += _)
    // accum: Int = 28

    // リストのフィルタリング
    // filter, partition, find, takeWhile, dromShile, span
    // filter : xs filter p , xs: List[T] , (xs, T) => Boolean
    val even = nums filter (_  % 2 == 0) // even: List[Int] = List(2, 4)
    val len2 = words filter (_.length == 2) // len2: List[String] = List(ae)
    // partiton : xs partition  p equals (xs filter p, xs filter (!p(_)))
    // フィルタ関数が真の組みと偽の組みのリストのペアを返す
    val evenOdd = nums partition (_ % 2 == 0) // venOdd: (List[Int], List[Int]) = (List(2, 4),List(1, 3, 5))
    // find : xs find p , Option型が返される
    // xsの中にp(x)がtrueになるようなxがある場合はSome(x)がかえる
    // pがすべての要素に対してfalseになる場合はNoneが返される
    val len7 = words find (_.length == 7) // len7: Option[String] = Some(sldkfja) // findFirst
    val len5 = words find (_.length == 5) // len5: Option[String] = Some(asdfa)
    val len3 = words find (_.length == 3) // len3: Option[String] = None
    // takeWhile : xs takeWhile p , xsの先頭からpを成功させる要素の連続を取れるだけとって返す
    // dropWhile : xs dropWhile p , xsの先頭からpを成功させる要素の連続を削除できるだけ削除して、残りのリストを返す
    val take = List(1,2,3,-1,3) takeWhile (_ > 0) // take: List[Int] = List(1, 2, 3)
    val drop = List("tanaka", "tarou", "sato", "gohan") dropWhile (_.startsWith("t"))
    //  drop: List[String] = List(sato, gohan)
    val mistake = List(1,2,3,4,5-1,-2) takeWhile (_ < 0) // mistake: List[Int] = List() 先頭がマッチしないと返せない
    // span : xs span p equals (xs takeWhile p, xs dropWhile p)
    // xsの先頭からpを成功させる要素の連続の組みと、残りの組みのリストのペアを返す
    val span = List(1,2,3,-1,3) span (_ > 0) // span: (List[Int], List[Int]) = (List(1, 2, 3),List(-1, 3))
    // splitAtと同様にリストを２度たどることを避ける

    // リストを対象とする述語関数(Predicate) forall, exists
    // forall : xs forall p , xsの全ての要素がpを満足させる場合にはtrue (all)
    // exists : xs exists p , xsの中にpを満足させる要素がある場合にはtrue (any)
    val isAllPos = List(1,2,3,4,5,-1) forall (_ > 0) // false
    val isAllNeg = List(-1,-4,-5,-4) forall (_ < 0) // true
    val isAnyPos = List(-1,-4,-5,-4) exists (_ > 0) // false
    val isAnyNeg = List(1,2,3,4,5,-1) exists (_ < 0) // true

    // リストの畳み込み /:, :\
    // /: : 左畳み込み(fold left)
    // (z /: List(a,b,c))(op) equals op(op(op(z, a), b), c)
    // sum(List(a,b,c)) equals 0 + a + b + c
    def sum(xs: List[Int]): Int = (0 /: xs)(_ + _)
    sum(List(1,2,3,4,5)) // res19: Int = 15
    // product(List(a,b,c)) equals 1 * a * b * c
    def product(xs: List[Int]): Int = (1 /: xs)(_ * _)
    product(List(1,2,3,4,5)) // res20: Int = 120
    val join1 = ("" /: words)(_ + " " + _) // join1: String = " sldkfja dkas;dlk ae klafv;z asdfa" 先頭にスーペース
    val join2 = (words.head /: words)(_ + " " + _) // join2: String = sldkfja sldkfja dkas;dlk ae klafv;z asdfa
    // :\ : 右畳み込み(fold right)
    // (List(a,b,c) :\ z)(op) equals op(a, op(b, op(c, z)))
    def flattenLeft[T](xss: List[List[T]]) = (List[T]() /: xss)(_ ::: _)
    def flattenRight[T](xss: List[List[T]]) = (xss :\ List[T]())(_ ::: _)
    // xs ::: ys と言うリストの連結はxsの長さに計算量が比例する
    // 右畳み込みの方が効率が良い

    // foldを使ったリストの反転
    def reverseLeft[T](xs: List[T]) = (List[T]() /: xs){(ys, y) => y :: ys}
    // 反転するコンストラクタ演算子は(snoc)と呼ばれることがある
    // reverseLeftは先頭に要素を追加する(一定時間で終わる計算)snocをn回適用しているので計算量は長さに比例する

    // リストのソート sortWith
    // xs sortWith before , before:比較関数
    // マージソートを実行する
    List(1, -3, 2, 5) sortWith (_ < _) // res24: List[Int] = List(-3, 1, 2, 5)
    words sortWith (_.length < _.length) // res23: List[String] = List(ae, asdfa, sldkfja, klafv;z, dkas;dlk)
  }
  {  // Listオブジェクトのメソッド
    // 要素からリストを作る: List.apply
    List.apply(1,2,3) // List[Int] = List(1, 2, 3)
    List(1,2,3) // List[Int] = List(1, 2, 3)

    // 数値の範囲を作る: List.range
    List.range(1,10) // List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    List.range(1,10,2) // List[Int] = List(1, 3, 5, 7, 9) // 3番目の引数はstep
    List.range(9,1,-3) // List[Int] = List(9, 6, 3)
    List.range(1,9,-3) // List()

    // 同じ値のリストを作る: List.fill
    List.fill(5)('a') // List[Char] = List(a, a, a, a, a)
    List.fill(2, 3)('b') // List[List[Char]] = List(List(b, b, b), List(b, b, b))
    List.fill(3,2,4)(3)
    // List[List[List[Int]]] = List(List(List(3, 3, 3, 3), List(3, 3, 3, 3)), List(List(3, 3, 3, 3), List(3, 3, 3, 3)), List(List(3, 3, 3, 3), List(3, 3, 3, 3)))

    // 関数の実行結果による表の作成: List.tabulate
    List.tabulate(5)(n => n * n) // List[Int] = List(0, 1, 4, 9, 16)
    List.tabulate(5,5)(_ * _)
    // List[List[Int]] = List(List(0, 0, 0, 0, 0), List(0, 1, 2, 3, 4), List(0, 2, 4, 6, 8), List(0, 3, 6, 9, 12), List(0, 4, 8, 12, 16))

    // 複数のリストの連結: List.concat
    List.concat(List('a', 'b'), List('c')) // List[Char] = List(a, b, c)
    List.concat(List(), List('a', 'b'), List('c')) // List[Char] = List(a, b, c)
    List.concat(List()) // List[Nothing] = List()
  }
  {  // 複数のリストをまとめて処理する方法
    (List(1,2,3), List(30,20)).zipped.map(_ * _) // List[Int] = List(30, 40)
    (List("abc", "de"), List(3, 2)).zipped.forall(_.length == _) // true
    (List("abc", "de"), List(3, 2)).zipped.exists(_.length != _) // false

  }

}

object ListsTest extends App {
  def test(times: Int) {

    var list = List()

    val start = System.currentTimeMillis()
    // 後結合
    for (i <- 1 to times) {
      val l = list ::: List(i)
    }
    val split = System.currentTimeMillis()

    list = List()
    // 前結合
    for (i <- 1 to times) {
      val l = List(i) ::: list
    }
    val stop = System.currentTimeMillis()

    println("method1 = " + (split - start))
    println("method2 = " + (stop - split))
  }

}









