package learning.pra.ioc;

/**
 * 业务逻辑层示例 Bean：依赖注入 {@link OrderRepository} 的"依赖方"。
 *
 * <p>同时标注 {@code @Component} + {@code @Singleton}：既被容器管理，又只创建一次。
 * {@code repository} 字段标 {@code @Inject}，由容器在创建后通过反射塞入实例
 * （对应概念点 3 的 injectDependencies）。
 *
 * @see MiniContainer
 * @see Inject
 * @see OrderRepository
 */
@Component
@Singleton
public class OrderService {

    /**
     * 容器注入的依赖：数据访问组件。
     * 字段标 {@code @Inject}，创建时由 {@link MiniContainer} 反射赋值。
     */
    @Inject
    private OrderRepository repository;

    /**
     * 创建一笔订单——真正使用注入的依赖（黑盒验证注入是否成功）。
     *
     * <p>若注入失败（repository 为 null），此处会抛 NullPointerException。
     *
     * @param item 订单内容，如"咖啡"
     * @return {@code repository.save(item)} 的返回结果
     */
    public String createOrder(String item) {
        return repository.save(item);
    }
}
