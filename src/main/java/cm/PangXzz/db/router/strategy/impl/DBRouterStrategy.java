package cm.PangXzz.db.router.strategy.impl;

import cm.PangXzz.db.router.DBContextHolder;
import cm.PangXzz.db.router.DBRouterConfig;
import cm.PangXzz.db.router.strategy.IDBRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author：PangXz
 * @Package：cm.PangXzz.du.router.strategy.impl
 * @name：DBRouterStrategy
 * @Date：2023-08-05 14:27
 */
public class DBRouterStrategy implements IDBRouterStrategy {
    private Logger logger = LoggerFactory.getLogger(DBRouterStrategy.class);

    private DBRouterConfig dbRouterConfig;

    public DBRouterStrategy(DBRouterConfig dbRouterConfig) {
        this.dbRouterConfig = dbRouterConfig;
    }

    @Override
    public void doRouter(String dbKeyAttr) {
        int size=dbRouterConfig.getDbCount()*dbRouterConfig.getTbCount();
        // 扰动函数；在 JDK 的 HashMap 中，对于一个元素的存放，需要进行哈希散列。而为了让散列更加均匀，所以添加了扰动函数。扩展学习；https://mp.weixin.qq.com/s/CySTVqEDK9-K1MRUwBKRCg
        int idx = (size - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));
        //公式:计算存放的位置 例如idx=5 所以在二库一表
        int dbIdx=idx/dbRouterConfig.getTbCount()+1;  //5/4+1=2
        int tbIdx=idx-dbRouterConfig.getTbCount()*(dbIdx-1); //5-(4*(5-1))=1
        //放到ThreadLocal中
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
        DBContextHolder.setTBKey(String.format("%03d", tbIdx));
        logger.debug("数据库路由 dbIdx：{} tbIdx：{}",  dbIdx, tbIdx);
    }

    @Override
    public void setDBKey(int dbIdx) {
        /*
        * String.format("%02d", dbIdx) 是一个用于格式化字符串的方法，
        * 它的作用是将一个整数 dbIdx 格式化为固定宽度为 2 位的字符串，
        * 不足 2 位的整数会在前面补零。*/
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
    }

    @Override
    public void setTBKey(int tbIdx) {
        DBContextHolder.setTBKey(String.format("%03d", tbIdx));
    }

    @Override
    public int dbCount() {
        return dbRouterConfig.getDbCount();
    }

    @Override
    public int tbCount() {
        return dbRouterConfig.getTbCount();
    }

    @Override
    public void clear() {
        DBContextHolder.clearDBKey();
        DBContextHolder.clearTBKey();
    }
}
