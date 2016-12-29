package start5

/** 20章 抽象メンバー
 *
 * Scalaでは抽象メンバーという考え方に十分な普遍性を与えている
 * つまり、抽象メソッドだけでな、クラスやトレイトのメンバーとしての
 * 抽象フィールド、更に抽象型というようなものまで宣言出来る
 *
 * 本章ではすべての抽象メンバー
 *
 * ・val
 * ・var
 * ・メソッド
 * ・型
 *
 * の４種類を説明する。その過程で
 *
 * ・事前初期化済みフィールド
 * ・遅延評価val
 * ・パス依存型
 * ・列挙
 *
 * も取り上げる
 */
/**
 * 20.1 抽象メンバーの弾丸ツアー
 */
class Abstracts {
  trait Abstract {
    type T  // 抽象型
    def transform(x: T): T  // 抽象メソッド
    val initial: T  // 抽象val
    var current: T  // 抽象var
  }
  /* Abstractトレイトの具象実装では、抽象メンバーの定義を１つ１つ埋めていかなければならない
   */
  class Concrete extends Abstract {
    type T = String
    def transform(x: String) = x + x
    val initial = "hi"
    var current = initial
  }
}
/**
 * 20.2 型メンバー
 *
 * 抽象型(abstract types)
 *
 * trait Abstract {
 * 		type T
 * }
 *
 * class Concrete extends Abstract {
 * 		type T = String
 * }
 *
 * ConcreteクラスのT型のような非抽象(具象)型メンバーは、型の新しい名前、別名を定義する手段と考えることができる
 *
 * ・本当の型名が長くてわかりにくいときに、短くてもっとわかりやすい別名を定義すること
 * ・サブクラスで定義しなければならない抽象型を宣言すること
 */

/**
 * 20.3 抽象val
 *
 * trait Abstract {
 * 		val initial: String
 * }
 *
 * class Concrete extends Abstract {
 * 		val initial = "Hi!"
 * }
 *
 * 抽象val宣言を使うのは、そのクラスでは何が正しい値なのか分からないが、個々のインスタンスでは
 * 変更できない値を持つことが分かっているとき
 *
 * 抽象valは、その実装に対して制限を課す、と言い換えられる
 * 実装はval定義でなければならず、varやdefであってはならない
 *
 * それに対し、抽象メソッド宣言は、具象メソッド定義で実装しても、具象val定義で実装してもかまわない
 */
class AbVal {
  abstract class Fruit {
    val v: String // value
    def m: String // method
  }
  abstract class Apple extends Fruit {
    val v: String
    val m: String // ok: 'def'を'val'でオーバライドすることは認められる
  }
  abstract class BadApple extends Fruit {
    def v: String // error: overriding value v in class Fruit of type String;
    def m: String
  }
}

/**
 * 20.4 抽象var
 *
 * trait Abstract {
 * 		var current: String
 * }
 *
 * class Concrete extends Abstract {
 * 		var current = "Hello"
 * }
 *
 * クラスメンバーとして宣言されたvarには、自動的にゲッター/セッターが与えられる
 * 抽象varにも同じことが当てはまる
 *
 * 抽象ゲッタメソッドのcurrentと、抽象セッタメソッドのcurrent_=を暗黙のうちに宣言したことになる
 * しかし再代入可能のフィールドは具象実装を定義するまで作られない
 *
 * trait AbstractTime {
 * 		var hour: Int
 * 		var minute: Int
 * }
 */
class AbVar {
  trait AbstractTime {
    def hour: Int        // hourのゲッタ
    def hour_=(x: Int)   // hourのセッタ
    def minute: Int      // minuteのゲッタ
    def minute_=(x: Int) // minuteのセッタ
  }
}

/**
 * 20.5 抽象valの初期化
 *
 * トレイトにパラメータを与えるという考え方は、抽象valで実現できる
 */
