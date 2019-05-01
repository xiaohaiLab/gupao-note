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



**https://dev.mysql.com/doc/refman/5.7/en/general-thread-states.html**



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

query_cache_type



>值: 0--不启用查询缓存，默认值;
>
>值: 1--启用查询缓存，只要符合查询缓存的要求，客户端的查询语句和记录集都可以缓存起来，供其他客户端使用,加上SQL_NO_CACHE将不缓存
>
>值: 2--启用查询缓存，只要查询语句中添加了参数:SQL_CACHE,且符合查询缓存的要求，客户端的查询语句和记录集，则可以缓存起来，供其他客户端使用



**query_cache_size**

允许设置query_cache_size的值最小为40K,默认1M,推荐设置为:64M/128M;



**query_cache_limit**

限制查询缓存区最大能缓存的查询记录集，默认设置为1M



**show status like 'Qcache%'命令可查看缓存情况**



####  不会缓存的情况

* 1.当查询语句中有一些不确定的数据时，则不会被缓存。如包含函授NOW(),CURRENT_DATE()等类似的函数，或者用户自定义的函数，存储函数，用户变量等都不会被缓存

* 2.当查询的结果大于query_cache_limit设置的值时，结果不会被缓存
* 3.对于InnoDB引擎来说，当一个语句在事务中修改了某个表，那么在这个事务提交之前，所有与这个表相关的查询都无法被缓存。因此长时间执行事务，会大大降低缓存命中率。
* 4，查询的表是系统表
* 5，查询的语句不涉及到表



### 阶段三: 查询优化处理



**查询优化又分为三个阶段:**

* 解析sql 

  ​	通过lex词法分析，yacc语法分析将sql语句解析成解析树

* 预处理阶段

  ​	根据mysql的语法的规则进一步检查解析树的合法性，如:检查数据的表和是否存在，解析名字和别名的设置，进行权限的验证。

*  查询优化器

  ​         优化器的主要作用就是找到最优的执行计划



**查询优化器如何找到最优执行计划**

* 使用等价变化规则

  ​	 5=5 and a>5 改写成 a>5

  ​	a<b and a=5 改写成 b>5 and a=5

  ​	基于联合索引，调整条件位置等

* 优化count、min、max等函数

  ​	min函数只需找索引最左边

  ​	max函数只需找索引最右边

  ​	myisam引擎count(*)

* 覆盖索引扫描

* 子查询优化

* 提前终止查询

  ​        用了limit关键字或者使用不存在的条件

* IN的优化

  ​        先进行排序，再采用二分查找的方式



**MySql的查询优化器是基于成本计算的原则。他会尝试各种执行计划。数据抽样的方式进行试验*(随机的读取一个4K的数据块进行分析)**

 

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



### 阶段四 : 查询执行引擎

​	调用插件式的存储引擎的原子API的功能进行执行计划的执行



### 阶段五：返回客户端

1、有需要做缓存的，执行缓存操作
2、增量的返回结果：
​		开始生成第一条结果时,mysql就开始往请求方逐步返回数据
​	好处： mysql服务器无须保存过多的数据，浪费内存
​	用户体验好，马上就拿到了数据	



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



# 事务

数据库操作的最小工作单元，是作为单个逻辑工作单元执行的一系列操作；

事务是一组不可再分割的操作集合（工作逻辑单元）;

**典型事务场景(转账)：**
​	update user_account set balance = balance - 1000 where userID = 3;
​	update user_account set balance = balance +1000 where userID = 1;

**mysql中如何开启事务：**
​	begin / start transaction -- 手工
​	commit / rollback -- 事务提交或回滚
​	set session autocommit = on/off; -- 设定事务是否自动开启

**JDBC 编程：**
​	connection.setAutoCommit（boolean）;
​	Spring 事务AOP编程：
​	expression=execution（com.gpedu.dao.*.*(..)）



### ACID特性

* 原子性（Atomicity）

  最小的工作单元，整个工作单元要么一起提交成功，要么全部失败回滚

* 一致性（Consistency）
  事务中操作的数据及状态改变是一致的，即写入资料的结果必须完全符合预设的规则，
    不会因为出现系统意外等原因导致状态的不一致

* 隔离性（Isolation）

    一个事务所操作的数据在提交之前，对其他事务的可见性设定（一般设定为不可见）

* 持久性（Durability）
    事务所做的修改就会永久保存，不会因为系统意外导致数据的丢失



### 隔离级别

SQL92 ANSI/ISO标准：
http://www.contrib.andrew.cmu.edu/~shadow/sql/sql1992.txt

* Read Uncommitted（未提交读） --未解决并发问题
  事务未提交对其他事务也是可见的，脏读（dirty read）
* Read Committed（提交读） --解决脏读问题
  一个事务开始之后，只能看到自己提交的事务所做的修改，不可重复读（nonrepeatable
  read）
* Repeatable Read (可重复读) --解决不可重复读问题
  在同一个事务中多次读取同样的数据结果是一样的，这种隔离级别未定义解决幻读的问题
