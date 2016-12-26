package start4
/**
 * 大規模なサンプルを使って、ミュータブルオブジェクトと一人前の値としての関数を上手く
 * 組み合わせる方法を示す。
 *
 * サンプルはデジタル回路のシミュレーターとし、その設計と実装を見ていく
 * この大きな問題は、それぞれ面白い意味を持ついくつかのサブ問題に分割できる
 *
 * １．デジタル回路のための小さな言語を作る
 * 		その際に、Scalaのようなホスト言語にDSL(ドメイン固有言語)を埋め込むためには
 * 		一般的にどうすれば良いかに焦点を当てる
 *
 * ２．離散イベントシミュレーションを行うため、単純だが汎用性の高いフレームワークを作る
 * 		フレームワークの主な仕事は、シミュレーション上にあるタイミングに実行されるアクション(行為)
 * 		を追跡することである。
 *
 * ３．離散シミュレーションの構成を考えた上で実装を進めていく
 *		このようなシミュレーションのポイントは、シミュレーション上のオブジェクトによって
 * 		実際のオブジェクトをモデリングし、シミュレーションフレームワークを使って
 * 		実際の時間をモデリングすることである
 */
/**
 * このサンプルは古典的な教科書「Structure and Interpretation of Computer  Programs」から取ったもの
 *
 * 違いはサンプルのさまざまな側面を４階層に構造化したこと
 * １階層：シミュレーションフレームワーク
 * ２階層：基本論理回路シミュレーションパッケージ
 * ３階層：ユーザー定義回路のライブラリー
 * ４階層：シミュレーション上の回路自体
 * 各階層はクラスとして表現される、一般性の低い階層が高い階層から継承する
 */
/**
 * デジタル回路を記述する小言語
 *
 * デジタル回路は、配線(Wire)とゲート(Gate)から構成される
 *
 * ゲートの種類
 * inverter: 信号を反転させる
 * AND gate: 入力の論理積を出力とする
 * OR gate : 入力の論理和を出力とする
 *
 * 小言語での各要素の作り方
 * val a, b, c = new Wire // ワイヤーをつくる
 * def inverter(input: Wire, output: Wire) // 反転ゲートを作る
 * def andGate(a1: Wire, a2: Wire, output: Wire) // ANDゲートを作る
 * def orGate(o1: Wire, o2: Wire, output: Wire) // ORゲートを作る
 *
 * これらの手続きは、結果値として作ったゲートを返すのではなく、副作用としてゲートを作る
 * e.g. inverter(a, b) 呼出は、配線aとbの間にインバーターを挿入する
 *
 * このように副作用を使うことによって複雑な回路を少しずつ組み立てていくことができる
 *
 * 基本ゲートからはより複雑なゲートを作ることができる
 * e.g. 半加算器
 *
 * def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire) = {
 * 		val d, e = new Wire
 * 		orGate(a, b, d)
 * 		andGate(a, b, c)
 * 		inberter(c, e)
 * 		andGate(d, e, s)
 * }
 *
 *  a _____________     _______ d _________
 *       |    _____ OR                 ___ AND---- s (sum)
 * 		 	 |    |              ___INV__e_|
 *       |____|____          |
 *    ________|____ AND -------------------------- c (carry)
 *  b
 *
 * s: 排他的論理和
 * c: a,b積(桁上がり)
 *
 * halfAdder は基本ゲートを作る３つのメソッドと同様に、パラメータをとる関数になっている
 * e.g. 全加算器 下位桁からの桁上がりも考慮された加算器
 *
 * def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cont: Wire) = {
 * 		bal s, c1, c2 = new Wire
 * 		halfAdder(a, cin, s, c1)
 * 		halfAdder(b, s, sum, c2)
 *    orGate(c1, c2, cont)
 * }
 *
 * sum : sum = (a + b + cin) % 2
 * cin :
 * cout: cout = (a + b + cin) / 2
 *
 *  b______________________            ______________ sum
 *                      ___  halfAdder __c2_
 *  a_____           _s_|        __________ OR ------ cout
 *     ___ halfAdder________c1___|
 *  cin
 *
 * 離散イベントシミュレーションの汎用APIを基礎としてその上でDSLを実装する
 */
