package start1

class Scala16 {
  /** 3.5 Scalaのfor内包表記
   *
   * Scalaではforループにも豊富な機能を与えられている
   *
   * 内包(implicit): 何らかの集合を探索し、見つけたものを「理解」して、新しいものを計算すると言うアイデア
   *                 関数型プログラミングの用語
   */

  /* 3.5.1 基本的なfor式の例
   *
   * ( <- ) : 左向きのアロー演算子はジェネレータと呼ばれる
   *
   */
  def forSample {
    val friends = List("bob","keit","john","dug")
    for (friend <- friends)
      println(friend)
    // Java8: for (String friend: friends) { System.out.println(friend); }
  }

  /* 3.5.2 フィルタリング
   */
  def filteringSample1 {
    val friends = List("bob","keit","john","dug")
    for (friend <- friends if friend.contains("b")) println(friend) // bob
    // Java8: friends.stream().filter(f -> f.contains("b")).forEach(System.ont::println);
  }
  // フィルタを追加: フィルタの追加には (;) セミコロンで区切って続ける
  def filteringSample2 {
    val friends = List("bob","keit","john","dug","blues")
    for (friend <- friends if friend.contains("b"); if friend.contains("l")) println(friend) // blues
    // Java8: friends.stream().filter(f -> f.contains("b")).filter(f -> f.contains("l")).forEach(System.out::println);
    // Java8: friends.stream().filter(f -> f.contains("b") && f.contains("l")).forEach(System.out::println);

    // 波括弧でfor式を使うと区切りの (;) を省略できる
    for {friend <- friends
      if friend.contains("b")
      if friend.contains("l")
      } println(friend) // blues
    // 要素の１つ１つをfor 式の中のif式をくぐらせて、つど表示するイメージ
  }

  /* 3.5.3 yield (意 作物などを産出する;与える)
   *
   * フィルタリングの結果から新しいコレクションを生成する
   */
  def yieldSample {
    val friends = List("bob","keit","john","dug","blues")
    val particularFriends = for (friend <- friends if friend.contains("b")) yield friend //  List(bob, blues)
    // java8: List<String> particularFriends = friends.stream().filter(f -> f.contains("b")).collect(Collectors.toList());
  }

  /* 3.5.4 スコープの拡大
   *
   * for式の内側で定義した変数をあとで使用できる
   */
  def scopeWiden1 {
    val friends = List("bob","keit","john","dug","blues")
    for { friend <- friends
      upCaseFriend = friend.toUpperCase()
    } println(upCaseFriend)
    // Java8: friends.stream().map(f -> f.toUpperCase()).forEach(System.out::println);
  }
  def scopeWiden2 {
    val friends = List("bob","keit","john","dug","blues")
    for { friend <- friends
      val original = friend.toUpperCase();
      if friend.contains("b")
    } { println("origin: " + original);println("filtered: " + friend)}
    // origin: BOB
    // filtered: bob
    // origin: BLUES
    // filtered: blues
    // フィルタリングされた集合のみ表示されている
  }
}