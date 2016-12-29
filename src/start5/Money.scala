package start5

/** 20.10 ケーススタディ：通貨計算
 *
 * 目的：Currencyクラスを設計する
 *
 * Currencyのインスタンスは、ドル、ユーロ、円などの通過を単位とする金額を表している
 * この金額は計算に使えるものでなくてはならない。
 * e.g. 同じ通貨の２つの金額を加える、金額に利子率を表す係数を乗算すると言ったことができる
 */

/* 通貨クラスの最初の設計
 */
class FirstDesign {
  abstract class Currency {
    val amount: Long
    def designation: String  // 通貨単位を表す文字列
    override def toString = amount + " " + designation
    def + (that: Currency): Currency = { ??? }
    def * (x: Double): Currency = { ??? }
  }
  /* toStringメソッドは以下のような文字列を生成する
   * 79 USD
   * 11000 Yen
   * 99 Euro
   */
  // このように生成する
  new Currency {
    val amount = 79L
    def designation = "USD"
  }
  /* 複数の通貨単位で表された金額を扱おうとすると使えなくなる
   */
  abstract class Dollar extends Currency {
    def designation = "USD"
  }
  abstract class Euro extends Currency {
    def designation = "Euro"
  }
  /* 加算の結果はCurrencyになるだろうが、ドルとユーロを混ぜた計算になる
   * Dollarの＋メソッドはDollarを、Euroの＋メソッドはEuroを引数に取り、またそれぞれの型で結果値を返すようにしたい
   *
   * しかし、加算メソッドの記述は１回だけにしたい
   */
}
class SecondDesign {
  abstract class AbstractCurrency {
    type Currency <: AbstractCurrency
    val amount: Long
    def designation: String
    override def toString = amount + " " + designation
    def + (that: Currency): Currency = { ??? }
    def * (x: Double): Currency = { ??? }
  }
  abstract class Dollar extends AbstractCurrency {
    type Currency = Dollar
    def designation = "USD"
  }
  /* ＋メソッドの右辺はどのように記述するべきか？
   *
   * def + (that: Currency): Currency = new Currency // error
   *
   * Scalaの抽象型は、インスタンスを作ったり、他のクラスのスーパ型には出来ない
   *
   * ファクトリーメソッドを使うとこの制限を回避できる
   */
}
class Fact {
   abstract class AbstractCurrency {
    type Currency <: AbstractCurrency
    def make(amount: Long): Currency // ファクトリーメソッド
    val amount: Long
    def designation: String
    override def toString = amount + " " + designation
    def + (that: Currency): Currency = { ??? }
    def * (x: Double): Currency = { ??? }
  }
  abstract class Dollar extends AbstractCurrency {
    type Currency = Dollar
    def designation = "USD"
  }
  /* このような設計は動作するところまで発展させられないわけではないが
   * 疑わしい部分を持っている。
   *
   * なぜAbstractCurrencyクラスの中にファクトリメソッドが配置されているのだろうか？
   * 疑わしさの理由は少なくとも２つある
   *
   * １．次のようなコードを使えば同じ通過の金額を次々に作れてしまう
   *
   *  myDollar.make(100) // 100ドル増えた
   *
   * ２．Currencyオブジェクトへの参照があれば、Currencyオブジェクトを次々に作れるが
   *     最初のCurrencyオブジェクトを手に入れるにはどうすればよいだろうか？
   *     makeと基本的に同じことをする別の作成メソッドが必要になる。
   *     そこで、コードをコピーすることになるが、これは怪しげなコードに見られる確かな兆候である
   *
   * 正解は抽象型とファクトリーメソッドをAbstractCurrencyクラスの外に出すこと
   * AbstractCurrencyクラス、Currencyクラス、makeファクトリーメソッドを含む別のクラスを作る
   */
}
class refact {
  abstract class CurrencyZone {
    type Currency <: AbstractCurrency
    def make(x: Long): Currency
    abstract class AbstractCurrency {
      val amount: Long
      def designation: String
      override def toString = amount + " " + designation
      def + (that: Currency): Currency =
        make(this.amount + that.amount)
      def * (x: Double): Currency =
        make((this.amount * x).toLong)
    }
  }
  // CurrencyZoneの具象クラスの例としてUSクラスを定義する
  object US extends CurrencyZone {
    abstract class Dollar extends AbstractCurrency {
      def designation = "USD"
    }
    type Currency = Dollar
    def make(x: Long) = new Dollar { val amount = x }
  }
  /* この通貨圏の通貨は、US.Dollarである
   * USオブジェクトは、Currency型をDollarの別名として固定し
   * makeファクトリーメソッドに指定されたドル建ての金額を返す実装を与えている
   *
   * ・補助通貨単位の実装: 例えばドルにはセントという補助通貨単位
   * セントをもっとも素直にモデリングする方法は、US.Currencyのamountフィールドをドル
   * では無くセントを単位として表現するというもの
   * ドル表記に戻せるように、CurrendcyZoneクラスにCurrencyUnitフィールドを導入し
   * 標準単位が補助単位のいくつ分に相当するかを管理すると便利
   */
}
class refact2 {
  abstract class CurrencyZone {
    type Currency <: AbstractCurrency
    def make(x: Long): Currency
    val CurrencyUnit: Currency   // 抽象標準通貨単位
    abstract class AbstractCurrency {
      val amount: Long
      def designation: String
      // formattedメソッドは、右被演算子として渡された生計文字列に従って
      // 元の文字列を整形した結果の文字列を返す(JavaのString.formatメソッドと同じ形式)
      override def toString = ((amount.toDouble / CurrencyUnit.amount.toDouble) // 標準通貨単位で表す
          formatted ("%." + decimals(CurrencyUnit.amount) + "f") + " " + designation) // %.2fだと小数点2桁の表記
      def + (that: Currency): Currency =
        make(this.amount + that.amount)
      def * (x: Double): Currency =
        make((this.amount * x).toLong)
      private def decimals(n: Long): Int =
        if (n == 1) 0 else 1 + decimals(n / 10) // １０進数の桁数から１を引いた整数を返す
    }
  }
  // CurrencyZoneの具象クラスの例としてUSクラスを定義する
  object US extends CurrencyZone {
    abstract class Dollar extends AbstractCurrency {
      def designation = "USD"
    }
    type Currency = Dollar
    def make(cents: Long) = new Dollar { val amount = cents } // セント単位で総量を記憶
    val Cent = make(1)
    val Dollar = make(100) // 1ドル＝１００セント
    val CurrencyUnit = Dollar // 標準通貨単位がDollar(値)であることを示す
  }
  // EUの通貨圏
  object Europe extends CurrencyZone {
    abstract class Euro extends AbstractCurrency {
      def designation = "EUR"
    }
    type Currency = Euro
    def make(cents: Long) = new Euro {
      val amount = cents
    }
    val Cent = make(1)
    val Euro = make(100)
    val CurrencyUnit = Euro
  }
  // 日本の通貨圏
  object Japan extends CurrencyZone {
    abstract class Yen extends AbstractCurrency {
      def designation = "Yen"
    }
    type Currency = Yen
    def make(yen: Long) = new Yen {
      val amount = yen
    }
    val Yen = make(1)
    val CurrencyUnit = Yen
  }
}
/* 二段階目の改良としてこのモデルに通貨換算機能を追加する
 */
