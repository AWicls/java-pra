package learning.pra.stream;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StreamAdvLab {

    // groupingBy + counting：按区域分组，每组数有几笔订单
    public static Map<String, Long> countByRegion(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(p -> p.region(), Collectors.counting()));
    }

    // groupingBy + summingInt：按商品分组，累加每组销售额
    public static Map<String, Integer> sumByProduct(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(p -> p.product(),
                        Collectors.summingInt(p -> p.amount())));
    }

    // 按是否 >= threshold 分成达标/未达标两桶（>= 含等于 threshold）
    public static Map<Boolean, List<Sale>> partitionByAmount(List<Sale> sales, int threshold) {
        return sales.stream()
                .collect(Collectors.groupingBy(p -> p.amount() >= threshold));
    }

    // flatMap：每个箱子的标签流拍平成一个大 List
    public static List<String> flattenTags(List<TaggedBox> boxes) {
        return boxes.stream().flatMap(t -> t.tags().stream()).toList();
    }

    // flatMap + distinct：拍平后去重，跨箱子重复标签只留一个
    public static List<String> distinctTags(List<TaggedBox> boxes) {
        return boxes.stream().flatMap(t -> t.tags().stream()).distinct().toList();
    }

    // distinct + joining：商品名去重后用 ", " 拼接成一个字符串
    public static String joinProducts(List<Sale> sales) {
        return sales.stream()
                .map(Sale::product).distinct().collect(Collectors.joining(", "));
    }

    // groupingBy + mapping：按区域分组，每组只保留商品名（不保留整个 Sale）
    public static Map<String, List<String>> productsByRegion(List<Sale> sales) {
        return sales.stream()
                .collect(
                        Collectors.groupingBy(Sale::region,
                                Collectors.mapping(Sale::product,
                                        Collectors.toList())));
    }

    // groupingBy + collectingAndThen：按区域分组求和，收尾把 Integer 保持为 Integer
    public static Map<String, Integer> totalAmountBuRegion(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(Sale::region,
                        Collectors.collectingAndThen(Collectors.summingInt(Sale::amount),
                                Integer::intValue)));
    }

    // parallelStream + reduce：并行求和（Integer::sum 满足结合律，并行安全）
    public static int parallelSum(List<Integer> nums) {
        return nums.parallelStream()
                .reduce(0, Integer::sum);
    }

    // stream + reduce：串行求和，与 parallelSum 对照
    public static int serialSum(List<Integer> nums) {
        return nums.stream()
                .reduce(0, Integer::sum);
    }

    // parallelStream + sorted：并行排序后逆序（并行只影响处理，不影响排序结果）
    public static List<Integer> parallelSortedDesc(List<Integer> nums) {
        return nums.parallelStream()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    // 自定义 Collector：把 Integer 流收成平均值
    // supplier=造 int[]{count,sum}；accumulator=累加；combiner=并行合并；finisher=算均值
    public static Collector<Integer, ?, Double> averageCollector() {
        return Collector.of(() -> new int[] { 0, 0 },
                (int[] acc, Integer t) -> {
                    acc[0]++;
                    acc[1] += t;
                }, (a, b) -> {
                    a[0] += b[0];
                    a[1] += b[1];
                    return a;
                }, (acc) -> {
                    if (acc[0] == 0) {
                        return 0.0;
                    }
                    return (double) acc[1] / acc[0];
                });
    }

}

record Sale(String region, String product, int amount) {
};

record TaggedBox(String region, List<String> tags) {
};