class iniVal {
  trait RationalTrait {
    val numerArg: Int
    val denomArg: Int
  }
  /* このトレイトの具象インスタンスを生成するには
   * 抽象valの定義を実装しなければならない
   */
  new RationalTrait {
    val numerArg = 1
    val denomArg = 2
  }
  /* この式は、トレイトをミックスインし、本体で定義されている無名クラス(anonymous class)の
   * インスタンスを生成する
   *
   * new Rational(1,2)と言うインスタンス生成と同じような効果があるが、式が初期化される順序に
   * 微妙な違いがある
   *
   * new Rational(expr1, expr2)
   *
   * Rationalクラスが初期化されるあめに、expr1とexpr2の２つの式が評価され、
   * そこから得られたexpr1とexpr2の結果値がRationalクラスの初期化に使われる
   *
   * しかし、トレイトの場合はこの順序が逆になる
   *
   * new RationalTrait {
   * 		val numerArg = expr1
   * 		val denomArg = expr2
   * }
   *
   * expr1とexpr2の２つの式は、無名クラスの初期化処理の一部として評価されるが、
   * 無名クラスはRationalTraitの後に初期化される
   *
   * そのため、numerArgやdenomArgはRationalTraitの初期化中は使えない
   * 正確にはInt型のデフォルト値である０が生成される
   *
   * 次のような場合ではこれが問題になる
   */
}
class problemful {
  trait RationalTrait {
    val numerArg: Int
    val denomArg: Int
    require(denomArg != 0)
    private val g = gcd(numerArg, denomArg)
    val numer = numerArg / g
    val denom = denomArg / g
    private def gcd(a: Int, b: Int): Int =
      if (b == 0) a else gcd(b, a % b)
    override def toString = numer + "/" + denom
  }

  val x = 2
  new RationalTrait {
    val numerArg = 1 * x
    val denomArg = 2 * x
  }
  // java.lang.IllegalArgumentException: requirement failed
}
/* このサンプルではクラスパラメータと抽象フィールドでは初期化の順序が異なることを示している
 *
 * クラスパラメータは、クラスコンストラクタに渡される前に評価される(名前渡しを除く)
 * それに対し、サブクラスでのval定義の実装は、スーパクラスが初期化された後に始めて評価される
 *
 * 初期化されてフィールドによるエラーを回避し、確実に初期化できるRationalTraitを定義できるか？
 *
 * ・事前初期化済みフィールド(pre-initialized fields)
 * ・遅延評価(lazy evaluation)val
 *
 * の２つによって、Scalaはこの問題に対処する
 */
/* 20.5.1 事前初期化済みフィールド
 *
 * スーパクラスが呼び出される前にサブクラスのフィールドを初期化できるようにするもの
 * そのためには、スーパクラスの呼び出しより前にフィールド定義を書けばよい
 */
class PreIni {
  trait RationalTrait {
    val numerArg: Int
    val denomArg: Int
    require(denomArg != 0)
    private val g = gcd(numerArg, denomArg)
    val numer = numerArg / g
    val denom = denomArg / g
    private def gcd(a: Int, b: Int): Int =
      if (b == 0) a else gcd(b, a % b)
    override def toString = numer + "/" + denom
  }
  // 無名クラス式に含まれている事前初期化済みフィールド
  val x = 2
  new {
    val numerArg = 1 * x
    val denomArg = 2 * x
  } with RationalTrait
  // RationalTrait = 1/2

  /* 事前初期化済みフィールドは、無名クラスだけではなく、オブジェクトや名前付きサブクラスでも使える
  */
  // オブジェクトに含まれている事前初期化済みフィールド
  object twoThirds extends {
    val numerArg = 2
    val denomArg = 3
  } with RationalTrait

