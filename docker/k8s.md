# kubernetes对象

## Kubernetes对象：是一种持久化的、用于表示集群状态的实体

 * 一种声明式的意图的记录，一般使用yaml文件描述对象
 * K8S集群使用k8s对象来表示集群的状态
 * 通过API/kubectil管理K8S对象



面试题：

​	***主节点和工作节点是如何通信的***

​		  ***apiserver*** 

​      **Master是如何将Pod调度到指定的Node上的  **


​      **各Node、Pod的信息是维护在哪里**

​	etcd



​	 K8S中的Service是对象资源之一，一个K8S Service是一系列pod的逻辑集合的抽象，同时它是访问这些pod的一个策略，有时候也被成为微服务。Service通过Label Selector和Pod建立关联关系，并由Service决定将访问转向到后端的哪个pod.



​	Service被创建后，系统自动创建一个同名的endpoints,该对象包含pod的ip地址和端口号集合



​	Service分为三类：

​			1.ClusterIP

​			2.NodePort

​			3.LoadBalancer