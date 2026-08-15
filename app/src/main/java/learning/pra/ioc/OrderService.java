package learning.pra.ioc;

@Component
@Singleton
public class OrderService {

    @Inject
    private OrderRepository repository;

}
