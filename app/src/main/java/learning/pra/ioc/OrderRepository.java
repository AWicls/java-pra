package learning.pra.ioc;

@Component
public class OrderRepository {
    public String save(String item) {
        return "已保存:" + item;
    }
}
