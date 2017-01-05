package start22
/** 第２２章 リストの実装(implementing Lists)
 *
 * Scalaのリストがどのように実装されているかを説明する
 *
 * Listクラスの内部作用の知識は、さまざまな理由で役に立つ
 * ・リスト操作の相対的な効率がどのようなものかがより明確に分かるので
 *   高速でコンパクトなリストコードを書くために役に立つ
 * ・自分自身のライブラリを設計するときに応用できるさまざまなテクニックを得られる
 * ・ListクラスはScalaの型システム全般、特にジェネリックの概念を高度に応用した結晶のような存在なので
 *   Listクラスを学べば、これらの分野の知識が深まっていく
 */
class Principle {
  /** 22.1 Listクラスの原則
   *
   * リストは、Scalaの「組み込み」の言語要素ではない
   * ScalaパッケージのList抽象クラスによって定義されており
   * :: と Nil のためのサブクラスを伴っている
   *
   * この節では実際の実装より単純化した説明をする
   */
  // Listクラスの一部
  class ListImplementionPart {
    abstract class List[+T] { ??? }
  }
  /* 型パラメータの前の + はリストが共変であることを示している
   * したがってList[Int]の要素はList[Any]の変数に代入できる
   */
  val xs = List(1, 2, 3)
  var ys: List[Any] = xs

  /* すべてのリスト演算は次の３つの基本メソッドによって定義できる
   */
  class BasicMethods[T] {
    def isEmpty: Boolean
    def head: T
    def tail: List[T]
  }
  /* これら３種類のメソッドは、すべてListクラスでは抽象メソッドであり、
   * サブオブジェクトのNilとサブクラスの :: で定義される
   * クラス階層は以下のとおり
   *
   *               scala
   *              List[+T]
   *          <<sealed abstract>>  // sealedクラスは同じファイル内のサブクラス以外定義できない
   *                   |
   *                   |
   *      ---------------------------
   *      |                         |
   *    scala                    scala
   *    ::[T]                      Nil
   * <<final case>>           <<case object>>
   *
   */

  /** 22.1.1 Nilオブジェクト
   *
   * Nilオブジェクトは、空リストを定義する。
   * 定義は次のとおり
   */
  class NilImpl {
    class List[+T]
    case object Nil extends List[Nothing] {
      override def isEmpty = true
      def head: Nothing = throw new NoSuchElementException("head of empty list")
      def tail: List[Nothing] = throw new NoSuchElementException("tail of empty list")
    }
  }
  /* Nilオブジェクトは、List[Nothing]型を継承する。
   * この継承関係と「リストが共変である」ということから、NilはList型のすべてのインスタンスに対して互換性をもつ
   */

  /** 22.1.2 :: クラス
   *
   * :: クラスは「cons」(構築:constractの略)と発音され、空ではないリストを表す。
   * このような名前になっているのは、中置によるパターンマッチをサポートするため。
   *
   * パターンに含まれる全ての中置演算子は、引数に対する中置演算子コンストラクタの適用として扱われる。
   * e.g.
   * x :: xs というパターンは、::(x, xs) として扱われる
   * この :: はケースクラスである
   */
  class CaseImpl {
    sealed abstract class List[+T]
    final case class ::[T](hd: T, tl: List[T]) extends List[T] {
      def head = hd
      def tail = tl
      override def isEmpty: Boolean = false
    }
  }
  // headとtailはパラメータをそのまま返すので、以下のように短くできる
  class CaseImplCompaction {
    sealed abstract class List[+T]
    final case class ::[T](head: T, tail: List[T]) extends List[T] {
      override def isEmpty: Boolean = false
    }
  }
  /* このように定義できるのは、すべてのケースパラメータが暗黙のうちにクラスのフィールドになるから
   * (これはvalをプレフィックスとするパラメータ宣言と似ている)
   *
   * Scalaでは、headやtailなどのパラメータ無しの抽象メソッドを、フィールド使って実装できる
   * そこで、上のコードはListクラスから継承した抽象メソッドheadやtailの実装として、
   * 直接headやtailパラメータとして使っている
   */

  /** 22.1.3 その他のメソッド
   *
   *  他のすべてのListメソッドは、３つの基本メソッドで書くことができる
   */
  class OtherMethods {
    sealed abstract class List[+T] {
      def length: Int
      def drop(n: Int): List[T]
      def map[U](f: T => U): List[U]

      case object Nil extends List[Nothing] {
        override def isEmpty = true
        def head: Nothing = throw new NoSuchElementException("head of empty list")
        def tail: List[Nothing] = throw new NoSuchElementException("tail of empty list")
      }