  // クラス定義に含まれている事前初期化済みフィールド
  class RationalClass(n: Int, d: Int) extends {
    val numerArg = n
    val denomArg = d
  } with RationalTrait {
    def + (that: RationalClass) = new RationalClass(
        numer * that.denom + that.numer * denom,
        denom * that.denom)
  }
  /* 事前初期化済みフィールドは、スーパクラスのコンストラクタが呼び出される前に初期化される。
   * したがって、これらの初期化子は、構築中のオブジェクトを参照できない。
   *
   * このような初期化子がthisを参照している場合、参照先は構築中のオブジェクト自体ではなく、
   * 構築中のオブジェクトや、クラスを包含しているオブジェクトになる
   */
  new {
    val numerArg = 1
    // val denomArg = this.numerArg * 2 // numerArg is not a menber of PreIni
  } with RationalTrait
  /* この点において、事前初期化済みフィールドはコンストラクター引数と同じように振舞う
   *
   */
}
/* 20.5.2 遅延評価val
 *
 * 事前初期化済みフィールドは、クラスコンストラクタ引数の初期化動作を正確にシミュレートするのに使える
 * しかし、初期化の順序をシステム自身に考えさせたい場合がある。
 * それはval定義を遅延評価させれば実現できる
 *
 * val定義の前に lazy修飾子 をつけると、右辺の初期化式は、valが始めて使われるまで評価をしない
 */
class Lazy extends App {
  object Demo {
    val x = { println("initializing x"); "done" }
  }
  Demo
  // initializing x
  // Demo.type = Demo$@3d59da32
  Demo.x
  // String = done

  /* Demoを参照したときに、そのフィールドであるxも初期化されている。
   * xの初期化はDemoの初期化の一部である
   */

  object lazyDemo {
    lazy val x = { println("initializing x"); "done" }
  }
  lazyDemo
  // lazyDemo.type = lazyDemo$@1906a909
  lazyDemo.x
  // initializing x
  // String = done

  /* x の初期化は、始めて x が参照されるまで先延ばしにされる
   * これは def を使って x をパラメータなしメソッドとして定義したときと似ている
   *
   * しかし def とは違い、遅延評価valは２回以上評価されることはない
   */
}
/* 遅延評価valを使ったRationalTraitの書き直し
 */
class LazyRational {
  trait LazyRationalTrait {
    val numerArg: Int
    val denomArg: Int
    lazy val numer = numerArg / g
    lazy val denom = denomArg / g
    override def toString = numer + "/" + denom
    private lazy val g = {
      require(denomArg != 0)
      gcd(numerArg, denomArg)
    }
    private def gcd(a: Int, b: Int): Int =
      if (b == 0) a else gcd(b, a % b)
  }
  /* この新しいトレイトの定義では、すべての具象フィールドの定義にlazyが付いている。
  * すべての初期化コードが遅延評価valの右辺に移った
  */
  val x = 2
  new LazyRationalTrait {
    val numerArg = 1 * x
    val denomArg = 2 * x
  }
  // LazyRationalTrait = 1/2
}
/* 遅延valを使えば、事前初期化は不要
 * 上記コードで1/2と言う文字列が表示されるまでの初期化シーケンスをトレースする
 *
 * ①．LazyRationalTraitの新しいインスタンスが生成され、LazyRationalTraitの初期化コードが実行される。
 *     この初期化コードは空であり、LazyRationalTraitのフィールドは、まだどれ１つとして初期化されていない
 *
 * ②．new式によって定義された無名サブクラスの基本コンストラクタが実行される。
 *     こうするとnumerArgが2、denomArgが4で初期化される
 *
 * ③．インタープリターにより構築されたオブジェクトのtoStringメソッドが呼び出され、結果値が表示される
 *
 * ④．numerフィールドがLazyRationalTraitのtoStringメソッドによって初めてアクセスされ、
 *     そのためnumerの初期化子が評価される
 *
 * ⑤．numerの初期化子は、非公開フィールドの g にアクセスする。そこで g が評価される。
 *     評価の過程で②で定義されたnumerArgとdenomArgへのアクセスが発生する
 *
 * ⑥．toStringメソッドがdenomにアクセスし、そのためdenomが評価される。
 *     denomの評価の過程でdenomArgと g へのアクセスが起きるが、g フィールドは既に⑤で評価されたので、
 *     初期化子が改めて評価されることはない
 *
 * ⑦．結果値の"1/2"という文字列が構築され、表示される
 */
