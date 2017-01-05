package start23
/** 第２３章 for式の再説
 *
 * map、flatMap、filterなどの高階関数はリストを操作するための強力な構文要素
 * しかし抽象レベルが高いので、プログラムが少し分かりにくくなる
 *
 * 例を使って考えてみる
 */
class Sample {
  case class Person(name: String, isMale: Boolean, children: Person*)
  // サンプルリスト
  val lara = Person("Lara", false)
  val bob = Person("Bob", true)
  val julie = Person("Julie", false, lara, bob)
  val persons = List(lara, bob, julie)
  // リストから、母親と子供のペアを拾い出して名前を表示したいとする
  val pairs = persons filter (p => !p.isMale) flatMap (p => (p.children map (c => (p.name, c.name))))
  // pairs: List[(String, String)] = List((Julie,Lara), (Julie,Bob))

  /* このサンプルは、filterの代わりにwithFilterを使えば少し最適化される
   * こうすれば、女性データを集めた中間データ構造の生成が回避される
   */
  val pairs2 = persons withFilter (p => !p.isMale) flatMap (p => (p.children map (c => (p.name, c.name))))
  //  pairs2: List[(String, String)] = List((Julie,Lara), (Julie,Bob))

  /* このクエリは書くのも読むのもお手軽というわけにはいかない
   * もっと簡単な方法がある
   *
   * for式を使えば同じサンプルが次のように書ける
   */
  val pair3 = for (p <- persons; if !p.isMale; c <- p.children) yield (p.name, c.name)
  //  pair3: List[(String, String)] = List((Julie,Lara), (Julie,Bob))

  /* 結果値を生成(yield)するすべてのfor式はコンパイラによって
   * map、flatMap、withFilter呼び出しの組み合わせに変換される
   */
}

class ForExpression {
  case class Person(name: String, isMale: Boolean, children: Person*)
  // サンプルリスト
  val lara = Person("Lara", false)
  val bob = Person("Bob", true)
  val julie = Person("Julie", false, lara, bob)
  val persons = List(lara, bob, julie)

  /** 23.1 for式
   *
   * 一般に、for式は次のような形式になっている
   *
   * for ( seq ) yield expr
   *
   * このseqには、ジェネレータ(generator)、定義(definition)、フィルター(filter)を連続した形で指定する
   * 例えば次のように
   */
  for (p <- persons; n = p.name; if (n startsWith "To")) yield n
  // シーケンスを囲んでいる括弧は中括弧に書き換えられる
  // 中括弧を使う場合セミコロンはオプションになる
  for {
    p <- persons  // ジェネレータ
    n = p.name    // 定義
    if (n startsWith "B") // フィルター
  } yield n

  /* ジェネレータは次の形になっている
   *
   * pat <- expr
   *
   * exprは一般にリストを返すが、後で示すようにコレは一般化できる
   * patパターンはそのリストに含まれる全ての要素との間で１対１で照合される
   * マッチに成功すると、パターン内の変数が、要素内に対応する部分に束縛される
   * ただしマッチが失敗してもMatchErrorはなげられない
   *
   * ほとんどの場合、pat は、x <- expr のように、変数パターンである
   * この場合、変数ｘは単純にexprが返す全ての要素を反復処理する
   */

  /* 定義は次のような形式になっている
   *
   * pat = expr
   *
   * この定義はexprの値にpatパターンを束縛する
   * そこで、これは次のval定義と同じ意味を持っている
   *
   * val x = expr
   *
   * もっとも一般的なパターンは変数パターンになっているもの
   * e.g. x = expr
   */

  /* フィルターは次のような形式になっている
   *
   * if expr
   *
   * フィルターのexprは、Boolean型の式である
   * exprがfalseを返してくるような要素をすべて反復処理から脱落させる
   */

  /* すべてのfor式はジェネレータから始まる。
   * for式に複数のジェネレータが含まれている場合、後ろのジェネレータの方が前のジェネレータよりも早く反復される
   */
  for (x <- List(1, 2); y <- List("one", "two")) yield (x, y)
  // List[(Int, String)] = List((1,one), (1,two), (2,one), (2,two))
}

class NQueen {
  /** 23.2 N女王問題
   *
   * 8女王(eight-queens)問題: 標準的なチェス盤を使って、互いに相手を取れない位置に８個の女王の駒を並べる
   *
   * この問題は、任意のサイズのチェス盤に問題を一般化したほうが解きやすい
   * つまり、任意のNについてN×Nの盤面にN個の女王を並べる問題にする
   *
   * マス目は１から番号をつける
   *
   *
   */
}
class For {

}