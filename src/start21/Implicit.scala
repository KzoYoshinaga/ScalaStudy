package start21

/** 第２１章 暗黙の型変換とパラメータ: Implicit Conversions and Parameters
 *
 * 暗黙の型変換と暗黙のパラメータ
 *
 * これらを使うことにより、本当に重要な部分を目立たなくさせてしまう紋切り型コードを省略できるようになり
 * 既存のライブラリーを今までよりもずっと気持ちよく扱えるようになる
 *
 * プログラムの大切なポイントに焦点を絞ったコードが書ける
 */

/** 21.1 暗黙の型変換
 *
 * 暗黙の型変換は、２つのソフトウェア構成要素が互いに意識せずに開発されたときに、
 * それぞれの要素が機能することを助けるものである
 *
 * それぞれのライブラリーは、本質的には同じコンセプトをそれぞれの方法でコード化している
 * 暗黙の型変換は、片方の型からもう片方への明示的な型変換を行う数をへらして、２つの
 * ライブラリーを併用しやすくする
 */
import javax.swing.JButton
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

class swing {
  /* 暗黙の型変換を行わない場合、Swingを使うScalaプログラムはJavaと同じように
   * 内部クラスを使わなければ成らない
   */
	val button = new JButton
	button.addActionListener(
	    new ActionListener {
	      def actionPerformed(event: ActionEvent) = {
	        println("pressed!")
	      }
	    })
	/* もっとScala向きなコードが書ければ、以下のように引数として関数を取っていたはず
	 *
	 * button.addActionListener( // 型の不一致となる
	 * 		(_: ActionEvent) => println("pressed!")
	 * )
	 *
	 * ※これはScala 2.12では機能する
	 */
}
class typeChanger {
  /* 関数からアクションリスナーへの暗黙の型変換
   */
  implicit def function2ActionListener(f: ActionEvent => Unit) =
    new ActionListener {
    def actionPerformed(event: ActionEvent) = f(event)
  }
  /* このメソッドは、１個の引数をとる他のメソッドと同様に、直接呼び出して、
   * その結果を他の式に渡すことができる
   */
  val button = new JButton
  button.addActionListener(function2ActionListener(
      (_:ActionEvent) => println("pressed!")))
  /* function2ActionListenerにはimplicitが付いているので、
   * このメソッド呼出は省略してもコンパイラーが自動的に挿入してくれる
   */
  val button2 = new JButton
  button2.addActionListener((_:ActionEvent) => println("pressed!"))
  /* まずコンパイラはそのままの形でコンパイルしようとする
   * しかし、型の不一致があることに気付く
   *
   * コンパイラはあきらめず、問題を修復できる暗黙の型変換を探す
   *
   * この場合はfunction2ActionListenerが見つかる
   *
   * コンパイラは型変換メソッドを試し、上手く機能することを確かめ、実際に動作させる
   */
}

/** 21.2 implicit の規則
 *
 * implicit定義とは: 型エラーを修正するために、コンパイラがプログラムに挿入できるメソッドのこと
 *
 * e.g.
 * x + y が型チェックを通らない場合、コンパイラは convert(x) + y に変えてコンパイルを試みる
 * convertは、使える状態になっている何らかの暗黙の型変換メソッドである
 * convert が x を + メソッドを持つ何らかの型に変換できれば、型チェックをパスして正しく実行できる
 * プログラムに修正できるかもしれない
 *
 * そして、convertが単純な変換関数にすぎず、しかもソースコードからその部分を省略できるなら
 * コードが明確になる
 *
 * 以下、暗黙の型変換の一般原則
 */
class rules {
  /* ● マーキングルール: implicitによって修飾された定義だけが暗黙の型変換に使われる
   *
   * implicit は、任意の変数、関数、オブジェクト定義に付けられる
   */
  // Sample
  implicit def intToString(x: Int) = x.toString
  /* コンパイラが x + y を convert(x) + y に書き換えるのは、convert が implicit 宣言されている場合だけ
   */

