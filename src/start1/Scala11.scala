

package start1{
  /* 2.9 ファイルと名前空間によるコード構成
   *
   * ファイルの「物理的な」位置に依存することなく、ファイルの中にパッケージを定義できる
   * 入れ子型のパッケージ指定
   *
   * Javaのパッケージ構文だと
   * package start1.namespace.file.code
   * package start1.namespace.file.sample
   * package start1.namespace.pkg1.pkg20.pkg121
   *
   * C#のnamespace構文やRubyの名前空間としてのmoduleの使い方に似ている
   */
  package namespace {
    package file {
      package code {
        class Scala11 {

        }
      }
     package samlpe {
        class Sample {

        }
      }
      package pkg1.pkg20.pkg121 {  // パッケージ毎にpackage節を分ける必要はない
        class ClassSomeWhere {

        }
      }
    }
  }
}