class refact３ {
  abstract class CurrencyZone {
    type Currency <: AbstractCurrency
    def make(x: Long): Currency
    val CurrencyUnit: Currency
    abstract class AbstractCurrency {
      val amount: Long
      def designation: String
      override def toString = ((amount.toDouble / CurrencyUnit.amount.toDouble)
          formatted ("%." + decimals(CurrencyUnit.amount) + "f") + " " + designation)
      def + (that: Currency): Currency =
        make(this.amount + that.amount)
      def * (x: Double): Currency =
        make((this.amount * x).toLong)
      def - (that: Currency): Currency =
        make(this.amount - that.amount)
      def / (that: Double): Currency =
        make((this.amount / that).toLong)
      def / (that: Currency): Double =
        this.amount.toDouble / that.amount
      private def decimals(n: Long): Int =
        if (n == 1) 0 else 1 + decimals(n / 10)
      // 入れ子マップになったレート表から換算率を得る ****
      def from(other: CurrencyZone#AbstractCurrency): Currency =
        make(math.round(other.amount.toDouble * Converter.exchangeRate(other.designation)(this.designation)))
    }
  }
  // CurrencyZoneの具象クラスの例としてUSクラスを定義する
  object US extends CurrencyZone {
    abstract class Dollar extends AbstractCurrency {
      def designation = "USD"
    }
    type Currency = Dollar
    def make(cents: Long) = new Dollar { val amount = cents }
    val Cent = make(1)
    val Dollar = make(100)
    val CurrencyUnit = Dollar
  }
  // EUの通貨圏
  object Europe extends CurrencyZone {
    abstract class Euro extends AbstractCurrency {
      def designation = "EUR"
    }
    type Currency = Euro
    def make(cents: Long) = new Euro {
      val amount = cents
    }
    val Cent = make(1)
    val Euro = make(100)
    val CurrencyUnit = Euro
  }
  // 日本の通貨圏
  object Japan extends CurrencyZone {
    abstract class Yen extends AbstractCurrency {
      def designation = "JPY"
    }
    type Currency = Yen
    def make(yen: Long) = new Yen {
      val amount = yen
    }
    val Yen = make(1)
    val CurrencyUnit = Yen
  }
  // 換算レートの対応表を格納したConverterオブジェクト ****
  // 内部表現の通貨(ドル、ユーロならcent)との為替率になる
  object Converter {
    val exchangeRate = Map(
        "USD" -> Map("USD" -> 1.0, "EUR" -> 0.7596,
                     "JPY" -> 1.211, "CHF" -> 1.223),
        "EUR" -> Map("USD" -> 1.316, "EUR" -> 1.0,
                     "JPY" -> 1.594, "CHF" -> 1.623),
        "JPY" -> Map("USD" -> 0.8257, "EUR" -> 0.6272,
                     "JPY" -> 1.0, "CHF" -> 1.018),
        "CHF" -> Map("USD" -> 0.8108, "EUR" -> 0.6160,
                     "JPY" -> 0.982, "CHF" -> 1.0))
  }
  object Main extends App {
    val yen = Japan.Yen from US.Dollar * 100
    // yen: Japan.Currency = 12110 JPY
    val euro = Europe.Euro from yen
    // euro: Europe.Currency = 75.95 EUR
    val doll = US.Dollar from euro
    // doll: US.Currency = 99.95 USD

    // 3回の変換の後にほぼ同じ額になる

    // 同じ通貨単位の金額を加算することも出来る
    US.Dollar * 100 + doll
    // res6: US.Currency = 199.95 USD

    // 異なる通貨単位では加算できない
    // US.Dollar + Europe.Euro
    // <console>:15: error: type mismatch;

    /* 型の抽象化は、単位(この場合は通貨単位)の異なる２つの値の加算を禁止するという使命を果たしている
     *
     * 異なる単位間の変換を忘れるということが、多くの深刻なシステムエラーを引き起こしてきた実績を持っている
     * e.g. 1999/9/23 火星探査機マーズクライメイトオービターの衝突事故
     *      メートル法を使っているエンジニアチームと、ヤードポンド法を使っているチームが混在していたため起きた
     */
  }
}
/** まとめ
 *
 * ・メソッド
 * ・値(val)
 * ・変数(var)
 * ・型(type)
 * を抽象化できる(実装の責任をサブクラスに任せる)
 *
 * まだ分からないものすべてのものを抽象メンバーにして、クラスを設計せよ。
 *
 * すると、型システムがモデルの発展を導いてくれる
 */

class Money {

}





































