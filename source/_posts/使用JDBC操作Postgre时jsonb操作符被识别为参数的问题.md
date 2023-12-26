---
title: 使用JDBC操作Postgre时jsonb操作符被识别为参数的问题
date: 2022-08-26 15:08:11
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/使用JDBC操作Postgre时jsonb操作符被识别为参数的问题/title.png?x-oss-process=style/blog_title
categories:
- 我永远爱学习
tags:
- Java
- Debug
---

# 使用JDBC操作Postgre时jsonb操作符被识别为参数的问题

## 一句话解决方式
把sql中的`?`替换为`??`，就能解决

**原SQL**
```sql
-- 可以在pgClient中正常执行
SELECT * FROM project WHERE (tags ?& array['Sonar'])
```
**现SQL**
```sql
-- 可以通过org.postgresql.Driver正常执行
SELECT * FROM project WHERE (tags ??& array['Sonar'])
```
<!--more-->
# 相关环境版本

- org.postgresql.postgresql:42.3.4

# 报错的代码

```java
@Override
public List<Project> listByQuery(ProjectQuery query) {
        // json查询
    if (CollectionUtils.isNotEmpty(tagContains)) {
        var tagContainsArray = tagContains.toArray();
        var placeHolder = IntStream.range(0, tagContains.size()).boxed().map(s -> "{" + s + "}")
                .collect(Collectors.joining(",", "tags ?& array[", "]"));
        if (queryWrapper.nonEmptyOfWhere()) {
            queryWrapper.apply("AND " + placeHolder, tagContainsArray);
        } else {
            queryWrapper.apply(placeHolder, tagContainsArray);
        }
    }
    return baseMapper.selectList(queryWrapper); // <= 此行报错
}
```

# 特征日志

```log
2022-08-26 14:04:17.666 [TID:SLT-1563036635401490432] [ERROR] ller.GlobalExceptionController[70]- 出现未知异常:
### Error querying database.  Cause: org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
### The error may exist in com/kailinjt/middleware/beam/mapper/ProjectMapper.java (best guess)
### The error may involve defaultParameterMap
### The error occurred while setting parameters
### SQL: SELECT  id,key,display_name,description,product_id,technical_manager_id,gitlab_project_id,development_language,tags,create_time,update_time,create_user_id,update_user_id  FROM project     WHERE (tags ?& array[?])
### Cause: org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
; 未设定参数值 2 的内容。; nested exception is org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
org.springframework.dao.DataIntegrityViolationException: 
### Error querying database.  Cause: org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
### The error may exist in com/kailinjt/middleware/beam/mapper/ProjectMapper.java (best guess)
### The error may involve defaultParameterMap
### The error occurred while setting parameters
### SQL: SELECT  id,key,display_name,description,product_id,technical_manager_id,gitlab_project_id,development_language,tags,create_time,update_time,create_user_id,update_user_id  FROM project     WHERE (tags ?& array[?])
### Cause: org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
; 未设定参数值 2 的内容。; nested exception is org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
	at org.springframework.jdbc.support.SQLStateSQLExceptionTranslator.doTranslate(SQLStateSQLExceptionTranslator.java:104) ~[spring-jdbc-5.3.14.jar:5.3.14]
	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:70) ~[spring-jdbc-5.3.14.jar:5.3.14]
	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:79) ~[spring-jdbc-5.3.14.jar:5.3.14]
	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:79) ~[spring-jdbc-5.3.14.jar:5.3.14]
	at org.mybatis.spring.MyBatisExceptionTranslator.translateExceptionIfPossible(MyBatisExceptionTranslator.java:91) ~[mybatis-spring-2.0.6.jar:2.0.6]
    <!-- 下面这行堆栈是框架代码的错误源，上面的堆栈是在抛出和翻译这个错误 -->
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:441) ~[mybatis-spring-2.0.6.jar:2.0.6]  
	at jdk.proxy2.$Proxy139.selectList(Unknown Source) ~[?:?]
	at org.mybatis.spring.SqlSessionTemplate.selectList(SqlSessionTemplate.java:224) ~[mybatis-spring-2.0.6.jar:2.0.6]
	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.executeForMany(MybatisMapperMethod.java:166) ~[mybatis-plus-core-3.4.3.4.jar:3.4.3.4]
	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.execute(MybatisMapperMethod.java:77) ~[mybatis-plus-core-3.4.3.4.jar:3.4.3.4]
	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy$PlainMethodInvoker.invoke(MybatisMapperProxy.java:148) ~[mybatis-plus-core-3.4.3.4.jar:3.4.3.4]
	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:89) ~[mybatis-plus-core-3.4.3.4.jar:3.4.3.4]
	at jdk.proxy2.$Proxy165.selectList(Unknown Source) ~[?:?]
    <!-- 下面这行堆栈是业务代码错误源，上面的堆栈是在框架中处理的 -->
	at com.kailinjt.middleware.beam.service.impl.ProjectServiceImpl.listByQuery(ProjectServiceImpl.java:103) ~[classes/:?]
    <!-- 以下堆栈业务无关，因此略掉... -->
```

