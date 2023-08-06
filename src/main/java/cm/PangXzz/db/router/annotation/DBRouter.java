package cm.PangXzz.db.router.annotation;

import java.lang.annotation.*;

/**
 * @Author：PangXz
 * @Package：cm.PangXzz.du.router.annotation
 * @name：DBRouter
 * @Date：2023-08-05 14:59
 */


/*
* 定义了一个自定义的注解 DBRouter，用于在 AOP 或其他上下文中指定数据库路由相关的信息。
* */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouter {

    /** 分库分表字段 */
    String key() default "";

}