/* g の定義がLazyRationalTraitのテキストとしてはnumer, denomの後ろに書かれていることに注目する
 * しかし、これら３つは遅延評価されるので、numer, denomの初期化が完了するまえに g が初期化される
 *
 * 値がオンデマンドで初期化されるので、定義のテキスト上での順序は実行順に影響を与えない
 * そのため、遅延評価valは
 *
 * 「必要なときに必要となるフィールドが全て定義されているように、val定義の配置を真剣に考える」
 * と言う作業から開放してくれる
 *
 * しかし、このメリットが得られるのは、遅延評価valの初期化が副作用を起こさず
 * 副作用に依存していないと言う前提が満たされているときだけ
 *
 * 遅延評価valは、全てが最終的に初期化されてる限り、初期化の実行順が意味を持たない関数型オブジェクトに向いている
 * 命令型にはあまり適さない
 */

/** 20.6 抽象型
 *
 * 他の抽象宣言と同様に、抽象型宣言は、サブクラスで具体的な定義を行うためのプレースホルダーである
 */
class AbTySam {
  // 抽象型が自然に姿を見せる例
  class Food
  abstract class Animal {
    def eat(food: Food)
  }
  // 上記抽象クラスの特化
  class Grass extends Food
  class Cow extends Animal {
    override def eat(food: Grass) = {} // これではコンパイルできない
  }
  //  method eat in class Animal of type (food: Food)Unit is not defined
  /* サブクラスでメソッドのパラメータを特化できないのは厳格に過ぎるか？
   * クラスがそのように書けてしまったら、すぐに次のような危険な状況に陥る
   */
  class Unsafe {
    abstract class Animal {
      def eat(food: Food)
    }
    class Grass extends Food
    class Cow extends Animal {
      override def eat(food: Grass) = {} // もしこれがコンパイルできたとすると
    }
    class Fish extends Food
    val bessy: Animal = new Cow
    bessy eat (new Fish) // 牛に魚を食べさせることになる
  }

  // 抽象型で適切な餌をモデリングする
  class AbTy {
    class Food
    abstract class Animal {
      type SuitableFood <: Food
      def eat(food: SuitableFood)
    }
    // サブクラスでの抽象の実装
    class Grass extends Food
    class Cow extends Animal {
      type SuitableFood = Grass
      override def eat(food: Grass) = {}
    }
    // このクラス定義のもとで、「魚を食べる牛」と言う反例を実行
    class Fish extends Food
    val bessy: Animal = new Cow
    // bessy eat (new Fish) // type mismatch
  }

  /** 20.7 パス依存型
   *
   * required: bessy.SuitableFood
   *    bessy eat (new Fish)
   *               ^
   * 「bessyが参照しているオブジェクトのメンバーであるSuitableFood型」
   * このような型は、パス依存型(path-dependeent type)と呼ばれる
   *
   * この場合の「パス」は、オブジェクト参照のことである
   *
   * 一般にパスが異なれば、異なる型が参照される
   */
  class pathDep {
    class Food
    abstract class Animal {
      type SuitableFood <: Food
      def eat(food: SuitableFood)
    }
    class Grass extends Food
    class Cow extends Animal {
      type SuitableFood = Grass
      override def eat(food: Grass) = {}
    }
    class DogFood extends Food
    class Dog extends Animal {
      type SuitableFood = DogFood
      override def eat(food: DogFood) = {}
    }
    val bessy = new Cow
    val johny = new Cow
    val lassie = new Dog
    johny eat (new bessy.SuitableFood) // ok
    // lassie eat (new bessy.SuitableFood) // type mismatch
  }
  /* パス依存型はJavaの内部クラス型に似ているが、大きな違いがある
   *
   * パス依存型は外部オブジェクトに名前を与えるのに対し
   * 内部クラス型は外部クラスに名前を与える
   *
   * Javaスタイルの内部クラス型はScalaでも表現できるが書き方が異なる
   */
  class InOut {
    // 次の２つのクラスについて考える
    class Outer {
      class Inner
    }
    // ScalaではOuter#Innerでアクセスする
    val o1 = new Outer
    val o2 = new Outer
    /*
     * ここでo1.Innerとo2.Innerは２つの異なるパス依存型である
     * そして、これらは異なる型である
     *
     * Outer#Innerは、Outer型の任意の外部オブジェクトが持つInnerクラスを表す
     *
     * o1.Innerとo2.Innerの型は、より一般的なOuter#Innerに対して互換性がある
     * (つまりサブ型になっている)
     *
     * しかし、o1.Innerは、特定の外部オブジェクト(o1が参照しているもの)のInnerクラスを参照している
     * 同様に、o2.Innerは、別の外部オブジェクト(o2が参照しているもの)のInnerクラスを参照している
     *
     * Scalaでは、Javaと同じように、内部クラスインスタンスは、自分を包含している外部クラスインスタンス
     * への参照を持っている。そのため、内部クラスは、たとえば外部クラスのメンバーにアクセスできる
     *
     * したがって、何らかの形で外部クラスインスタンスを指定しなければ、内部クラスのインスタンスを
     * 作ることはできない
     *
     * １つのやりかたは、外部クラスの本体内で内部クラスのインスタンスを作るようにすればいい
     * この場合、現在の外部クラスインスタンス(thisで参照されるもの)が使われる
     *
     * 別のやり方として、パス依存型を使う方法もある。
     */
    new o1.Inner
    /* このようにして作られた内部オブジェクトは、o1から参照できるオブジェクト(外部オブジェクト)への参照を持っている
     * それに対しOuter#Inner型はOuterの特定のインスタンスを指定していないので、そのインスタンスを作る
     * ことができない
     */
    // new Outer#Inner // error: Outer is not a legal prefix for a constructor
  }

