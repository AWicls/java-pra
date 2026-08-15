package learning.pra.ioc;

@Component
@Singleton
public class OrderService {

    @Inject
    private OrderRepository repository;

    public String createOrder(String item) {
        return repository.save(item);
    }

}
