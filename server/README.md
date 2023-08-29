# 服务端开发指南

## 数据库

推荐使用 [Docker](https://www.docker.com/) 运行数据库。

```shell
docker run -d \
   --name db-for-ntoj \
   -e POSTGRES_USER=ntoj \
   -e POSTGRES_PASSWORD=123456 \
   -e POSTGRES_DB=ntoj \
   -e PGDATA=/var/lib/postgresql/data/pgdata \
   -e TZ=Asia/Shanghai \
   -e PGTZ=Asia/Shanghai \
   -e LANG=en_US.UTF-8 \
   -p 15432:5432 \
   postgres:15
```

## 数据库迁移

后端使用了 [Flyway](https://flywaydb.org/) 进行数据库迁移，迁移脚本位于 `server/src/main/resources/db/migration` 目录下。
同时，启动时，[Spring Data JPA](https://spring.io/projects/spring-data-jpa) 会对于数据库结构进行验证，如果数据库结构与实体类不一致，
会抛出异常。例如：

```log
2023-08-29T18:56:04.656+08:00 ERROR 31400 --- [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Failed to initialize JPA EntityManagerFactory: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing table [t_contests_languages]
2023-08-29T18:56:04.656+08:00  WARN 31400 --- [  restartedMain] ConfigServletWebServerApplicationContext : Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing table [t_contests_languages]
2023-08-29T18:56:04.657+08:00  INFO 31400 --- [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2023-08-29T18:56:04.659+08:00  INFO 31400 --- [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
2023-08-29T18:56:04.664+08:00  INFO 31400 --- [  restartedMain] o.apache.catalina.core.StandardService   : Stopping service [Tomcat]
2023-08-29T18:56:04.671+08:00  INFO 31400 --- [  restartedMain] .s.b.a.l.ConditionEvaluationReportLogger : 

Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
2023-08-29T18:56:04.681+08:00 ERROR 31400 --- [  restartedMain] o.s.boot.SpringApplication               : Application run failed

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing table [t_contests_languages]
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1770) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:598) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:520) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:326) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:324) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1132) ~[spring-context-6.0.8.jar:6.0.8]
    at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:907) ~[spring-context-6.0.8.jar:6.0.8]
    at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:584) ~[spring-context-6.0.8.jar:6.0.8]
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146) ~[spring-boot-3.0.6.jar:3.0.6]
    at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:732) ~[spring-boot-3.0.6.jar:3.0.6]
    at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:434) ~[spring-boot-3.0.6.jar:3.0.6]
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:310) ~[spring-boot-3.0.6.jar:3.0.6]
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1304) ~[spring-boot-3.0.6.jar:3.0.6]
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1293) ~[spring-boot-3.0.6.jar:3.0.6]
    at zip.ntoj.server.ServerApplicationKt.main(ServerApplication.kt:13) ~[main/:na]
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:na]
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77) ~[na:na]
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:na]
    at java.base/java.lang.reflect.Method.invoke(Method.java:568) ~[na:na]
    at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49) ~[spring-boot-devtools-3.0.6.jar:3.0.6]
Caused by: jakarta.persistence.PersistenceException: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing table [t_contests_languages]
    at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:421) ~[spring-orm-6.0.8.jar:6.0.8]
    at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:396) ~[spring-orm-6.0.8.jar:6.0.8]
    at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:352) ~[spring-orm-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1816) ~[spring-beans-6.0.8.jar:6.0.8]
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1766) ~[spring-beans-6.0.8.jar:6.0.8]
    ... 21 common frames omitted
Caused by: org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing table [t_contests_languages]
    at org.hibernate.tool.schema.internal.AbstractSchemaValidator.validateTable(AbstractSchemaValidator.java:133) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.tool.schema.internal.GroupedSchemaValidatorImpl.validateTables(GroupedSchemaValidatorImpl.java:46) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.tool.schema.internal.AbstractSchemaValidator.performValidation(AbstractSchemaValidator.java:96) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.tool.schema.internal.AbstractSchemaValidator.doValidation(AbstractSchemaValidator.java:74) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.performDatabaseAction(SchemaManagementToolCoordinator.java:293) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.lambda$process$5(SchemaManagementToolCoordinator.java:143) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at java.base/java.util.HashMap.forEach(HashMap.java:1421) ~[na:na]
    at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.process(SchemaManagementToolCoordinator.java:140) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.internal.SessionFactoryImpl.<init>(SessionFactoryImpl.java:336) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.boot.internal.SessionFactoryBuilderImpl.build(SessionFactoryBuilderImpl.java:415) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1423) ~[hibernate-core-6.1.7.Final.jar:6.1.7.Final]
    at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:66) ~[spring-orm-6.0.8.jar:6.0.8]
    at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:376) ~[spring-orm-6.0.8.jar:6.0.8]
    at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:409) ~[spring-orm-6.0.8.jar:6.0.8]
    ... 25 common frames omitted
```

项目引入了 [Flyway Gradle 插件](https://plugins.gradle.org/plugin/org.flywaydb.flyway)，
可以使用 `./gradlew :server:flywayMigrate` 命令进行数据库迁移。
