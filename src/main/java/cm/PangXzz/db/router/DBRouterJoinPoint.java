package cm.PangXzz.db.router;

import cm.PangXzz.db.router.annotation.DBRouter;
import cm.PangXzz.db.router.strategy.IDBRouterStrategy;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author：PangXz
 * @Package：cm.PangXzz.du.router
 * @name：DBRouterJoinPoint
 * @Date：2023-08-05 14:25
 */
public class DBRouterJoinPoint {
    private Logger logger = LoggerFactory.getLogger(DBRouterJoinPoint.class);

    private DBRouterConfig dbRouterConfig;

    private IDBRouterStrategy dbRouterStrategy;

    public DBRouterJoinPoint(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {

        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }
    /*
    * 标注了 @DBRouter 注解的方法会成为切点。
    * */
    @Pointcut("@annotation(cm.PangXzz.db.router.annotation.DBRouter)")
    public void aopPoint(){}

    @Around("aopPoint() && @annotation(DBRouter)")
    public Object dbRouter(ProceedingJoinPoint jp, DBRouter dbRouter) throws Throwable {
        String dbKey = dbRouter.key();
        //dbkey=>dbRouter.key() 确定根据哪个字段进行路由
        if (StringUtils.isBlank(dbKey) && StringUtils.isBlank(dbRouterConfig.getRouterKey())) {
            throw new RuntimeException("annotation DBRouter key is null！");
        }
        dbKey = StringUtils.isNotBlank(dbKey) ? dbKey : dbRouterConfig.getRouterKey();
        //也可以通过配置其他，比如默认是uid，传进来了[{"ZKB","123","18"}]
        String dbKeyAttr = getAttrValue(dbKey, jp.getArgs());
        dbRouterStrategy.doRouter(dbKeyAttr);
        try{
            return jp.proceed();
        }finally {
            dbRouterStrategy.clear();
        }
    }
    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

        /*
        * getAttrValue 方法：这个方法接收两个参数，attr 表示属性名，args 表示方法的参数数组。
        * 首先，它检查参数数量，如果只有一个参数且是字符串类型，就直接将其转换为字符串并返回。
        * 如果参数数量不止一个或第一个参数不是字符串类型，那么就进入循环遍历参数。
        * 在循环中，使用 getValueByName 方法尝试获取每个参数对象的指定属性值，属性名就是传入的 attr。
        * 如果成功获取属性值，就将其赋值给 filedValue 变量。如果 filedValue 已经被赋值，就跳出循环。
        * 最后，返回获取到的属性值（或者为 null，如果没有找到属性值）。
        * */
        public String getAttrValue(String attr, Object[] args) {
            if (1 == args.length) {
                Object arg = args[0];
                if (arg instanceof String) {
                    return arg.toString();
                }
            }

            String filedValue = null;
            for (Object arg : args) {
                try {
                    if (StringUtils.isNotBlank(filedValue)) {
                        break;
                    }
                    // filedValue = BeanUtils.getProperty(arg, attr);
                    // fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
                    filedValue = String.valueOf(this.getValueByName(arg, attr));
                } catch (Exception e) {
                    logger.error("获取路由属性值失败 attr：{}", attr, e);
                }
            }
            return filedValue;
        }

    /**
     * 获取对象的特定属性值
     *
     * @author tang
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @author tang
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
