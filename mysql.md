# mysql存储引擎



### Archive

* 压缩数据的方式进行存储
* 只支持insert和select两种操作
* 只运行自增ID列建立索引
* 行级锁
* 不支持事务
* 数据占用磁盘少



### Memory|heap

数据都是存储在内存中，IO效率要比其他引擎高很多，服务重启数据丢失，内存数据表默认只有16M

特点：

​	支持hash索引，B tree索引，默认hash

​	字段长度都是固定长度varchar(32)=char(32)

​	不支持大数据存储类型字段如blog,text

​	表级锁

应用场景：

​	等值查找热度较高数据

​	查询结果内存中的计算，大多数都是采用这种存储引擎作为临时表存储需计算的数据



### Myisam

特点：

​	a.select count(*) from table 无需进行数据的扫描

​	b.数据和索引分开存储

​	c.表级锁

​	d.不支持事务



### Innodb

​	支持事务ACID

​	行级锁

​	聚集索引

​	



# mysql查询优化



### 阶段一:mysql客户端/服务端通信 



mysql客户端与服务端的通信方式是"半双工";



mysql通讯特点和限制:

​	客户端一旦开始发送消息另一端要接受完整个消息才能响应

​	客户端一旦开始接受数据没法停下来发送指令



show full processlist/show processlist


Sleep

线程正在等待客户端发送数据

Query

连接线程正在执行查询

Locked

线程正在等待锁的释放

SendingData

向请求端返回数据



### 阶段二: 查询缓存 



工作原理：

缓存SELECT操作的结果集和SQL语句；

新的SELECT语句，先去查询缓存，判断是否存在可用的记录集；



判断标准：

与缓存的SQL语句，是否完全一样

(简单认为存储一个key-value结构，key为sql,value为sql查询结果集)



mysql> show variables like 'query_cache%';

mysql> show status like ‘Qcache%’;



### 阶段三: 查询优化处理



**查询优化又分为三个阶段:**

* 解析sql 

  ​	通过lex词法分析，yacc语法分析将sql语句解析成解析树

* 预处理阶段

  ​	根据mysql的语法的规则进一步检查解析树的合法性，如:检查数据的表和是否存在，解析名字和别名的设置，进行权限的验证。

*  查询优化器

  ​         优化器的主要作用就是找到最优的执行计划



**查询优化器如何找到最优执行计划**

* 将可转换的外连接查询转换成内连接查询

 

#### select查询的序列号，标识执行的顺序     

1、id相同，执行顺序由上至下

2、id不同，如果是子查询，id的序号会递增，id值越大优先级越高，越先被执行

3、id相同又不同即两种情况同时存在，id如果相同，可以认为是一组，从上往下顺序执行；在所有组中，id值越大，优先级越高，越先执行



#### 执行计划-select_type

查询的类型，主要是用于区分普通查询，联合查询，子查询等

* SIMPLE : 简单的select查询，查询中不包含子查询或者union

* PRIMARY：查询中包含字部分，最外层查询则被标记为primary

* SUBQUERY/MATERIALIZED: SUBQUERY表示在select或where列表中包含了子查询

​		MATERIALIZED表示where后面in条件的子查询

* UNION: 若第二个select出现在union之后，则被标记为union

* UNION RESULT： 从union表获取结果的select



#### 执行计划-type

访问类型，sql查询优化中一个很重要的指标，结果值从好到坏依次是：

* system：表只有一行记录（等于系统表），const类型的特例，基本不会出现，可以忽略不计
* const：表示通过索引一次就找到了，const用于比较primary key 或者 unique索引
* eq_ref：唯一索引扫描，对于每个索引键，表中只有一条记录与之匹配。常见于主键 或 唯一索引扫描
* ref：非唯一性索引扫描，返回匹配某个单独值的所有行，本质是也是一种索引访问
* range：只检索给定范围的行，使用一个索引来选择行
* index：Full Index Scan，索引全表扫描，把索引从头到尾扫一遍
* ALL：Full Table Scan，遍历全表以找到匹配的行
* possible_keys 查询过程中有可能用到的索引
* key 实际使用的索引，如果为NULL，则没有使用索引 rows根据表统计信息或者索引选用情况，大致估算出找到所需的记录所需要读取的行数
* filtered它指返回结果的行占需要读到的行(rows列的值)的百分比表示返回结果的行数占需读取行数的百分比，filtered的值越大越好



#### 执行计划-Extra

**十分重要的额外信息**

* 1、Using filesort ：
  mysql对数据使用一个外部的文件内容进行了排序，而不是按照表内的索引进行排序读取

* 2、Using temporary：

  使用临时表保存中间结果，也就是说mysql在对查询结果排序时使用了临时表，常见于order by 或 group by 

* 3、Using index：
表示相应的select操作中使用了覆盖索引（Covering Index），避免了访问表的数据行，效率高

* 4、Using where ：
  使用了where过滤条件
* 5、select tables optimized away：
  基于索引优化MIN/MAX操作或者MyISAM存储引擎优化COUNT(*)操作，不必等到执行阶段在进行计算，查询执行计划生成的阶段即可完成优化



### 阶段四:查询执行引擎

​	调用插件式的存储引擎的原子API的功能进行执行计划的执行



### 阶段五：返回客户端

1、有需要做缓存的，执行缓存操作
2、增量的返回结果：
开始生成第一条结果时,mysql就开始往请求方逐步返回数据
好处： mysql服务器无须保存过多的数据，浪费内存
用户体验好，马上就拿到了数据	



# 如何定位慢sql

 1、业务驱动

 2、测试驱动



 ### 慢查询日志

* show variables like 'slow_query_log'
* set global slow_query_log = on
* set global slow_query_log_file = '/var/lib/mysql/gupaoeduslow.log'
* set global log_queries_not_using_indexes = on 
* set global long_query_time = 0.1 (秒)	



**慢查询日志分析** 

* Time ：日志记录的时间
* User@Host：执行的用户及主机
* Query_time：查询耗费时间 Lock_time 锁表时间 Rows_sent 发送给请求方的记录
* 条数 Rows_examined 语句扫描的记录条数
* SET timestamp 语句执行的时间点
* select .... 执行的具体语句

 mysqldumpslow -s at -t 10 /var/...

