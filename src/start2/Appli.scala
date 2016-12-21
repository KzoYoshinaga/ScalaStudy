package start2

// App トレイとをシングルトンオブジェクトにミックスすることで
// そのまま起動できる
object Appli extends App{

  Array("a","b","c").foreach(println)

  // argsと言う名前で引数を受け取れる
  for (arg <- args)
    println(arg)
}