  /* ● スコープルール: 挿入される暗黙の型変換は、単一の識別子としてスコープ内にあるか、
   *                    変換のソース型やターゲット型として対応付けられていなければならない
   *
   * 単一の識別子
   * e.g. コンパイラは someVariable.convert と言う形式の変換を挿入することはない
   *      someVariable.convert を implicit として使えるようにするには、importするしかない
   *
   * 実際に、ライブラリーは役に立つ暗黙の型変換をいくつか含んだPreambleオブジェクトを用意することが多い
   * そうしたライブラリーを使うコードではimport Preamble._ と書くだけで、そのライブラリーに含まれる
   * 暗黙の型変換全にアクセスできるようになる
   *
   * 例外
   * 型変換においてソース型(変換前の型)やターゲット型(変換要求された変換後の型)
   * のコンパニオンオブジェクトの中に含まれているimplicit定義も、コンパイラの探索対象に入る
   *
   * 例：Dollarのコンパニオンオブジェクトにimplicit定義を組み込んだ例
   * object Dollar {
   * 		implicit def dollarToEuro(x: Dollar): Euro = ...
   * }
   * class Dollar { ... }
   *
   * このような場合、変換dollarToEuroはDollar型に関連付いているといわれる
   *
   * ファイル内のコードを読むときに考慮しなければならない他のファイルのものは、インポートされているものと
   * 明示的に完全名を指定して参照されているものだけ
   */

  /* ● １度に１回ルール: 暗黙の型変換は１度しか挿入されない
   *
   * コンパイラが x + y を convert1(convert2(x)) + y に書き換えることはない
   *
   * ただし暗黙の型変換に暗黙のパラメータを取らせてこの制限を回避することは出来る
   */

  /* ● 明示的変換優先ルール: 書かれたままの状態でコードが型チェックをパスするときには、暗黙の型変換は行われない
   *
   * わけがわからにほどに短いコードでは明示的な型変換を行う、といった選択が可能
   */
}

class name {
  /** 21.2.1 暗黙の型変換の名前の付け方
   *
   *  暗黙の型変換には何でも自由に名前を付けられる。
   *  問題になるのは次の２つのケース
   *
   *  ・メソッドの適用時に明示的にメソッド名を書きたいとき
   *  ・プログラムの任意の位置で使える暗黙の型変換が何かを判定するとき
   *
   *  後者について具体的に考えてみる
   */
  // 次のように暗黙の型変換を２つ持つオブジェクトがあったとする
  object MyConversions {
    implicit def stringWrapper(s: String): IndexedSeq[Char] = ???
    implicit def intToString(x: Int): String = ???
  }
  /* アプリケーションの中では、stringWrapper変換を使いたいが、intToString変換で
   * 整数が自動的に文字列に変換されるのは困ると思っていたとする
   *
   * その場合は、片方の変換のみインポートする
   */
  class Application {
    import MyConversions.stringWrapper
    // ... stringWrapperを利用するコード
  }
}
/** 21.2.2 暗黙の型変換が試される場所
 *
 *  暗黙の型変換が使われる場所は３つある
 *  ・要求された型への変換
 *  ・レシーバーの変換
 *  ・暗黙のパラメータ
 *
 * 要求された型への暗黙の型変換は、異なる型が必要とされている文脈において使いたい型を使えるようにする
 * 手持ちの型には実行したいメソッドが定義されていないときでも、レシーバーの変換が出来れば実行できる
 * 暗黙のパラメータは、呼び出し元が求めていることについて、呼び出された関数により多くの情報を与えるために使われる
 */

