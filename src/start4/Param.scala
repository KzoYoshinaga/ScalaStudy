package start4
/**
 * 型のパラメータ化
 *
 * 純粋関数型待ち行列クラスの設計の例で情報隠蔽のテクニックを示す
 */
/**
 * 関数型待ち行列
 *
 * 関数型待ち行列は、次の３つの操作を持つデータ型である
 *
 * head: 待ち行列の先頭要素を返す
 * tail: 先頭要素を取り除いた形で待ち行列を返す
 * enqueue: 指定した要素を末尾に追加した新しい待ち行列を返す
 *
 * 関数型待ち行列はミュータブルな待ち行列とは異なり、要素が追加されても内容を書き換えず
 * その要素を含んだ新しい待ち行列を返す
 *
 * ここでは、以下のように動作するQueueと言う名前のクラスを作ることを目標にする
 *
 * val q = Queue(1, 2, 3) // q: Queue[Int] = Queue(1, 2, 3)
 * val q1 = q enqueue 4 // q1: Queue[Int] = Queue(1, 2, 3, 4)
 * q // res0: Queue[Int = Queue(1, 2, 3)]
 */
/**
 * 表現型でリストを使った、単純な実装
 * headとtailはリストにおける同じ操作に変換し、enqueueは連結メソッドに変換する
 */
class SlowAppendQueue[T](elems: List[T]) { // 効率が悪い
  def head = elems.head
  def tail = new SlowAppendQueue(elems.tail)
  def enqueue(x: T) = new SlowAppendQueue(elems ::: List(x))
}
/**
 * このenqueueの演算は、要素の数に比例した実行時間を必要とする
 *
 * 待ち行列を表現するリストの向きを逆にした実装
 */
class SlowHeadQueue[T](smele: List[T]) { // 効率が悪い
  def head = smele.last
  def tail = new SlowHeadQueue(smele.init)
  def enqueue(x: T) = new SlowHeadQueue(x :: smele)
}
/**
 * enqueueにかかる時間は一定になるが、逆にheadとtailの実行時間が一定ではなくなる
 * ２つの例を組み合わせ、一定に近いパフォーマンスを引き出す
 *
 * leadingとtrailingという２本のリストで待ち行列を表現する
 * leading: 前から順番に要素を並べる
 * trailing: 待ち行列の末尾から逆順に要素を並べる
 * ある特定の時点における待ち行列全体の内容は、
 * leading ::: trailing.reverse になる
 */
class Queue[T](
    private val leading: List[T],
    private val trailing: List[T]
) {
  private def mirror =
    if (leading.isEmpty)
      new Queue(trailing.reverse, Nil)
    else
      this
  def head = mirror.leading.head
  def tail = {
      val q = mirror
      new Queue(q.leading.tail, q.trailing)
    }
  def enqueue(x: T) =
    new Queue(leading, x :: trailing)
}
/**
 * この待ち行列の計算量はどうなるか？
 * mirror演算は、待ち行列の要素数に比例する時間を必要とする場合があるが、
 * それはleadingリストが空の時だけ。
 *
 * リストが空の時に長さがｎの待ち行列があるとして、mirrorは長さｎのリストを反転コピーしなければならない。
 * しかし、次のmirror呼び出しはtail演算をｎ回行った後である。
 *
 * headとtailとenqueueが同じ頻度で現れるとすると、それぞれの演算の平均計算量は一定になる
 * このとき、関数型待ち行列は、漸近的計算量ではミュータブルな待ち行列にほぼ匹敵する
 *
 * headが連続的に呼び出される時は、最初の呼び出しだけが内部表現の再構成を実行するように
 * 設計することが可能
 */

/**
 * 情報隠蔽
 *
 * 上記Queueは内部の複雑な実装を不必要に外部にさらしている
 * Queueコンストラクタは、パラメータとして２個のリストをとり、しかも１つは反転させたもの
 * このコンストラクタを隠す手段が必要
 */

/**
 * 非公開コンストラクタとファクトリメソッド
 */
