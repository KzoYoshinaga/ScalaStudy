package start4

class MutableObject {
  {  // どのようなオブジェクトがミュータブルなのか ****

    // 純粋関数型オブジェクト
    // 全ての操作が何らかの結果値を返し
    // 同じ操作をいつ行っても同じ結果値返すオブジェクト
    // 内部に var を持っていても、操作の結果がいつも変わらなければ純粋関数型といえる
    // e.g. メモ化されたキー値(最適化コストが掛かる操作の結果)を返すオブジェクト

    // ミュータブルオブジェクト
    // 同じ操作でも行うタイミングによって違う結果値を返すオブジェクト
    // 時間と共に変化する現実世界のオブジェクトを自然にモデリングすることが多い
  }

  {  // 再代入可能な変数とプロパティ ****
    // 再代入可能な変数(reassignableVariables)には
    // 値の取得(get)と新しい値の設定(set)という２つの基本操作がある
    // JavaBeansではゲッターとセッターを明示的に定義しなければならない

    // Scalaでは、何らかのオブジェクトの公開/限定公開メンバーになっている var には
    // 暗黙のうちにゲッター/セッターメソッドが定義される
    // var x のゲッターは x セッターは x_= である
    {
      class Time {
        var hour = 12
        var minute = 0
      }
    }
    // ゲッター/セッターは共に元の var と同じ可視性を得る
    // 上記Timeクラスは下記の用に明示的に定義されたものと等しくなる
    {
      class Time {
        private[this] var h = 12
        private[this] var m = 0
        def hour: Int = h
        def hour_=(x: Int) = { h = x }
        def minute: Int = m
        def minute_=(x: Int) = { m = x}
      }
    }
    // 直接セッター/ゲッターを定義し、変数への値の取得・代入に独自の解釈を織り込むことができる
    {
      class Time {
        private[this] var h = 12
        private[this] var m = 0
        def hour: Int = h
        def hour_= (x: Int) = {
          require(0 <= x && x < 24)
          h = x
        }
        def minute: Int = m
        def minute_= (x: Int) = {
          require(0 <= x && x < 24)
          m = x
        }
      }
      // C#にはこうした変数と同じ役割を果たすプロパティを持っている
      // JavaFXでもBeansベースのプロパティを使ってる

      // 対応するフィールドを持たないゲッター/セッターを定義することも可能
      {
        class Thermometer {
          var celsius: Float = _  // 初期値としてデフォルト値をとることを明示 Javaでの初期化子なしと同義
          def fahrenheit = celsius * 9 / 5 + 32
          def fahrenheit_= (f: Float) = {  // 華氏で変数を持っているわけではない
            celsius = (f - 32) * 5 / 9
          }
          override def toString = fahrenheit + "F/" + celsius + "C"
        }
        val temp = new Thermometer
        temp.celsius = 100 // Thermometer = 212.0F/100.C
        temp.fahrenheit = 100 //Thermometer = 100.0F/37.77778C
        // Scalaでは =_ と言う初期化子を省略できない
        // 次のようなコードは抽象変数の宣言になる
        // var celsius: Float
      }
    }
  }

  { // ケーススタディ：離散イベントシミュレーション ****

  }

}