* Serializable（串行化） --解决所有问题
  最高的隔离级别，通过强制事务的串行执行





# 表锁，行锁

锁是用于管理不同事务对共享资源的并发访问

表锁与行锁的区别：

>​	锁定粒度：表锁 > 行锁
>​	加锁效率：表锁 > 行锁
>​	冲突概率：表锁 > 行锁
>​	并发性能：表锁 < 行锁



InnoDB存储引擎支持行锁和表锁（另类的行锁）

>​         共享锁（行锁）：Shared Locks
>​	 排它锁（行锁）：Exclusive Locks
>​	 意向锁共享锁（表锁）：Intention Shared Locks
>​	 意向锁排它锁（表锁）：Intention Exclusive Locks



*行锁的算法*

> 自增锁 AUTO-INC Locks
> 记录锁 Record Locks
> 间隙锁 Gap Locks
> 临键锁 Next-key Locks



**共享锁:**
​	又称为读锁，简称S锁，顾名思义，共享锁就是多个事务对于同一数据可以共享一把锁，
都能访问到数据，但是只能读不能修改;

> 加锁释锁方式：
> ​		select * from users WHERE id=1 LOCK IN SHARE MODE;
> ​		commit/rollback



**排他锁:**
​	又称为写锁，简称X锁，排他锁不能与其他锁并存，如一个事务获取了一个数据行的排他
锁，其他事务就不能再获取该行的锁（共享锁、排他锁），只有该获取了排他锁的事务是可以对
数据行进行读取和修改，（其他事务要读取数据可来自于快照）

>加锁释锁方式：
>​		delete / update / insert 默认加上X锁
>​		SELECT * FROM table_name WHERE ... FOR UPDATE
>​		commit/rollback



**InnoDB的行锁是通过给索引上的索引项加锁来实现的。** 

>只有通过索引条件进行数据检索，InnoDB才使用行级锁，否则，InnoDB将使用表锁(锁住索引的所有记录)



**意向共享锁(IS)**

		表示事务准备给数据行加入共享锁，即一个数据行加共享锁前必须先取得该表的IS锁，
	意向共享锁之间是可以相互兼容的

**意向排它锁(IX)**

		表示事务准备给数据行加入排他锁，即一个数据行加排他锁前必须先取得该表的IX锁，
	意向排它锁之间是可以相互兼容的



**意向锁(IS 、IX) 是InnoDB 数据操作之前 自动加的，不需要用户干预**

意义：当事务想去进行锁表时，可以先判断意向锁是否存在，存在时则可快速返回该表不能启用表锁



**自增锁**

​	针对自增列自增长的一个特殊的表级别锁
 show variables like 'innodb_autoinc_lock_mode';
 默认取值1 ，代表连续，事务未提交ID 永久丢失	



**Next-key locks：**

- 锁住记录+ 区间（左开右闭）
  当sql执行按照索引进行数据的检索时,查询条件为范围查找（between and、<、>等）并有数
  据命中则此时SQL语句加上的锁为Next-key locks， 锁住索引的记录+ 区间（左开右闭）

**Gap locks：**

  - 锁住数据不存在的区间（左开右开）
    当sql执行按照索引进行数据的检索时，查询条件的数据不存在，这时SQL语句加上的锁即为
    Gap locks， 锁住索引不存在的区间（左开右开）

**Record locks:**

- 锁住具体的索引项
  当sql执行按照唯一性（Primary key、Unique key）索引进行数据的检索时，查询条件等值匹配且查询的数据是存在，这时SQL语句加上的锁即为记录锁Record locks


# 利用锁怎么解决脏读

排它锁



# 利用锁怎么解决不可重复读

共享锁



# 利用锁怎么解决幻读

临界锁

A

select * from user where age > 15    加上next-key

next-key 区间 负无穷大到16  16 -- +&

B

insert into user values(2,'Bob',22);

A

select * from user where age > 15;



# 死锁的避免

- 1）类似的业务逻辑以固定的顺序访问表和行。
- 2）大事务拆小。大事务更倾向于死锁，如果业务允许，将大事务拆小。
- 3）在同一个事务中，尽可能做到一次锁定所需要的所有资源，减少死锁概率。
- 4）降低隔离级别，如果业务允许，将隔离级别调低也是较好的选择
- 5）为表添加合理的索引。可以看到如果不走索引将会为表的每一行记录添加上锁（或者说是表锁）

 

# 快照读

​	SQL读取的数据是快照版本，也就是历史版本，普通的SELECT就是快照读

​	INNODB快照读，数据的读取将由cache（原本数据）+undo（事务修改过的数据）两部分组成。



# 当前读

​	SQL读取的数据是最新版本。通过锁机制来保证读取的数据无法通过其他事务进行修改

​	UPDATE、DELETE、INSERT、SELECT ... LOCK IN SHATE MODE、SELECT ... FOR UPDATE都是当前读