// private を指定してコンストラクタを隠す
class Queueue[T] private (
    private val leading: List[T],
    private val trailing: List[T]
) {}
/* このコンストラクタにアクセスできるのは、クラス自体の内部とコンパニオンオブジェクトだけ
 *
 */
// コンパニオンオブジェクトのapplyファクトリメソッド
object Queueue{
  def apply[T](xs: T*) = new Queue[T](xs.toList, Nil)
}

/**
 * 非公開クラス
 *
 * クラス自体を隠してしまい、クラスの公開インターフェイスを示すトレイトだけをエクスポートする
 */
class privateClass {
  trait Queue[T] {
    def head: T
    def tail: Queue[T]
    def enqueue(x: T): Queue[T]
  }

  object Queue {
    def apply[T](xs: T*): Queue[T] =
      new QueueImpl[T](xs.toList, Nil)
    private class QueueImpl[T](
      private val leading: List[T],
      private val trailing: List[T]
    ) extends Queue[T] {
      def mirror =
        if (leading.isEmpty)
          new QueueImpl(trailing.reverse, Nil)
        else
          this
      def head: T = mirror.leading.head
      def tail: QueueImpl[T] = {
          val q = mirror
          new QueueImpl(q.leading.tail, q.trailing)
        }
      def enqueue(x: T) =
        new QueueImpl(leading, x :: trailing)
      override def toString = "List("+ leading.mkString(", ") + ", " + trailing.reverse.mkString(", ") + ")"
    }
  }
}

/**
 * 変位指定アノテーション
 *
 * 上記Queueはトレイトであって型ではない
 * def doesNottCompile(q: Queue) = {}
 * <console>:13: error: trait Queue takes type parameters
 *        def doesNottCompile(q: Queue) = {}
 *                               ^
 * QueueはトレイトだがQueue[String]は型である
 * def doesCompile(q: Queue[AnyRef]) = {} // doesCompile: (q: Queue[AnyRef])Unit
 * def doesCompile(q: Queue[String]) = {} // doesCompile: (q: Queue[String])Unit
 * def doesCompile(q: Queue[Int]) = {} // doesCompile: (q: Queue[Int])Unit
 *
 * Queueがあれば、型パラメータを指定して型を作れるので、型コンストラクタと呼ばれる
 * Queueという型コンストラクタはQueue[Int], Queue[String], Queue[AnyRef] などの
 * 型ファミリーを生成する
 *
 * Queue[T]によって生成された型ファミリーのメンバの間に特別なサブ型関係はあるか？
 * 型SがTのサブ型とするとき、Queue[S]はQueue[T]のサブ型と考えるべきか？共変か？
 * Queue[AnyRef]型の値パラメータを取るメソッドににQueue[String]を渡せるのか？
 *
 * val q = Queue("a") // Queue[String]
 * def doesCompile(q: Queue[AnyRef]) = {}
 * doesCompile(q)
 *
 * <console>:36: error: type mismatch;
 * found   : Queue[String]
 * required: Queue[AnyRef]
 * Note: String <: AnyRef, but trait Queue is invariant in type T.
 * You may wish to define T as +T instead. (SLS 4.5)
 *        doesCompile(q)
 *                    ^
 *
 * Scalaはデフォルトで非共変(nonvariant)
 * しかしQueueトレイトの型パラメータを変更することで共変にすることができる
 *
 * Queue[+T] { ... }
 *
 * + ではなく - の場合は反変(contravariant)
 *
 * Queue[-T] { ... }
 *
 * TがSのサブタイプだとするとQueue[S]はQueue[T]のサブタイプになる・・・！
 * このような + や - を変位指定アノテーション(variance annotations)と言う
 *
 * 古いJavaではジェネリックな配列をエミュレートする目的でObject配列を使っている
 * このような古いメソッドとのやりとりのために、
 * ScalaではT型の配列をTのスーパ型の配列にキャストできるようにしている
 */
class variance {
  val a1 = Array("a")
  val a2: Array[Object] = a1.asInstanceOf[Array[Object]]
  // コンパイルは通るがArrayStoreException実行時例外が投げられる場合がある

