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

  // コールバック付きボタン *******************************
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

  // 関心を分離した形 *******************************


  def traitImplementsObserver {

    abstract class Widget

    // ボタンはボタンの「本質」を処理することだけに関心を持つ
    class Button(val label: String) extends Widget {
      def click() = {
        // クリックされたボタンの「物理的な」見た目についての処理
      }
    }

    // Observerパタンのロジックを実装するトレイト
    trait Subject {
      // 構造型: ある型がサポートすべき構造のみを指定
      // このシグネチャを持つクラスであれば何でもObserver として使用できる
      type Observer = { def receiveUpdate(subject: Any) }
      private var observers = List[Observer]()                        // リスト追加するのでvar
      // オブサーバーの登録 observers = observer :: observers  => observers.::(observer) 右束縛なのでこう変換される
      def addObserver(observer:Observer) = observers ::= observer
      // (obs) => obs.receiveUpdate(this)
      // _.receiveUpdate(this)       (_) がプレイスホルダーとなり短縮できる ラムダ式相当の構文
      def notifyObservers = observers foreach (_.receiveUpdate(this)) // foreach は (A) => Unit型の引数を受け取る
    }
    // トレイトは抽象メンバ、具象メンバ、両方を宣言できる
    // var: 参照を変えられる
    // val: 参照を変えられない

    // ここではvarと不変のListを使っている
    // 実際の実装ではvalと可変のListBufferを使うほうがよい

    // Subjectトレイトを使ってみる
    class ObservableButton(name: String) extends Button(name) with Subject {
      override def click() = {       // スーパクラスの具体的な実装を上書きするのでoverrideキーワードが必要
        super.click()
        notifyObservers
      }
    }
    /* １つ以上のトレイトを使うクラスを宣言し、そのクラスが他のクラスを拡張しないのであれば
     * 最初のトレイトにextendsキーワードを使わなければならない
     */

    // ObservableButtonの動作を確認するために
    // ボタンのクリックを観察しカウントするオブサーバーを作る
    class ButtonCountObserver {
      var count = 0
      def receiveUpdate(Subject: Any) = count += 1
    }

    /* Specsテスト
     *
     * import org.specs2._
     *
     * object ButtonObserverSpec extends Specification {
     *   "A Button Observer" should {
     *     "Observe button clicks" in {
     *       val observableButton = new ObservableButton("Okay")
     *       val buttonObserver = new ButtonCountObserver
     *       oservableButton.addObserver(buttonObserver)
     *
     *       for (i <- 1 to 3) observableButton.click()
     *       buttonObserver.count mustEqual 3
     *     }
     *   }
     * }
     */

    /* ObservableButtonインスタンスが１つしか必要ない場合
     * Buttonのサブクラス宣言は必要ない -> インスタンスを作るときにトレイトをミックスインする
     *
     * import org.specs2._
     *
     * object ButtonObserverAnonSpec extends Specification {
     *   "A Button Observer" should {
     *     "Observe button clicks" in {
     *
     *       val observableButton = new Button("Okay") with Subject {  // ObservableButtonのclickメソッドのロジックが
     *         override def click() = {                                // 複製されている
     *           super.click()
     *           notifyObservers
     *         }
     *       }
     *
     *       val buttonObserver = new ButtonCountObserver
     *       oservableButton.addObserver(buttonObserver)
     *
     *       for (i <- 1 to 3) observableButton.click()
     *       buttonObserver.count mustEqual 3
     *     }
     *   }
     * }
     * }
     *
     */

  }

  /* 4.2 積み重ね可能なトレイト
   *
   * 上記のコードを改善することで、コードの再利用性を高め、一度に２つ以上のトレイトをより簡単に使用することが出来る。
   * トレイトを積み重ねていく
   */
  def traitStack {
    trait Subject {
      type Observer = { def receiveUpdate(subject: Any) }
      private var observers = List[Observer]()
      def addObserver(observer:Observer) = observers ::= observer
      def notifyObservers = observers foreach (_.receiveUpdate(this))
    }

    // javaのインターフェイスのようなトレイト
    trait Clickable {
      def click()
    }

    abstract class Widget

    // Clickableトレイトを使ってButtonをリファクタリング
    class Button(val label: String) extends Widget with Clickable {
      def click() ={
        // クリックされたボタンの見た目を変更するロジック
      }
    }

    // ボタンの観察ではなくクリックの観察
    trait ObservableClicks extends Clickable with Subject {
      abstract override def click() = { // superはまだ何にも束縛されていない
        super.click()                   // Buttonのclickメソッドを具体的に定義するインスタンスがObservableClicksを
        notifyObservers                 // ミックスインするときに始めてsuperが束縛される
      }
    }
    // トレイトのメソッドがsuperメソッドを呼び出す実装を持つにも関わらず、
    // 親トレイとのsuperメソッドが具象化された実装を持たない場合に
    // abstractキーワードを持つメソッドを定義する

    /* Specsテスト内でButtonと具象化されたclickメソッドと一緒にObservableClicksトレイトを使ってみる
     *
     * import org.specs2._
     *
     * object ButtonClickableObserverSpec extends Specification {
     *
     *   "A Button Observer" should {
     *     "observe button clicks" in {
     *       val observableButton = new Button("Okay") with ObservableClicks
     *       val buttonClickeCountObserver = new ButtonCountObserver
     *       observableButton.addObserver(buttonClickCountObserver)
     *
     *       for ( i <- to 3) observableButton.click()
     *       buttonClickCountObserver.count mustEqual 3
     *     }
     *   }
     * }
     *
     */

    /* JavaBeansの「拒否可能」イベント: JavaBeansへの変更に対するリスナーはその変更を拒否できる
     *
     * トレイトを使って同じような仕組みを作る
     */
    trait VetoableClicks extends Clickable {
      val maxAllowed = 1  // default
      private var count = 0

      abstract override def click() = {
        if (count < maxAllowed) {
          count += 1
          super.click()
        }
      }
    }
    /* ObservableClickesとVetoableClickesが連携するSpecsオブジェクト
     *
     * import org.specs2._
     *
     * object ButtonClickableObserverVetoableSpec extends Specification {
     *   "A Button Observer with Vetoable Clicks"
     * }
     *
     */
     


  }









}





















































