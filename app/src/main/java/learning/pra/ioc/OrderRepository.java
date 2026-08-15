package learning.pra.ioc;

/**
 * 数据访问层示例 Bean：被 {@link OrderService} 依赖注入的"被依赖方"。
 *
 * <p>标注 {@code @Component}（非单例），由容器管理。本身没有 {@code @Inject} 字段，
 * 所以容器对它做注入时是"空转"（遍历不到标了 @Inject 的字段，什么都不做）。
 *
 * @see OrderService
 */
@Component
public class OrderRepository {

    /**
     * 保存一条订单记录（示例方法：真正"用上"注入依赖的验证点）。
     *
     * @param item 订单内容，如"咖啡"
     * @return 保存结果文案，形如 {@code "已保存:咖啡"}
     */
    public String save(String item) {
        return "已保存:" + item;
    }
}