  // val a3: Array[Object] = a1 // error: type mismatch;
}
/**
 * 変位指定アノテーションのチェック
 *
 * 再代入可能なフィールドや配列要素が関わっている場合に、変位指定が問題を起こすことをみてきた
 * しかし、それらがなくとも問題が起きる状況は作れる
 *
 * 型特化した処理が記述されている場合
 */
class varAno {
  class Queue[+T] {
    def enqueue(x: T): Queue[T]
    // 再代入可能なフィールドには + アノテーション付きパラメータは使えない
    // コンパイルエラーになる
    // 変位指定アノテーションをつけるとコンパイラのチェック対象になる
  }

  class StrangeIntQueue extends Queue[Int] {
    override def enqueue(x: Int) = {
      println(math.sqrt(x))  // 数値型に特化した処理 実行時エラー
      super.enqueue(x)
    }
  }
  object Main extends App {
    val x: Queue[Any] = new StrangeIntQueue
    x.enqueue("abc") //
  }
}
/* 上記のように実行時エラーが予測される場合にはコンパイラチェックを受け
 * コンパイルエラーになる
 * enqueueの再代入可能なパラメータ var x: T は
 * def x: T と def x_=(y: T) として扱われる
 * セッターメソッドはフィールドの型であるTのパラメータを取っている
 * そのためフィールド型は共変にすることができない
 */

/** Scalaコンパイラの変位指定アノテーションチェックの仕組
 *
 * ポジション(position): クラス(またはトレイト)内の型パラメータを使える全ての場所のこと
 * e.g. メソッドの値パラメータは型を持っており、クラスの型パラメータがそこに現れる可能性があるのでポジションである
 *
 * Scalaコンパイラはクラスやトレイトの中のあらゆるポジションを以下に分類する
 *
 * 陽性(positive): + アノテーションを持つ型パラメータが使えるポジション
 * 陰性(negative): - アノテーションを持つ型パラメータが使えるポジション
 * 中性(neutral) : 変位指定アノテーションの無い型パラメータが使えるポジション
 *
 * コンパイラは、ポジションを分類するために、型パラメータ宣言からスタートして、ネストレベルの深い部部に入っていく
 * デフォルトではネストの中のポジションは、メソッドの外のポジションと同じだが、分類が変化する例外がいくつかある
 *
 * メソッドの値パラメータのポジションでは、メソッドの外のポジションが反転(flip)する
 * 陽性->陰性    陰性->陽性    中性->中性
 *
 * メソッドの型パラメータのポジションでも分類は反転する
 *
 * C[Arg]の中のArgのような型の中の型引数のポジションでも、対応する型パラメータの
 * 変位次第で分類が反転することがある
 * ・Cの型パラメータが＋アノテーションを持つ場合 -> 同じ分類
 * ・Cの型パラメータが－アノテーションを持つ場合 -> flip
 * ・変位指定アノテーションを持たない場合 -> 中性に変わる
 *
 * e.g.
 *
 * abstract class Cat[-T, + U] {
 * 		def meow[W-](volume: T-, listener: Cat[U+, T-]-): Cat[Cat[U+, T-]-, U+]+
 * }
 *
 * ・型パラメータWと２つの値パラメータのポジションは全て陰性
 * ・meowの結果型(Cat[Cat[U+,T-]-, U+])における第一引数(Cat[U+,T-])のポジションは陰性
 *   これはCatの型パラメータであるTに－のアノテーションがついているから
 * ・結果型の第一引数(Cat[U+,T-])第一引数Uのポジションは２度の反転で陽性になる
 *   しかしT型のポジションは陰性のまま
 *
 * ここからも分かるように、変位指定のポジションを管理することはきわめて難しい
 * コンパイラはポジションの分類を計算したら、個々の型パラメーターが分類の適切なポジションだけで
 * 使われているかどうかをチェックする。この場合、Tは陰性ポジションだけで使われており、Uは陽性ポジションだけで使われている
 * よってCatの型指定は正しい
 */

/** 下限境界(lower bounds)
 *
 * Queue[T]の定義ではTがenqueueメソッドのパラメータの型として現れる。
 * Tが現れる場所は陰性のポジションなので、Tを共変にすることができない
 *
 * しかし、この場合では対処ができる
 * enqueueメソッドを多相的にして(つまりenqueueメソッド自体に型パラメータを与えて)
 * その型パラメータに下限境界を設定する
 */