  /** 20.8 リファインメント型
   *
   * クラスが別のクラスを継承するとき、第１のクラスは、第２のクラスの名目的(nominal)だと言われる
   * 関係する２つの型はそれぞれ名前を持っており、それらの名前にはサブ型関係があるということが
   * 明示的に宣言されているからである
   *
   * Scalaは、その他に構造的(structural)サブ型をサポートする
   * これは、２つの型が互換性のあるメンバーを持つためにサブ型関係になる、と言うもの
   * Scalaで構造的サブ型を指定するにはリファインメント型(refinement type)を使う
   *
   * 型にメンバー以外追加するものがない場合に適している
   *
   * e.g.
   * 草食動物が含まれるPasture(牧草地)クラスを定義したい場合
   * AnimalThatEatsGrassトレイトを定義して該当する全てのクラスにミックスインするのも１つの方法だが冗長
   *
   * AnimalThatEatsGrassを定義する代わりに、リファインメント型を使用できる
   */
  class Pasture {
    var animals: List[Animal { type SuitableFood = Grass}] = Nil
  }

  /** 20.9 列挙(enumeration)
   *
   * Scalaでは標準ライブラリにscala.Enumerationクラスを拡張するオブジェクトを定義する
   */
  object Color extends Enumeration {
    val Red = Value
    val Green = Value
    val Blue = Value
  }
  // まとめて定義
  object Color2 extends Enumeration {
    val Red, Green, Blue = Value
  }
  // Colorに含まれるすべてのものをインポート
  import Color._
  /* Enumerationは、Valueという名前の内部クラスを定義しており、同名のパラメータなし
   * メソッドValueがそのクラスの新しいインスタンスを返す
   *
   * そのためColor.Redなどの値はColor.Value型になる
   *
   * Color.Valueは、Colorオブジェクトで定義された全ての列挙値の型
   * これはパス依存で、Colorがパス、Valueが依存型である
   *
   * この型が他のすべての型と全く異なる新しい型である点が重要
   */
  object Direction extends Enumeration {
    val North, East, South, West = Value
  }
  /* このときDirection.ValueとColor.Valueはパス部分が異なるので、
   * まったく違う型になる
   */
  /* このように多重定義されているValueメソッドを使えば、名前と列挙値を結びつけることができる
   */
  object Direction2 extends Enumeration {
    val North = Value("North")
    val East = Value("East")
    val South = Value("South")
    val West = Value("West")
  }
  /* valuesメソッドで返されたセットを使って反復できる
   */
  for (d <- Direction2.values) print(d + " ") // North East South West
  /* 定義された順番に０から番号が与えられる
   */
  Direction2.East.id  // Int = 1
  /* これは逆方向にも使える
   */
  Direction2(1)  // East
}






