## 源码跟踪

1. 由日志可知，这段SQL中有两个参数需要填充，但我们只传入了一个参数导致报错
    ```
    Cause: org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
    ```
2. 打开问题SQL发现这个SQL中的确只有一个参数，猜测是PG的`jsonb`操作符`?&`中的`?`被错误的识别为了参数，基于此，反向查找参数来源。
    ```sql
    SELECT  id,key,display_name,description,product_id,technical_manager_id,gitlab_project_id,development_language,tags,create_time,update_time,create_user_id,update_user_id  FROM project WHERE (tags ?& array[?])
    ```
3. 根据堆栈反向查找错误源`org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:441) ~[mybatis-spring-2.0.6.jar:2.0.6]`
    ```java
    private class SqlSessionInterceptor implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession sqlSession = getSqlSession(SqlSessionTemplate.this.sqlSessionFactory,
            SqlSessionTemplate.this.executorType, SqlSessionTemplate.this.exceptionTranslator);
        try {
            Object result = method.invoke(sqlSession, args);                      // <= 实际报错的是这行
            if (!isSqlSessionTransactional(sqlSession, SqlSessionTemplate.this.sqlSessionFactory)) {
            // force commit even on non-dirty sessions because some databases require
            // a commit/rollback before calling close()
            sqlSession.commit(true);
            }
            return result;
        } catch (Throwable t) {
            Throwable unwrapped = unwrapThrowable(t);
            if (SqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
            // release the connection to avoid a deadlock if the translator is no loaded. See issue #22
            closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
            sqlSession = null;
            Throwable translated = SqlSessionTemplate.this.exceptionTranslator
                .translateExceptionIfPossible((PersistenceException) unwrapped);    // <= 报错的是这行
            if (translated != null) {
                unwrapped = translated;
            }
            }
            throw unwrapped;
        } finally {
            if (sqlSession != null) {
            closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
            }
        }
        }
    }
    ```
4. 到第三步这里发现已经在mybatis和jdbcDriver里了，但是还看不出问题，因为看漏了一个`Caused by`
   ```log
   Caused by: org.postgresql.util.PSQLException: 未设定参数值 2 的内容。
	at org.postgresql.core.v3.SimpleParameterList.checkAllParametersSet(SimpleParameterList.java:284) ~[postgresql-42.3.4.jar:42.3.4]
	at org.postgresql.core.v3.QueryExecutorImpl.execute(QueryExecutorImpl.java:339) ~[postgresql-42.3.4.jar:42.3.4]
	at org.postgresql.jdbc.PgStatement.executeInternal(PgStatement.java:490) ~[postgresql-42.3.4.jar:42.3.4]
	at org.postgresql.jdbc.PgStatement.execute(PgStatement.java:408) ~[postgresql-42.3.4.jar:42.3.4]
	at org.postgresql.jdbc.PgPreparedStatement.executeWithFlags(PgPreparedStatement.java:167) ~[postgresql-42.3.4.jar:42.3.4]
	at org.postgresql.jdbc.PgPreparedStatement.execute(PgPreparedStatement.java:156) ~[postgresql-42.3.4.jar:42.3.4]
	at com.alibaba.druid.pool.DruidPooledPreparedStatement.execute(DruidPooledPreparedStatement.java:497) ~[druid-1.2.8.jar:1.2.8]
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.query(PreparedStatementHandler.java:64) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.query(RoutingStatementHandler.java:79) ~[mybatis-3.5.7.jar:3.5.7]
	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:?]
	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77) ~[?:?]
	at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:?]
	at java.lang.reflect.Method.invoke(Method.java:568) ~[?:?]
	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:64) ~[mybatis-3.5.7.jar:3.5.7]
	at jdk.proxy2.$Proxy222.query(Unknown Source) ~[?:?]
	at org.apache.ibatis.executor.SimpleExecutor.doQuery(SimpleExecutor.java:63) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.executor.BaseExecutor.queryFromDatabase(BaseExecutor.java:325) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.executor.BaseExecutor.query(BaseExecutor.java:156) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:109) ~[mybatis-3.5.7.jar:3.5.7]
	at com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor.intercept(MybatisPlusInterceptor.java:81) ~[mybatis-plus-extension-3.4.3.4.jar:3.4.3.4]
	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:62) ~[mybatis-3.5.7.jar:3.5.7]
	at jdk.proxy2.$Proxy221.query(Unknown Source) ~[?:?]
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:151) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:145) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:140) ~[mybatis-3.5.7.jar:3.5.7]
	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:?]
	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77) ~[?:?]
	at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:?]
	at java.lang.reflect.Method.invoke(Method.java:568) ~[?:?]
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:427) ~[mybatis-spring-2.0.6.jar:2.0.6]
	... 128 more
   ```