      final case class ::[T](head: T, tail: List[T]) extends List[T] {
        override def isEmpty: Boolean = false
        // その他のメソッドを３つの基本メソッドで実装する
        def length: Int = if (isEmpty) 0 else 1 + tail.length
        def drop(n: Int): List[T] =
          if (isEmpty) Nil
          else if (n <= 0) this
          else tail.drop(n -1)
//         def map[U](f: T => U): List[U] =
//          if (isEmpty) Nil
//          else f(head) :: tail.map(f)
      }
    }
  }
  /** 22.1.4 リストの構築
   *
   * リストを構築する :: や ::: は右束縛なので
   * x :: xs のような演算は x.::(xs) ではなく xs.::(x) に展開される
   *
   * 要素値に要求される型はなんだろう？
   * リストの要素型と同じと思われるが、それでは制限が厳しすぎる
   *
   * その理由を知るための例
   */
  class restrict {
    abstract class Fruit
    class Apple extends Fruit
    class Orange extends Fruit
    // サブ型リストに対するスーパ型の要素の挿入
    val apples = new Apple :: Nil      // apples: List[Apple] = List(Apple@231aea6b)
    val fruites = new Orange :: apples // fruites: List[Fruit] = List(Orange@773b98f2, Apple@231aea6b)
    /* 元のリストの要素型(Apple)と追加される要素の型(Orange)の共通のスーパ型で
     * もっとも特化した型(Fruit)である
     *
     * こｎような柔軟性が得られるのは :: メソッド(cons)が次のように定義されいてるから
     *
     * def ::[U >: T](x: U): List[U] = new scala.::(x, this)
     *
     * 追加できる型はリスト要素型Tのスーパ型に制限されている
     * :: 下限境界をTとする多相定義は、便利なだけでなく、Listクラスの定義を
     * 正しい型で展開するには不可欠なのである
     *
     * :: を次のように定義したらどうなるかを考えてみる
     *
     * def ::(x: T): List[T] = new scala.::(x, this)
     *
     * メソッドパラメータは反変ポジションなので
     * リストの型要素であるTは、上の定義から反変ポジションとなる
     *
     * そうすると、ListはTについて共変と宣言できなくなってしまう
     * つまり、下限境界 [U >: T] には二重の効果があるのだ
     * ・型の問題を取り除く
     * ・::メソッドを柔軟に使えるようにする
     *
     * リスト連結メソッドの ::: は :: と同じやり方で定義されている
     *
     * def :::[U <: T](prefix: List[U]): List[U] =
     * 		if (prefix.isEmpty) this
     *    else prefix.head :: prefix.tail ::: this
     *
     * prefix.head :: prefix.tail ::: this
     * // :: ::: は右結合のため
     * prefix.head :: (prefix.tail ::: this)
     * // :: は右に束縛されるため
     * (prefix.tail ::: this).::(prefix.head)
     * // ::: は右に束縛されるため
     * this.:::(prefix.tail).::(prefix.head)
     */
  }
}