class Implicit {
  /** 21.3 要求された型への暗黙の型変換
   *
   *  要求された型への暗黙の型変換は、コンパイラがこのテクニックを使う第１の場所
   *  コンパイラから見えるのはXだが、必要とされるのはYだというときに、コンパイラはXをYに変換する暗黙の関数を探す
   *
   *  たとえば、通常は精度が下がって情報が失われるので、倍精度浮動小数点数を整数に変換することはできない
   *
   *  val i: Int = 3.5 // error: type mismatch
   *
   *  しかし、暗黙の型変換を定義すれば、このエラーを避けられる
   */
   implicit def doubleToInt(x: Double) = x.toInt
   val i: Int = 3.5 // i: Int = 3
   /* コンパイラは、Intが要求されている場所でDouble、具体的には3.5を検出した
    * コンパイラは通常の型エラーを検出
    * DoubleからIntへの暗黙の型変換を探す
    *
    * コードは水面下で次のように変換される
    * val i: Int = doubleToInt(3.5)
    *
    * DoubleからIntへの暗黙の型変換は、目に見えない形で情報の精度が下がるような処理をするという
    * あまり適切ではない発想に基づく処理であり、非難されてもしょうがない
    *
    * しかし、これとは逆方向の制限の強い型から一般的な型への変換には意味がある
    * たとえばIntは精度を失わずにDoubleに変換できる
    *
    * Scala.Predef オブジェクトは「小さな」整数型から「大きな」整数型への暗黙の型変換を定義している
    * e.g.
    * implicit def int2double(x: Int): Double = x.toDouble
    */
}
class Receiver {
  /** 21.4 レシーバーの変換
   *
   *  メソッド呼び出しのレシーバー: メソッド呼び出しで使われるオブジェクトにも
   *  暗黙の型変換は適用される
   *
   *  この種の暗黙の型変換には、主に２つの用途がある
   *  ・既存のクラスを円滑に組み込むこと
   *  ・Scala言語の枠内でドメイン固有言語(DSL)を書くためのサポート
   */
  /* 21.4.1 新しい型の同時利用
   *
   * 既存の型に新しい型を統合すること
   */
  class Rational(n: Int, d: Int) {
    // ...
    def + (that: Rational): Rational = ???
    def + (that: Int): Rational = ???
  }
  /* Rationalクラスは２個の分数を加算することも、分数と整数を加算することもできる
   */
  val oneHalf = new Rational(1, 2) // oneHalf: Rational = 1/2
  oneHalf + oneHalf // res0: Rational = 1/1
  oneHalf + 1 // res1: Rational = 3/2
  /* では 1 + oneHalf のような式はどうか？
   * この式はレシーバーが１であり、１は適切な ＋メソッドを持っていない
   *
   * 1 + oneHalf // error: overloaded value + with alterrnatives (Double)Double <and>... cannot be applied to (Rational)
   */
  // 複数の型を併用するこの種の演算がエラーとならないようにするには、IntからRationalへの暗黙の型変換を定義する
  implicit def intToRational(x: Int) = new Rational(x, 1)
  1 + oneHalf // res2: Rational = 3/2

  /** 21.4.2 新しい構文のシミュレーション
   *
   *  新しい構文のシミュレート
   *
   *  Map(1 -> "one", 2 -> "two", 3 -> "three")
   *  -> はどのようにサポートされているだろうか？
   *
   *  -> はScalaプリアンブルscara.Predefの中で定義されているArrowAssocクラスのメソッド
   *  1 -> "one" と書いたとき、コンパイラは１からArrowAssocへの暗黙の型変換も定義されている
   */
  object Predef {
    class ArrowAssoc[A](x: A) {
      def -> [B](y: B): Tuple2[A, B]  = Tuple2(x, y)
    }
    implicit def any2ArrowAssoc[A](x: A): ArrowAssoc[A] =
      new ArrowAssoc(x)
    // ...
  }
  /* 言語に対する構文拡張的な機能を提供するライブラリーでは、この「リッチラッパー」パターンがよく見られる
   * RichSometingと言う名前のクラスを見かけたら、Something型に見えるメッソドを追加していると考えてよい
   */

