# db-router-PangXzz
## 简易路由分库分表starter
### 主要通过哈希散列和mybatis组件实现分库分表，具体过程请看代码
spring.factories绑定了DataSourceAutoConfig的路径，当外部使用该jar包时，外部的yml配置文件会自动导入DataSourceAutoConfig，实现数据源的创建，获得分库分表数据源，以及默认数据源，该项目用了aop对请求进行环绕获取参数，然后对其参数进行哈希散列，将哈希散列后获得的对应值放入ThreadLocal中，进而mybatis开启拦截器对sql语句进行拦截并从ThreadLocal中获得值并将其拼接，当拼接完成后，ThreadLocal会对其值进行remove，防止溢出，最后根据注解进行判断是否需要分表，如果需要分表，会对其表名后添加对应的tbidx，否则只进行分库。