5. 导航到驱动中的异常点`org.postgresql.core.v3.SimpleParameterList.checkAllParametersSet(SimpleParameterList.java:284) ~[postgresql-42.3.4.jar:42.3.4]`，发现根本原因果然是参数列表`paramValues`预期两个值，实际只有一个值导致的
   ```java
   @Override
   public void checkAllParametersSet() throws SQLException {
     for (int i = 0; i < paramTypes.length; ++i) {
       if (direction(i) != OUT && paramValues[i] == null) {
         throw new PSQLException(GT.tr("No value specified for parameter {0}.", i + 1),
             PSQLState.INVALID_PARAMETER_VALUE);
       }
     }
   }
   ```
6. 反向查找类变量`paramValues`是何时被赋值的:
   1. `org.postgresql.core.v3.SimpleParameterList#SimpleParameterList[48]`构造方法传入
   2. `org.postgresql.core.v3.SimpleQuery#createParameterList[52]` 由`getBindCount()`计算得到
   3. 由类变量`nativeQuery.bindPositions`的长度乘以批处理数`getBatchSize()`得到
    ```java
    public final int getBindCount() {

        return nativeQuery.bindPositions.length * getBatchSize();
    }
    ```
7. 反向查找类变量`nativeQuery.bindPositions`是何时被赋值的:
   1. `org.postgresql.core.NativeQuery#NativeQuery(java.lang.String, int[], boolean, org.postgresql.core.SqlCommand)[36-37]`构造方法传入
   2. 这个构造使用的方式太多了，在这行打个断点，启动应用后执行SQL触发断点，动态抛出一个异常获取堆栈:
        ```log
        java.lang.Exception
            at org.postgresql.core.NativeQuery.<init>(NativeQuery.java:36)
            <!-- 得到真实的使用位置 -->
            at org.postgresql.core.Parser.parseJdbcSql(Parser.java:295)      
            at org.postgresql.core.CachedQueryCreateAction.create(CachedQueryCreateAction.java:65)
            at org.postgresql.core.CachedQueryCreateAction.create(CachedQueryCreateAction.java:19)
            at org.postgresql.util.LruCache.borrow(LruCache.java:123)
            at org.postgresql.core.QueryExecutorBase.borrowQuery(QueryExecutorBase.java:296)
            at org.postgresql.jdbc.PgConnection.borrowQuery(PgConnection.java:172)
            at org.postgresql.jdbc.PgPreparedStatement.<init>(PgPreparedStatement.java:88)
            <!-- 以下堆栈略 -->
        ```
    3. 发现原参数是`bindPositions`的包装，转而追踪参数`bindPositions`:
        ```java
        NativeQuery lastQuery = new NativeQuery(nativeSql.toString(),
            toIntArray(bindPositions), !splitStatements,       // <= 就是这个toInArray包装
            SqlCommand.createStatementTypeInfo(currentCommandType,
                isBatchedReWriteConfigured, valuesBraceOpenPosition, valuesBraceClosePosition,
                isReturningPresent, (nativeQueries == null ? 0 : nativeQueries.size())));
        ```
    4. 追踪到`org.postgresql.core.Parser#parseJdbcSql`SQL解析器中,读取到SQL中的`?`时的处理方式:
        ```java
        case '?':
          nativeSql.append(aChars, fragmentStart, i - fragmentStart);
          // 有连续两个?的时候，只向sql中拼接一个?，不计入bindPositions中
          if (i + 1 < aChars.length && aChars[i + 1] == '?') /* replace ?? with ? */ {
            nativeSql.append('?');
            i++; // make sure the coming ? is not treated as a bind
          } else {
            // 只有一个?的时候，检查是否解析参数
            if (!withParameters) {
              // 不解析参数
              nativeSql.append('?');
            } else {
              // 解析参数
              if (bindPositions == null) {
                bindPositions = new ArrayList<Integer>();
              }
              bindPositions.add(nativeSql.length());
              int bindIndex = bindPositions.size();
              nativeSql.append(NativeQuery.bindName(bindIndex));
            }
          }
          fragmentStart = i + 1;
          break;
        ```
    5. 把sql中的`?`替换为`??`，问题成功解决。