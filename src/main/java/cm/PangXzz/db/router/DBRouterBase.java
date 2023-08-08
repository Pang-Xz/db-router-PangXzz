package cm.PangXzz.db.router;

/**
 * @description: 数据源基础配置
 * @Author：PangXz
 * @Package：cm.PangXzz.du.router
 * @name：DBRouterBase
 * @Date：2023-08-05 14:19
 */

public class DBRouterBase {

    private String tbIdx;

    public String getTbIdx() {
        return DBContextHolder.getTBKey();
    }
}