/**
 * シミュレーションAPI: シミュレータ実行の枠組み抽象
 *
 * 離散イベントシミュレーションは、指定されたタイミングでユーザ定義のアクションを実行する
 *
 * 作業項目に格納sされてアクション自身が、実行時に予定表に新しい作業項目を追加することができ
 * このシュミレーションフレームワークが威力を発揮するのはそのため
 */
abstract class Simulation {
  type Action = () => Unit // 型メンバー
  case class WorkItem(time: Int, action: Action) // 指定されたタイミングで実行しなければならないアクション
  private var curtime = 0 // シミュレーション上の時間
  def currentTime: Int = curtime
  private var agenda: List[WorkItem] = List() // まだ実行されいない全ての作業項目の予定表

  private def insert(ag: List[WorkItem], item: WorkItem): List[WorkItem] = {
    if (ag.isEmpty || item.time < ag.head.time) item :: ag
    else ag.head :: insert(ag.tail, item)  // 実行すべき時間の順序で挿入していく
  }

  // シミュレーション上の現在時から遅らせてアクションを呼び出す
  // 名前渡しパラメータはメソッドに渡されたときには評価されない
  def afterDelay(delay: Int)(block: => Unit) = {
    val item = WorkItem(currentTime + delay, () => block)
    agenda = insert(agenda, item)
  }

  private def next() = {
    (agenda: @unchecked) match {
      case item :: rest =>
        agenda = rest
        curtime = item.time
        item.action()
    }
  }
  def run() = {
    afterDelay(0) {
      println("*** simulation started, time = " + currentTime + " ***")
    }
    while (!agenda.isEmpty) next()
  }
}

/**
 * デジタル回路のシミュレーション
 *
 * シミュレーションフレームワークを使って、デジタル回路DSLを実装する
 *
 */
abstract class BasicCircuitSimulation extends Simulation {
  def InverterDelay: Int // 定数関数なので大文字、抽象で指定しサブクラスに実装を任せる
  def AndGateDelay: Int
  def OrGateDelay: Int
  class Wire {
    private var sigVal = false
    private var actions: List[Action] = List()
    def getSignal = sigVal
    def setSignal(s: Boolean) =
      if (s != sigVal) {
        sigVal = s
        actions foreach (_ ())
      }
    def addAction(a: Action) = {
      actions = a :: actions
      a()
    }
  }

  def inverter(input: Wire, output: Wire) = {
    def invertAction() = {
      val inputSig = input.getSignal
      afterDelay(InverterDelay) {
        output setSignal !inputSig
      }
    }
    input addAction invertAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) = {
    def andAction() = {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) {
        output setSignal (a1Sig & a2Sig)
      }
    }
    a1 addAction andAction // 2ほんのワイヤーのうち一方が変化すると
    a2 addAction andAction // 出力ワイヤーが変化する
  }

  def orGate(o1:Wire, o2: Wire, output: Wire) = {
    def orAction() = {
      val o1Sig = o1.getSignal
      val o2Sig = o2.getSignal
      afterDelay(OrGateDelay) {
        output setSignal (o1Sig | o2Sig)
      }
    }
    o1 addAction orAction
    o2 addAction orAction
  }

  // 配線上の信号の変化をチェックするプローブ(探測器)のインストール
  def probe(name: String, wire: Wire) = {
    def probeAction() = {
      println(name + " " + currentTime + " new-value = " + wire.getSignal)
    }
    wire addAction probeAction
  }
}



/**
 * 半加算器と全加算器のメソッド定義も含む抽象シミュレーションクラス
 */
abstract class CircuitSimulation extends BasicCircuitSimulation {
  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire) = {
    val d, e = new Wire
    orGate(a, b, d)
    andGate(a, b, c)
    inverter(c, e)
    andGate(d, e, s)
  }
  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire) = {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c1, c2, cout)
  }
}

object MySimulation extends CircuitSimulation {
	def InverterDelay = 1
	def AndGateDelay = 3
	def OrGateDelay = 5
}

object Simulator extends App {
  import MySimulation._
  val input1, input2, sum, carry = new Wire
  probe("sum", sum)
}