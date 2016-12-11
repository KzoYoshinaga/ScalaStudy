package start1

class Scala21 {

  /** ４章 トレイト
   */

  /* 4.1 トレイトの基礎
   *
   * Java ではインターフェイスで実装コードを再利用するための
   * 組み込みのメカニズムがない
   *
   * インターフェイスの実装のコードの中に、他のメンバと関係のない(直交する)メンバが含まれていることがある
   *
   * ミックスイン：そのように焦点が絞られ、再利用できる可能性があり、独立して保守できるインスタンスの部品
   *
   * コールバック付きボタンの例 ------------
   *
   * javaではコールバックの振る舞いのためのインターフェイスを用意しても、コールバックとボタン固有のロジックを分離
   *     することは難しい。モジュール性には妥協し、何らかの形で実装コードをクラスに埋め込む必要がある
   *     => JavaFX ではどうか？プロパティバインディング
   *
   * アスペクト指向プログラミング[Aspect-OP:AOP]では特別なツールJaspectなどが使える
   *     アスペクト指向は主に、アプリケーションのいたるところで何度も現れる「散在する」関心ごとの
   *     実装を分離するために考え出されてきた。
   *     散在する関心ごとをモジュール化しつつも、これらの振る舞いとアプリケーションのコアなドメインロジック
   *     などのほかの関心ごとを、ビルド時か実行時のいずれかで細かく「ミックスイン」可能にすることを目指している
   */

  /* 4.1.1 ミックスインとしてのトレイト
   *
   * 宣言時にもインスタンス生成時にもトレイとをミックスインできる
   *
   * トレイト：実装を持つことも出来るインターフェイス、「制限のある」多重継承、Rubyの「モジュール」に近い
   */

  // コールバック付きボタン
  def regacy{

    abstract class Widget

    class ButtonWithCallbaks(val lavbel: String, val clickedCallbacks: List[() => Unit]) extends Widget {
      require(clickedCallbacks != null, "Callback list can't be null") // require(boolean false then => doSomething)
                                                                       // test an expression
      def this(label: String, clickedCallback: () => Unit) =    // コンス
        this(label, List(clickedCallback))

      def this(label: String) = {
        this(label, Nil)
        println("Warning: button has no click callbacks!")
      }

      def click() = {
        // クリックされた物理的なボタンの見た目を変更するロジック
        clickedCallbacks.foreach(f => f())
      }
    }
  }

  // 関心を分離した形
  def divideInterest {

    abstract class Widget

    // ボタンはボタンの「本質」を処理することだけに関心を持つ
    class Burtton(val label: String) extends Widget {
      def click() = {
        // クリックされたボタンの「物理的な」見た目についての処理
      }
    }
  }

  // Observerパタンのロジックを実装するトレイト
  def traitImplementsObserver {
    trait Subject {
      type Observer = { def receiveUpdate(subject: Any) }

      private var observers = List[Observer]()
      def addObserver(observer:Observer) = observers ::= observer     // オブサーバーの登録
      def notifyObservers = observers foreach (_.receiveUpdate(this)) // 通知
    }
  }


}