// 下限境界が設定された型パラメータ
class lowerBounds {
  class Queue[+T] {
    def enqueue[U >: T](x: U) =
      new Queue[U]
  }
}
/* この定義では、U >: T という構文によりTをUの下限境界として設定している
 * そのため、UはTのスーパ型で無ければならない。
 * 注： スーパ型とサブ型の関係は反射律を満たす。つまり、型は型自身のスーパ型、サブ型でもある。
 *      TがUの下限境界であっても、enqueueのTを渡すことができる
 * 注： 下限境界で反転が起きる。型パラメータUは陰性ポジションにある(１反転)が、下限境界(>: T)は
 *      陽性ポジションにある(２反転)
 *
 * 前バージョンと異なり、新しい定義なら、待ち行列の要素型(T)の任意のスーパ型(U)の要素を代入できる
 * この場合の結果型はQueue[U]となる。
 *
 * この例は、変位指定アノテーションと下限境界の組み合わせがうまく機能することを示している
 * これは、インタフェイスの型が詳細設計と実装を導くという型駆動設計(type-driven design)のよい例である
 */

/** 反変(contravariance)
 *
 * 反変が自然な場合とは？
 */
class contra {
  trait OutputCannel[-T] {
    def write(x: T)
  }
}
/* このコードのOutputChannelは、型パラメータTを反変と定義している。
 * そのため、たとえばAnyRefの出力チャンネルは、Stringの出力チャンネルのサブ型になる
 *
 * 妥当性の理由を考えるために、OutputChannel[String]で何が出来るのかを考えてみる
 *
 * サポートされている操作はStringの書き込みだけ
 * OutputChannel[AnyRef]に対してもStringの書き込みは出来るだろう
 * そこで、OutputChannel[String]をOutputChannel[AnyRef]に置き換えても問題はおきない
 *
 * 逆に、OutputChannel[AnyRef]が必要なところにOutputChannel[String]と書くのは危険
 * OutputChannel[AnyRef]には任意のオブジェクトを送り込めるがOutputChannel[String]に
 * 書き込めるのは文字列だけである
 *
 * リスコフの置換原則(Liskov Substitution Principle): U型が必要とされているすべての場所
 * でT型の値を使えるなら、T型はU型のサブ型だと考えても良い。
 *
 * TがUと同じ操作をサポートし、さらにTのすべての操作がUの対応する操作よりも要求することが少なく
 * 提供することがより多いのであれば、この原則が成り立っている
 *
 * 出力チャンネルの場合、OutputChannel[AnyRef]の方がOutputChannel[String]よりも
 * 要求することが少ないので、OutputChannel[AnyRef]はOutputChannel[String]のサブ型になれる
 *
 * この場合の「少ない」と言うのはOutputChannel[AnyRef]なら引数はAnyRefでありさえすればよいが
 * OutputChannel[String]だと引数はStringでなければならない事をさす
 *
 * メソッド内でString特化の処理を書いてもAnyRefなら問題なく処理できる
 */

/* 同じ型が共変と反変の両方を混在させている場合もある、その顕著な例が関数トレイトである
 * e.g. A => B と言う関数型を書いたときScalaはこれをFunction1[A, B] と展開する
 */
// Functon1に含まれる共変と反変
class Mix {
  trait Function1[-S, + T] {
    def apply(x: S): T
  }
}
// 関数型パラメータの変位指定のサンプル
class funSam {
  class Publication(val title: String)
  class Book(title: String) extends Publication(title)
  object Library {
    val books: Set[Book] =
      Set(new Book("Programming in Scala"),
          new Book("Walden"))
    def printBookList(info: Book => AnyRef) = // BookはPublicationのサブ型
      for (book <- books) println(info(book)) // AnyRefはStringのスーパ型
  }
  object Customer extends App {
    def getTitle(p: Publication): String = p.title // PublicatonはBookのスーパ型
    Library.printBookList(getTitle)                // StringはAnyRefのサブ型
  }
}

class Param {

}





















