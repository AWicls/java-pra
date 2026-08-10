package learning.pra.stream;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamAdvLab {

    public static Map<String, Long> countByRegion(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(p -> p.region(), Collectors.counting()));
    }

    public static Map<String, Integer> sumByProduct(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(p -> p.product(),
                        Collectors.summingInt(p -> p.amount())));
    }

    public static Map<Boolean, List<Sale>> partitionByAmount(List<Sale> sales, int threshold) {
        return sales.stream()
                .collect(Collectors.groupingBy(p -> p.amount() >= threshold));
    }

    public static List<String> flattenTags(List<TaggedBox> boxes) {
        return boxes.stream().flatMap(t -> t.tags().stream()).toList();
    }

    public static List<String> distinctTags(List<TaggedBox> boxes) {
        return boxes.stream().flatMap(t ->t.tags().stream()).distinct().toList();
    }

}

record Sale(String region, String product, int amount) {};

record TaggedBox(String region, List<String> tags){};
