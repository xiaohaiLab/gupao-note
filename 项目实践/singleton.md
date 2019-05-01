# 单点登录的解决方案



## http 协议的特性

* 无状态(***客户端访问服务端，携带着sessionid***)

  - session

    服务端提供的机制，可以用来保存状态。

  - cookie(客户端存储sessionid)

* 集群环境下，挂在LB负载均衡器(***同一个用户的不同请求做分流***)



## 负载均衡服务器

>硬件（F5--》4层负载/redware--》7层负载）
>
>* 4层：IP协议 IP地址 -> 端口TCP协议
>* 7层 :  



>软件
>
>lvs(4层负载),HAProxy(),Nginx
>
>
>
>负载均衡算法
>
>* 轮询加权算法
>  - 4个客户端访问2个tomcat服务器->按照顺序依次访问从而达到负载均衡的目的->如果某个tomcat服务器性能好可以加3个客户端（***加权***）
>* 随机算法
>  - 随机加权
>* hash算法
>  - 根据客户端的ip地址固定请求某一台服务器
>* 最小连接数
>  - 哪台的服务器请求比较少，就落在哪台服务器上



### 集群服务器共享session的解决方案

1. session sticky

2. session 复制，利用tomcat的同步会话机制，存在较大的网络开销

3. session的统一存储

   * session统一放在数据库或者nosql中   

4. JWT，jsonwebtoken

   ***组成部分：***

   - header

     {

     ​    	typ:"jwt",

     ​    	alg: "HS256" 

      	zip:"gzip"

     }

   - playload

     {

     ​	"sub":"123123213",

     ​	"name":"john",

     ​	"iat":150565215825

     }

   - signature 签名 













​    