class ListBufferClass {
  /** 22.2 ListBufferクラス
   *
   * リストに対する典型的なアクセスパターンは、再帰である
   * 例えば、mapを使わずにリストの各要素をインクリメントするには次のように書く
   */
  def incAll(xs: List[Int]): List[Int] = xs match {
    case List() => List()
    case x :: xs1 => x + 1 :: incAll(xs1)
  }
  /* このプログラムパターンには、末尾再帰でないという欠点がある。
   * 上のコードのincAllに対する再帰呼び出しは、::演算の中で発生していることに注意する
   * そいのため、１つ１つの再帰呼び出しが新しいスタックフレームを必要とする
   *
   * 今日の仮想マシンではincAllを適用できるリストの要素数の上限は３万～５万程度
   *
   * ヒープ容量が許す限り、いくらでも大きなリストを操作できるincAlを書くにはどうすれば良いか？
   */
  // 末尾再帰を使ってみる(自作)
  def incAllHeap(list: List[Int]): List[Int] = {
    def inc(front: List[Int], end: List[Int]): List[Int] = end match {
      case List() => front.reverse
      case x :: xs1 => {
          val f = x + 1 :: front
          inc(f, xs1)
        }
    }
    inc(List(), list)
  }
  // リストバッファーを使ったループ
  // リストの構築方法としては効率的
  def incAllBuf(xs: List[Int]): List[Int] = {
    import scala.collection.mutable.ListBuffer
    val buf = new ListBuffer[Int]
    for (x <- xs) buf += x + 1
    buf.toList
  }
}
class ListClass {
  /** 22.3 Listクラスの実際の中身
   *
   * 上記リストバッファーの実装は、incAllの非末尾再帰と同様に、
   * スタックオーバーフローを起こすという欠陥がある
   *
   * Listクラスの大半の実装では再帰を避け、リストバッファーとループの併用方式をとっている
   */
  // 実際のマップの実装
  //    final override def map[U](f: T => U): List[U] = {
  //      import scala.collection.mutable.ListBuffer
  //      val b = new ListBuffer[U]
  //      var these = this
  //      while (!these.isEmpty) {
  //        b += f(these.head)
  //        these = these.tail
  //      }
  //      b.toList
  //    }
  /* 末尾再帰の実装であれば同じように効率的に成るが、一般的な再帰実装だと、
   * 遅くなる上にスケーラビリティが犠牲になる
   *
   * しかし、最後の b.toList はどうなのか？
   * 実際にはtoListメソッドはごくわずかなサイクルしか使われず、リストの長さに左右されない
   *
   * その理由を理解するために
   * 空ではないリストを構築する::クラスをもう１度見直してみる
   */
  //  final case class ::[U](hd: U, private[scala] var tl: List[U]) extends List[U] {
  //    def head = hd
  //    def tail = tl
  //    override def isEmpty: Boolean = false
  //  }
  /* tl は var になっているためリスト構築後にリストのテールが書き換えられる可能性がある
   * private[scala]修飾子が付いているので、scalaパッケージからで無ければアクセスできない
   *
   * ListBufferクラスは、scalaパッケージのサブパッケージscala.collection.mutableに含まれているので
   * consセルのtlフィールドにアクセスできる
   *
   * リストバッファの要素はリストとして表現されており、新しい要素の追加に当たっては
   * リストの最後の::セルのtlフィールドを書き換えている
   *
   * リストバッファの定義の冒頭は次のようになっている
   *
   * package scala.collection.mutable
   * final class ListBuffer[T] extends Buffer[T]  {
   * 		private var start: List[T] = Nil  // バッファーに格納されている全ての要素のリストを指す
   * 		private var last0: ::[T] = _      // リストの最後の::セルを指す
   * 		private var exported: Boolean = false  // toList操作を使ってバッファをリストに変換したかどうかを示す
   * 		...
   *
   * 		// toList操作は非常に単純
   * 		override def toList: List[T] = {
   * 				exported = !start.isEmpty
   * 				start
   * 		}
   * }
   *
   * toListでリストを返したら、そのリストはイミュータブルでなければならない。
   * しかし、last0要素を追加すると、startが参照するリストは書き換えられてしまう
   *
   * リストバッファ操作の正しさを維持するには、代わりに新しいリストを使う
   * これは+=操作の実装コードが最初の行で実行している
   *
   * override def += (x: T) = {
   * 		if (exported) copy()
   * 		if (start.isEmpty) {
   * 			last0 = new Scala.::(x, Nil)
   * 			start = last0
   * 		} else {
   * 			var last1 = last0
   * 			last0 = new scala.::(x, Nil)
   * 			last1.tl = last0
   * 		}
   * }
   *
   * exported が true なら、+= はstartが指すリストをコピーする。
   * イミュータブルなリストの末尾に要素を追加できるリストが必要なら、コピーは避けられない
   *
   * しかし、リストバッファからリストに転化された後に、更に拡張されるときに限り
   * リストのコピーが必要となるよう、ListBufferは実装されている
   */
}
class LooksOfFunction {
  /** w22.4 関数型の見かけ
   *
   * 見かけ上は純粋関数型だが、「内実」ではリストバッファを使った命令型実装になっている
   * これはScalaプログラミングでは一般的な戦略
   *
   * なぜ純粋性にこだわるのか？
   * リストの定義をオープンにして、tailフィールドやheadフィールドもミュータブルにしないのは？
   *
   * そうするとプログラムがひどく脆弱化する
   *
   * :: によるリストの構築が、構築されたリストのテールを再利用していることに注意しよう
   */
  // 次のように書いたとする
  val xs = List(4, 5, 6) // 共有されるテール部分
  val ys = 1 :: xs
  val zs = 2 :: xs
  /* ysリストとzsリストのテールは共有され、両方のテールは同じデータ構造をさしている
   * これは処理効率を上げるため
   *
   * しかし、共有の利用範囲は拡大していく傾向にあるので、リスト要素の変更が万が一にも可能であれば
   * きわめて危険なことになる
   *
   * たとえば、上のコードを前提とした上で、ysリストを先頭の2要素だけに縮小しようとするコードが
   * 次のように書けたとする
   *
   * ys.drop(2).tail = Nil // Scalaではこういうことはできない
   *
   * この場合、zsリストとxsリストも副作用として縮んでしまう
   *
   * ScalaのListとListBufferの設計は、JavaのStringとStringBufferとの関係に非常によく似ている
   * どちらの場合も、設計者は純粋にイミュータブルなデータ構造を維持しながら、そのような構造を少しずつ
   * 組み立てていく効率的な方法を提供しようとしたのである
   *
   * Scalaのリストには、::を使ってリストの先頭に要素を少しずつ追加していくか、
   * リストバッファーを使って末尾に要素を追加していくかである
   *
   * 通常は:: の方が再帰的なアルゴリズムと分割統治のプログラミングすたいるに適合するのに対し
   * リストバッファは伝統的なループベースのスタイルでよく使われる
   */
}

class Sum {
  /** 22.5 まとめ
   *
   * Listの２つのサブクラス、Nilと:: はともにケースクラスである
   * しかし、主要なリストメソッドの多くは、このデータ構造を再帰を使って操作していくのではなく
   * ListBufferを使って処理を実装している
   *
   * ListBufferは無駄なメモリを確保せずにリストを効率的に構築できるように、十分注意して実装されている
   * ListBufferは見かけ上は関数型。しかし、toListが呼び出された後は、バッファが破棄される通常のケース処理を
   * 高速化するため、内部では再代入を使っている
   */
}

class ImplementingLists {

}