  /** 21.4.3 暗黙のクラス
   *
   * 暗黙のクラスとは、implicitキーワードが先頭に付けられたクラスのこと
   * コンパイラはそのようなクラスに対しては、クラスのコンストラクタ引数からクラス自体への暗黙の変換コードを生成する
   *
   * 例えば、画面上の矩形の幅と高さを表すRectangleというクラスがあったとする
   */
  case class Rectangle(width: Int, height: Int)
  /* このクラスを非常に頻繁に使うようなら、リッチラッパーパターンを使ってもっと簡単に構築できるようにしたい
   * と思うかもしれない
   */
  implicit class RectangleMaker(width: Int) {
    def x(height: Int) = Rectangle(width, height)
  }
  /* 上の定義は、通常のようにRactangleMakerクラスを定義するが、それに加えて、次のような変換コード
   * も自動生成される
   */
  // 自動生成される
  class AutoCreation {
    implicit def RectanbleMaker(width: Int) =
      new RectangleMaker(width)
  }
  /* その結果、二つの整数の間に ｘ を入れると矩形が作れるようになる
   */
  val rect = 1 x 2 // rect: Rectangle = Rectangle(1,2)
  /* 暗黙のクラスはケースクラスにはなれない、コンストラクタは１個の引数を取るものでなければならない
   * また、暗黙のクラスは、他のオブジェクト、クラス、トレイトの中に配置されていなければならない
   *
   * しかし、実際的には、既存のクラスにいくつかのメソッドを追加するためのリッチラッパーとして
   * 暗黙のクラスを使っている限り、これらの制限が問題となることはない
   */
}
class ImplicitParam {
  /** 21.5 暗黙のパラメータ
   *
   * コンパイラが暗黙の型変換を挿入する第３の場所は、パラメーターリストである
   *
   * コンパイラは、someCall を someCall(a)(b)、あるいは new SomeClass(a) を new SomeClass(a)(b)
   * に置き換えて、足りないパラメータリストを補い、関数呼び出しを完成させることがある
   *
   * 補足されるのは、最後のパラメータではなく、カリー化された最後のパラメータリスト全体である
   *
   * e.g.
   * someCall呼び出しに欠けているパラメータリストが３個のパラメータから構成されている場合
   * コンパイラはsomeCall(a) を someCall(a)(b, c, d) に置き換える
   * この用法では、(b, c, d)に含まれている識別子、b,c,dの定義には、implicit宣言が必要なだけでなく
   * someCallの最後のパラメータりすと、またはsomeCallの定義にもimplicitが必要である
   */
  /* 簡単な例を示す
   * ユーザーが選んだシェルのプロンプト文字列("$ "や"> ")をカプセル化する
   * PreferrdPromptクラスがあったとする
   */
  class PreferredPrompt(val preference: String)
  /* また、Greeterオブジェクトというものもあり、その中には２個のパラメータリストをとるgreetメソッド
   * が含まれているものとする
   * このメソッドの第１パラメータリストはユーザー名の文字列を取り、第２パラメータリストはPreferredPromptをとる
   */
  object Greeter {
    def greet(name: String)(implicit prompt: PreferredPrompt) = {
      println("Welcom, " + name + ". The system is ready.")
      println(prompt.preference)
    }
  }
  /* 最後のパラメータリストにはimplicitマークが付けられている
   * これは、パラメータリストを暗黙のうちに供給してよいという意味である
   *
   * しかし、次のように、プロンプト文字列はまだ明示的に指定している
   */
  val bobsPrompt = new PreferredPrompt("relax> ")
  Greeter.greet("Bob")(bobsPrompt)
  // Welcom, Bob. The system is ready.
  // relax>
  /* コンパイラに暗黙のうちにパラメータリストを供給させたければ
   * まず、必要とされる型の変数を定義しなければならない
   *
   * この場合、その型とはPreferredPromptである
   * 例えば、ユーザー設定オブジェクト(JoesPrefs)の中に次のようなコードを入れておけばよい
   */
  object JoesPrefs {
    implicit val prompt = new PreferredPrompt("Yes, master> ")
    Greeter.greet("Joe")
  }
  /* val自体にimplicitが付けられていることに注目
   * これがあるのでコンパイラは足りないパラメータリストを供給するのにこの値を使うことが出来る
   * また、この値は単一の識別子としてスコープ内に居なければならない
   */
  object KzoPrefs {
    implicit val prompt = new PreferredPrompt("Yes, master> ")
    Greeter.greet("Kzo")
    // Welcom, Kzo. The system is ready.
    // Yes, master>
  }
  /* implicitキーワードが個々のパラメータではなく、パラメータリスト全体に適用されていることに注意
   * e.g.
   */
  class MultParam {
    class PreferredPrompt(val preference: String)
    class PreferredDrink(val preference: String)
    object Greeter {
      def greet(name: String)(implicit prompt: PreferredPrompt, drink: PreferredDrink) = {
        println("Welcome, " + name + ". The system is ready")
        println("But while you work, ")
        println("why not enjoy a cup of " + drink.preference + "?")
        println(prompt.preference)
      }
    }
    object KzoPrefs {
      implicit val prompt = new PreferredPrompt("Yes, master> ")
      implicit val drink = new PreferredDrink("coffee")
    }
    import KzoPrefs._
    Greeter.greet("Kzo")(prompt, drink)
    Greeter.greet("Kzo")
    // Welcome, Kzo. The system is ready
    // But while you work,
    // why not enjoy a cup of coffee?
    // Yes, master>
  }
  /* ここで注意して起きたいことが１つある。preferenceフィールドを介してpromptやdringに与えられたのはStringである
   * にもかかわらず、このサンプルでは、promptやdrinkの型としてStringを使っていない(わざわざ型を作る)
   *
   * コンパイラは、パラメータの型とスコープ内の値の型をマッチさせて暗黙のパラメータを選択する
   * そこで、偶然の一致が起きないように、暗黙のパラメータには通常「珍しい」あるいは「特別な」型を使う
   *
   * PreferredPromptやPreferredDrinkは暗黙のパラメータ型として使うことだけを目的とした型である
   */
  /* 暗黙のパラメータが多く使用されるのは、Haskellの型クラスと同じように、先にパラメータリストで明示されていた型
   * の情報を提供したいときである
   *
   * e.g.
   * リスト21.2のmaxListUpOrdering関数について考えてみる
   * この関数はパラメータリストの中の最大値を返す
   */
  def maxListOrdering[T](elements: List[T])(ordering: Ordering[T]): T =
    elements match {
      case List() => throw new IllegalArgumentException("empty list!")
      case List(x) => x
      case x::rest =>
        val maxRest = maxListOrdering(rest)(ordering)
        if (ordering.gt(x, maxRest)) x
        else maxRest
  }
  /* Ordering[T]型の追加引数はT型の要素を比較するときに使うorderingを指定する
   * そのため、このバージョンでは組み込みのorderingを持たない型でも使える
   *
   * しかし、StringやIntなど自明のデフォルトorderingを持つ型でも、明示的にorderingを指定しなければならない
   * そこで、第２引数を暗黙の引数にすればよい
   */
  def maxListImpParam[T](elements: List[T])(implicit ordering: Ordering[T]): T =
      elements match {
      case List() => throw new IllegalArgumentException("empty list!")
      case List(x) => x
      case x::rest =>
        val maxRest = maxListImpParam(rest)(ordering)
        if (ordering.gt(x, maxRest)) x
        else maxRest
  }
  /* このパターンは非常によく現れるので、Scala標準ライブラリは様々な共通型向けに暗黙のorderingメソッドを提供している
   */
  maxListImpParam(List(1, 2, 10, 3)) // res0: Int = 10
  maxListImpParam(List(1.3, 5.2, 10.7, 3.1415)) // res1: Double = 10.7
  maxListImpParam(List("one", "two", "three")) // res2: String = two

