package start1

class Scala15 {
  /** 3.4 Scalaの if文
   *
   *  Scalaでは一般的な言語機能でさえ大幅に強化されいる
   */

  // 普通のif文
  def ordinaryIf() {
    if (2 + 2 == 5) {
      System.out.println("Hello from 1984");
    } else if (2 + 2 == 3) {
      System.out.println("Hello from Remedial Math class?");
    } else {
      System.out.println("Hello from a non-Orwellian future");
      // Owellian(Owellの作風の:特に1984年に描かれた独裁主義体制世界について)
    }
  }

  // ifやその他のほとんどの文が実際には式である
  // e.g. ifの結果を代入
  def scalaIf {
    val configFile = new java.io.File(".conf")
    val configFilePath = if (configFile.exists()) {   // ifの結果はconfigFilePathに代入され再評価されない
      configFile.getAbsolutePath();
    } else {
      configFile.createNewFile()
      configFile.getAbsolutePath() // configFilePath: String = D:\10_Java\eclipse\pleiades\pleiades\eclipse\.conf
    }
  }
  /* Javaに読み替え
   * public static Result if(Boolean is) {
   *     return is ? trueResult : falseResult;
   * }
   */

  // if文と演算子の優先順位
  def ifAndOperator {
    println( if(true){1}else{2} + 3)  // => 1
    println( if(false){1}else{2} + 3) // => 5
    // {2}+3 が先に評価され if(){1}else{5} で評価されている
    println( (if(true){1}else{2}) + 3)   // => 4
    println( (if(false){1}else{2}) + 3)  // => 5
  }

  // if文のdefaultの返り値：else文省略時の戻り値
  def defaultReturnOfIfStatement {
    val x1 = if (false) {} // Unit
    val x2 = if(false) {1} // AnyVal
    val x4 = if(false) {new java.io.File("")} // Any
  }
  /* 型推論はif文の全ての結果で動作する型を選択する
   */

}