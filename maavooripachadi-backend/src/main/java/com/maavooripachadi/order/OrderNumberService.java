package com.maavooripachadi.order;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class OrderNumberService {
    private static final Pattern SUFFIX_PATTERN = Pattern.compile(".*-(\\d+)$");

    private final OrderRepository orders;
    private final AtomicInteger counter = new AtomicInteger(0);
    private volatile LocalDate counterDate;

    public OrderNumberService(OrderRepository orders){
        this.orders = orders;
    }

    @PostConstruct
    void init(){
        resetForDate(LocalDate.now());
    }

    public synchronized String next(){
        LocalDate today = LocalDate.now();
        if (counterDate == null || !counterDate.equals(today)) {
            resetForDate(today);
        }
        int sequence = counter.incrementAndGet();
        return formatOrderNumber(today, sequence);
    }

    private void resetForDate(LocalDate date){
        String prefix = prefixForDate(date);
        int seed = orders
            .findFirstByOrderNoStartingWithOrderByOrderNoDesc(prefix)
            .map(Order::getOrderNo)
            .map(this::extractSuffix)
            .orElse(0);
        counter.set(seed);
        counterDate = date;
    }

    private String prefixForDate(LocalDate date){
        return String.format("ORD-%04d%02d%02d-", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    private String formatOrderNumber(LocalDate date, int sequence){
        return String.format("%s%06d", prefixForDate(date), sequence);
    }

    private int extractSuffix(String orderNo){
        Matcher matcher = SUFFIX_PATTERN.matcher(orderNo);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}
