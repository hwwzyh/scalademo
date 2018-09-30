package main.scala

/**
  * Created by zhouyh on 2018/8/13.
  */
object ScalaWordCount {

  def main(args: Array[String]): Unit = {
    //有一些数据，需要进行分析统计，词频统计：获取单词出现的次数，按照次数进行降序排序
    //1.准备数据
    val lines = List(
      "spark, hadoop, hive, spark, hive",
      "spark, hive, spark, hadoop, hive",
      " , spark, hive, spark, spark, hdfs, hadoop"
    )
    /**
      * 回顾一下：
      *   MapReduce如何进行词频统计
      *       1.读取数据，一行一行的读取数据，对每一行数据进行分割为单词，就表明单词出现一次
      *       2.对单词进行分组 统计
      *     数据格式
      *       line -> list<(word, 1)> -> (key, list<value>) -> <key, values.sum>
      */
    //2.数据的转换 采用链式编程
    /**
      * 下述就类似于MapReduce框架中map处理的数据及输出
      */
    val mappedTuple = lines
      //对每一行进行分割
      .flatMap(line => line.trim.split(",").toList)
      //转换，去除单词空格
      .map(word => word.trim)
      //数据过滤清洗
      .filterNot(word => word.isEmpty)
      //将单词转化为元组对 二元组
      .map(word => (word, 1))
//    for (elem <- mappedWords) println(elem)
    /**
      * TODO，
      *   其实上述操作比较繁琐，可以一下就完成
      * 类似于MapReduce中Map函数的操作
      */
    val xx = lines.flatMap(line => {
      line.trim.split(",").map(_.trim).filterNot(_.isEmpty).map((_, 1)).toList
    })
    /**
      * TODO: 进行分组操作，将相同key的value合在一起
      * 类似于MapReduce中reduce端的Group操作
      */
    //3.数据的分组
    val groupdTuple: Map[String, List[(String, Int)]] = mappedTuple.groupBy(tuple => tuple._1)
    /**
      * 分组以后，Map[key:String, list : List[(String, Int)]]
      *       key:就是分组的值，就是单词
      *       list:里面存储的都是相同key的value
      */
    /**
      * TODO:对每一组中的value值进行统计
      * 类似于MapReduce中的reduce操作
      */
    //4.对每一组进行数据计算
    val result = groupdTuple.map(tuple => {
      //获取单词
      val word = tuple._1
      //计算单词word对应的值
      val count = tuple._2.map(_._2).sum
      //返回结果
      (word, count)
    })
    //5.打印结果
    result.foreach(println)
    println("==================================")
    /**
      * 按照词频进行降序排序，并且获取前K个值
      */
    val topResult = result
      .toList
      .sortBy(tuple => -tuple._2)
      .take(3)
    for (elem <- topResult) println(elem)

    /**
      * 组合为一条语句
      */
    println("==================================")
    lines
      //转换数据，清洗过滤
      .flatMap(line => {
        line.trim.split(",").map(_.trim).filterNot(_.isEmpty).map((_,1)).toList
      })
      //数据分组
      .groupBy(tuple => tuple._1)
      //各组统计
      .map(tuple => (tuple._1, tuple._2.map(_._2).sum))
      //获取top K
      .toList.sortBy(tuple => -tuple._2).take(3)
      //打印结果
      .foreach(println)
  }

}
