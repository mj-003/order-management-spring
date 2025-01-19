package com.example.shop_order.service;

import com.example.shop_order.entity.Order;
import com.example.shop_order.enums.DeliveryType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class DeliveryService {

    public List<DeliveryType> getAvailableDeliveryOptions(boolean hasLargeItems) {
        List<DeliveryType> options = new ArrayList<>();
        options.add(DeliveryType.COURIER);
        if (!hasLargeItems) {
            options.add(DeliveryType.INPOST);
        }
        options.add(DeliveryType.PICKUP);
        return options;
    }

    public List<LocalDateTime> getAvailableDeliveryDates() {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Dodaj następne 7 dni jako dostępne daty
        for (int i = 1; i <= 7; i++) {
            dates.add(now.plusDays(i).withHour(12).withMinute(0));
        }

        return dates;
    }

    private boolean isWorkingDay(LocalDateTime date) {
        return date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    public Double calculateDeliveryCost(Order order) {
        if (order.getDeliveryType() == DeliveryType.PICKUP) {
            return 10.0;
        }

        double baseCost = switch (order.getDeliveryType()) {
            case COURIER -> 20.0;
            case INPOST -> 15.0;
            default -> 0.0;
        };

        if (Boolean.TRUE.equals(order.getHomeDelivery())) {
            baseCost = baseCost + 230.0;
        }

        return baseCost;
    }
}
