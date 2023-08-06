package cm.PangXzz.db.router;

/**
 * @Author：PangXz
 * @Package：cm.PangXzz.du.router
 * @name：DBContextHolder
 * @Date：2023-08-05 14:20
 */
public class DBContextHolder {
    private static final ThreadLocal<String> dbKey = new ThreadLocal<String>();
    private static final ThreadLocal<String> tbKey = new ThreadLocal<String>();
    public static void setDBKey(String dbKeyIdx){
        dbKey.set(dbKeyIdx);
    }

    public static String getDBKey(){
        return dbKey.get();
    }

    public static void setTBKey(String tbKeyIdx){
        tbKey.set(tbKeyIdx);
    }

    public static String getTBKey(){
        return tbKey.get();
    }
    //remove方法来清理线程局部变量，避免内存泄漏。
    public static void clearDBKey(){
        dbKey.remove();
    }

    public static void clearTBKey(){
        tbKey.remove();
    }

}
