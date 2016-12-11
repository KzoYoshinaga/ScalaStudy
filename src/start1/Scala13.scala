package start1

class Scala13 {
  /* 3.1 演算子は演算子か？
   *
   * Scalaではあらゆる印字文字を識別子に使用できる
   *
   * [使用できない文字]
   * ・括弧に使用する文字 () {} []
   * ・区切り文字  ` " ' . ; ,
   * ・上記に相当する全角文字
   *
   * [使用できる文字]
   * ・上記以外の \u0020 から \u007F に含まれる数学記号やその他の記号もしようできる
   * ・$文字は内部処理で使うので使用するべきではない
   *
   * [使用できない文字列]
   * ・予約語(キーワード)
   *
   * [識別子の命名ルール]
   * １．プレイン識別子: 文字、数字、＄、_、演算子の組み合わせ
   *     ・(_)アンダースコアは次の空白文字までを識別子の一部として扱うことをコンパイラに伝える
   *           e.g. val xyz_++= = 1 は xyz_++= という変数に 1 を代入する
   *        val xyz++= = 1 はコンパイルできない
   *     ・(_)アンダースコアの後に演算子記号がある場合、文字と数字を混ぜることはできない
   *           e.g. abc_=123 は abc_ と言う変数に 123 を入れるのか abc_=123 と言う識別子なのか判断できない
   *
   * ２．プレイン識別子: 演算子
   *     ・演算子文字から始まる識別子の場合、他の文字も演算子でなければならない
   *           e.g. val ++= = 1  適格
   *                val +2= = 1  コンパイルエラー
   *
   * ３．(`)バッククォート
   *     ・バッククォートではさむことにより(プラットフォームの制約に従った)任意の文字列を指定できる
   *       Javaや.NETのクラスメソッド名がScalaの予約語と同じ場合、この方法で呼び出せる
   *       e.g. val `this is a valid identifier` = "Hello World!"
   *
   * ４．パターンマッチ識別子
   *     ・パターンマッチ式では次のように構文解析される
   *       変数の識別子: 小文字で始まるトークン
   *       定数の識別子: 大文字で始まるトークン
   */

  /* 3.1.1 糖衣構文(Syntax sugar)
   *
   * Scalaの命名規則にしたがっていれば、Scalaを自然に拡張したように見えるライブラリが作れる
   * ただし、直感に反するメソッド命名はしないこと(した方が面白いけど)
   */

  /* 3.2 丸括弧やドットのないメソッド
   */

   // ・引数のないメソッドは丸括弧なしで定義できる
   def doNothing {}
   //   呼び出す側は丸括弧なしで呼び出す必要がある
   def useDoNothing {
     doNothing
     // doNothing()  // エラー
   }

   // ・空の丸括弧をつけて定義した場合
   def doNothing_?() {}
   //   呼び出す側は丸括弧を付けても付けなくてもいい
   def useDoNothing_? {
     doNothing_?
     doNothing_?()
   }

   // e.g. Listクラスのsizeメソッド
   def listSize {
     val list = List[Int](0,1,2)
     list.size
     // list.size()  // エラー
   }

   // e.g. java.lang.String のlengthメソッド
   def javaStringLength {
     val string = new java.lang.String("abc")
     string.length   // 適格 空の丸括弧で定義しているため
     string.length()
   }

   // Q. パブリックなメンバ変数と同じ名前の場合は？
   def testLength {
     val test = new Test();
     print(test.length)   // メンバ変数を参照している
     print(test.length())
   }

   // 慣習として副作用のないメソッド呼び出しの場合は丸括弧を省略する
   // 副作用がない -> 値と同じに扱える

   // ・引数なしのメソッドや、引数が１つだけのメソッド呼び出しには(.)ドットを省略できる
   def isEven(n:Int) = (n%2)==0
   def plusOne(n:Int) = n + 1
   def noDot {
     List(0,1,2) size  // 引数がない List の sizeメソッド

     // 遅延処理なのでスタティックに使える
     // インスタンス化されないと評価されない
     List(0,1,2,3,4,5,6,7) filter isEven foreach println // 0 2 4 6

     List(0,1,2,3,4,5,6,7) map plusOne foreach println // 1 2 3 4 5 6 7

     // 即値処理なのでインスタンスが必要
     println(new Scala13 plusOne 57) // 58 インスタンスメソッド？
   }

   /* 3.2.1 優先順位
    * https://www.scala-lang.org/files/archive/spec/2.11/01-lexical-syntax.html
    * 優先順位の低いものから並べてみる[ScalaSpec2009]
    *
    * 1. 全ての文字
    * 2. |
    * 3. ^
    * 4. &
    * 5. < >
    * 6. = !
    * 7. :
    * 8. + -
    * 9. * / %
    * 10.その他の全ての特殊文字
    *
    * [例外]
    * ・(=)が代入に使用されるときは優先順位が一番低くなる
    */
   // e.g. 等価な計算式 左から順番に結合される(左結合)
   def calc {
     val calc1 = 2.0 * 4.0 / 3.0 * 5.0
     val calc2 = (((2.0 * 4.0) / 3.0) * 5.0)
   }
   // Scalaでは(:) で終わるドットなしメソッドは右側に束縛される
   // それ以外のメソッドは左側に束縛される
   def plusTwo_:(n:Int) =  n + 2;
   def rightBind {
     val value = 2
     println(value plusTwo_: new Scala13)    // 右側から順に結合される
     println((new Scala13).plusTwo_:(value)) // 等価
   }
   // :: コンストラクタメソッド : で終わっているので右バインド
   def cons {
     val list = List('c', 'd')   // List('c', 'd')
     'b' :: list                 // List('b', 'c', 'd')
     list.::('a')                // List('a', 'b', 'c', 'd')
   }
   // 右バインドと左バインドが混在している場合
   def mix {
     val list = List('c', 'd')
     'a' :: list.::('b') ++ List('e', 'f') // List('a', 'b', 'c', 'd', 'e', 'f')
   }
   // Scalaではメソッド名の先頭文字によって優先順位を決めている
   // 上記の例では + は : よりも優先順位が高いので
   // list.::('b') -> list ++ List('e', 'f') -> 'a' :: list
   // の順番で評価される

   // 順序の検証
   def mix1_:(test:Scala13):Scala13 = { println("mix1"); test }
   def mix2(test:Scala13):Scala13   = { println("mix2"); test }
   def +:(test:Scala13):Scala13 =     { println("mix3"); test }
   def :+:(test:Scala13):Scala13 =     { println("mix4"); test }
   def mixText {
     val test = new Scala13
     test :+: test +: test.mix2(test) mix1_: test // mix2 -> mix3 -> mix4 -> mix1
     test +: test mix1_: test :+: test.mix2(test) // mix3 -> mix2 -> mix4 -> mix1
    }
}