  /** 21.5.1 暗黙のパラメータのためのスタイルの原則
   *
   * 暗黙のパラメータの型にたいしては、独自の名前を使うのが望ましい
   * 上記サンプルではpromptやdrinkの型は、Stringではなく、PreferredPromptやPreferredDrink
   * である。
   *
   * 反例を示す
   */
  def maxListPoorStyle[T](element: List[T])(implicit orderer: (T, T) => Boolean): T
  /* (T, T) => Boolean の型の目的が何かをまったく示せていない、等価のテスト、より小さいかのテスト、
   * より大きいかのテスト、あるいはまったく異なるテストなのかもしれない
   */
}
class ContextLimit {
  /** 21.6 コンテキスト境界
   *
   *  implicitを使えば、暗黙のパラメータとして暗黙の型変換を供給するだけではなく、
   *  メソッド本体でもその暗黙の型変換を使うことが出来る
   *  そのため、メソッド本体に含まれて意tあ最初のorderingは省略できる
   */
  // 暗黙のパラメータを本体内で使う関数
  def maxList[T](element: List[T])(implicit ordering: Ordering[T]): T =
    element match {
      case List() => throw new IllegalArgumentException("empty list!")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxList(rest) // 暗黙のうちに(ordering)が追加される
        if (ordering.gt(x, maxRest)) x // このorderingはまだ明示されている
        else maxRest
  }
  /* コンパイラの動き
   * val maxRest = maxList(rest)
   * を解析したとき、型が一致していないことに気付く
   * maxList(rest)という式は１個のパラメータしかないが、maxListは２個のパラメータリスト
   * を必要とする。
   * しかし、第２のパラメータリストは暗黙のうちに指定されるので、コンパイラはすぐには型チェックを諦めない
   * 適切な型の暗黙のパラメータを探す。
   */
  /* ２度目のorderingを省略する方法もある
   * それは、標準ライブラリで定義されている次のメソッドを使ったものだ
   */
  def implicitly[T](implicit t: T) = t
  /* implicitly[Foo]を呼び出すと、コンパイラがFoo型の暗黙の定義を探すという効果が有る
   * さらに、コンパイラはそのオブジェクトを渡してimplicitlyメソッドを呼び出し、
   * そのオブジェクトをそのまま返す
   *
   * そこで、現在のスコープでFoo型の暗黙のオブジェクトを探したいときにはいつでも
   * implicitly[Foo]と書くことができる
   *
   * たとえば、上記maxListは次のように書ける
   */
  class implictlyMaxList {
    def maxList[T](elements: List[T])(implicit  ordering: Ordering[T]): T =
      elements match {
        case List() => throw new IllegalArgumentException("empty list!")
        case List(x) => x
        case x :: rest =>
          val maxRest = maxList(rest)
          if (implicitly[Ordering[T]].gt(x, maxRest)) x
          else maxRest
    }
  }
  /* メソッド本体ではorderingパラメータの名前が１度も使われていない。
   * そのため第２パラメーターはcomparatorという名前でも良かったところだ
   * それどころかiceCreamという名前でも良かった
   *
   * def maxList[T](elements: List[T])(implicit comparator: Ordering[T]): T = ???
   * def maxList[T](elements: List[T])(implicit iceCream: Ordering[T]): T = ???
   *
   * このパターンは非常によく見られるので、Scalaはコンテキスト境界(context bound)を使って
   * パラメータ名を省略し、メソッドヘッダーを短くすることを認めている
   */
  class ContextBound {
    def maxList[T: Ordering](elements: List[T]): T =  // コンテキスト境界を使った関数
      elements match {
        case List() => throw new IllegalArgumentException
        case List(x) => x
        case x :: rest =>
          val maxRest = maxList(rest)
          if (implicitly[Ordering[T]].gt(x, maxRest)) x
          else maxRest
      }
  }
  /* [T: Ordering] という構文がコンテキスト境界であり、２つの効果がある
   *
   * ・型パラメータTを普通に導入する
   * ・Ordering[T]型の暗黙のパラメータを追加する
   *
   * パラメータがどのような名前で呼ばれるかを知っている必要は無いことが多い
   *
   * 直感的には、コンテキスト境界は、型パラメータについて何かを言おうとしているのだと考えることができる
   * [T <: Ordered[T]] と書くときには T は Ordered[T] だと言おうとしている。
   *
   * それに対し [T : Ordering] と書くときには、Tとは何かについてそこまで言おうとしているわけではない
   * Tには何らかの形の順序付け(ordering)があると言っている
   *
   * つまり、コンテキスト境界は、かなり柔軟なのである
   * コンテキスト境界を使えば、型の定義を変えることなく、順序付け(または型のほかのプロパティ)を要求することができる
   */
}
class MultiImpl {
  /** 21.7 複数の型変換を適用できるとき
   *
   * スコープの中に使える暗黙の型変換が複数含まれている場合がある。
   * そのような場合、Scalaは基本的に変換を行わない
   *
   * 暗黙の型変換のメカニズムが作動するのは、残った型変換に疑いの余地がなく、
   * 純粋に定型文となっている場合だけである。
   *
   * 複数の型変換が適用できる場合、どの組み合わせになるのかは明確にされていない
   *
   * サンプル
   * 引数としてシーケンスを取るメソッドがある
   * さらに、整数を範囲(range)に変換する暗黙の型変換、整数を数字のリストに変換する暗黙の型変換があったとする
   */
  class Sample {
    def printLength(seq: Seq[Int]) = println(seq.length)
    implicit def intToRange(i: Int) = 1 to i  // シーケンスに変換する暗黙の型変換が２つある
    implicit def intToDigits(i: Int) =
       i.toString.toList.map(_.toInt)
    // printLength(12) // <console>:15: error: type mismatch;
    //        found   : Int(12)
    // required: Seq[Int]
    // Note that implicit conversions are not applicable because they are ambiguous: // ambiguous あいまいである
    // both method intToRange of type (i: Int)scala.collection.immutable.Range.Inclusive
    // and method intToDigits of type (i: Int)List[Int]
    // are possible conversion functions from Int(12) to Seq[Int]
    //        printLength(12)
    //                    ^
    /* このような曖昧さがある場合は、プログラマは自分の意図を指定して明示的に変換しなければならない
     * Scala2.7までは、これで話は終わりだった
     *
     * Scala2.8では、このルールが緩和された、
     * 暗黙の型変換のうち１つが他よりも厳密に限定できる場合
     * コンパイラはその限定された変換を用いる
     *
     * 例えば、使えるfooメソッドの中の１つがStringを引数とし、もう１つがAnyを引数とするなら、
     * Stringを変換に使うだろう。Stringの方がAnyよりも明らかに限定的だから
     *
     * 正確には次の点が当てはままるい暗黙の型変換は、他の暗黙の方変換よりも限定的である
     *
     * ・引数の型が別の引数のサブタイプ
     * ・型変換は両方ともメソッドであり、片方のクラスがもう片方のクラスを継承している
     */
     val cba = "abc".reverse // cba: String = cba
     /* cba の型はどのように推論されるだろうか
      * 直感てきにはStringでなければ成らない
      * しかし、Scala2.7では、abcはScalaコレクションに変換されいた
      * ScalaコレクションのreverseはScalaコレクションであり、cbaの型もコレクションになっていた
      *
      * 文字列に戻す暗黙の型変換もあったが、それだけではすべての問題を解決できない
      * 例えばScala2.7までは
      * "abc" == "abc".reverse.reverse は false だったのである
      *
      * Scala2.8では、cbaの型はStrinigである。
      * Scalaコレクションへの暗黙の型変換(現在はWrappedStringと呼ばれている)は残されているが、
      * StringからStringOpsという新しい型のより限定的な型変換が追加された
      * StringOpsは、reverseなどの多数のメソッドを持っているが、コレクションではなくStringを返す
      * StringOpsへの型変換はPredefで直接定義されている
      *
      * それに対し、Scalaコレクションへの型変換は、LowPriorityImplicitsという、
      * Predefによって拡張された新しいクラスで定義されている
      *
      * 両方の型変換が選べる状況では、コンパイラはStringOpsへの変換を選択する
      * StringOpsへの変換は、「コレクションへの変換が定義されているクラス」のサブクラスで定義されているからである
      */
  }
}
class Debug {
  /** 21.8 暗黙の型変換のデバッグ
   *
   * 暗黙の型変換をデバッグするためのヒントを提供する
   *
   * 暗黙の型変換が適用されるはずなのに、コンパイラが見つけられないとき
   * 変換を明示的に書いてみる
   * それでもエラーメッセージが生成されるようなら、コンパイラが暗黙の型変換を適用できない理由を考える
   *
   * 例えば、wrapStringはStringからIndexedSeqへの変換ではなく、Listへの変換を行うものだと勘違いしてたとする
   */
  // val chars: List[Char] = "xyz"
  //  <console>:13: error: type mismatch;
  // found   : String("xyz")
  // required: List[Char]
  //        val chars: List[Char] = "xyz"{}
  //                                ^
  /* この場合、wrapStringによる変換を明示的に書くと、エラーの原因が分かりやすくなる
   */
   // val chars: List[Char] = wrapString("xyz")
   //  <console>:13: error: type mismatch;
   // found   : scala.collection.immutable.WrappedString
   // required: List[Char]
   //        val chars: List[Char] = wrapString("xyz")
   //                                          ^
  /* これを見ればwrapStringの結果型に問題があったとわかる
   * 一方、明示的に変換を挿入するとエラーが消える場合もある
   * その時は、スコープルールなどその他のルールが暗黙の型変換を適用できなくしているのだろう
   */
  /* デバッグ時には、コンパイラによって挿入された暗黙の型変換が分かれば役に立つことが有る
   * コンパイラに対して -Xprint:typer オプションを挿入すると、挿入されたものがわかる
   *
   * scala -Xprint:typer
   * を使うと対話的セルがこの入力を受け付けたあと、実際に内部で使っているソースコードが表示されるようになる
   */
}
/** 21.9 まとめ
 *
 *  暗黙の型変換は、コードを圧縮できるScalaの強力な機能である。
 *
 *  ただし、あまり頻繁に暗黙の型変換を使うと、コードがわかりにいくくなるので注意する
 *
 *  新しく暗黙の型変換を追加するときは、その前に継承、ミックスイン合成、メソッドの多重定義など、
 *  他の手段で同様の効果が得られないかどうかを自問自答した方がいい
 *
 *  他の手段ではうまくいかず、自分のコードがまだ冗長でだらだらとした感じがするようなら
 *  暗黙の型変換が役に立つかもしれない
 */

