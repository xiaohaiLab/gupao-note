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

### 阶段一：mysql客户端/服务端通信 



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



### 阶段二、查询缓存 



工作原理：

缓存SELECT操作的结果集和SQL语句；

新的SELECT语句，先去查询缓存，判断是否存在可用的记录集；



判断标准：

与缓存的SQL语句，是否完全一样

(简单认为存储一个key-value结构，key为sql,value为sql查询结果集)



mysql> show variables like 'query_cache%';



mysql> show status like ‘Qcache%’;

