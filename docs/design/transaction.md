- MyBatis transactions
  
  ![transaction](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/images/tx.png)
  
- MyBatis @Transactional annotation
  
  Property            | Default             | Description                              |
   ------------------- | ------------------- | ---------------------------------------- |
   executorType        | ExecutorType.SIMPLE | the MyBatis executor type                |
   isolation           | Isolation.DEFAULT   | the transaction isolation level. The default value will cause MyBatis to use the default isolation level from the data source. |
   force               | false               | Flag to indicate that MyBatis has to force the transaction commit() |
   rethrowExceptionsAs | Exception.class     | rethrow caught exceptions as new Exception (maybe a proper layer exception) |
   exceptionMessage    | empty string        | A custom error message when throwing the custom exception; it supports java.util.Formatter place holders, intercepted method arguments will be used as message format arguments. |
   rollbackOnly        | false               | If true, the transaction will never committed, but rather the rollback will be forced. That configuration is useful for testing purposes. |
  
- MyBatis @Transactional VS Spring @Transactional 
  
  Spring的AOP事务管理默认针对unchecked exception回滚(RuntimeErrorException，RuntimeException)，checked exception不回滚。
  
  MyBatis默认针对所有异常都回滚。
  
- DAO与内存操作 @Transactional 回滚
  
  D：数据库操作
  
  M：内存操作
  
  *：表示一个或多个操作
  
   Case（同一方法内执行） | 回滚处理                             |
   ------------- | -------------------------------- |
   D\*           | 无需处理                             |
   D\*M\*        | D异常：无需处理  M异常：捕获异常，内存回滚，然后重新抛出异常 |
   M\*D\*        | 捕获异常，内存回滚，然后重新抛出异常               |
  
- 内存事务
  
  STM、CAS？

  开源STM框架
  
  https://github.com/pveentjer/Multiverse

  http://www.javacreed.com/software-transactional-memory-example-using-multiverse/