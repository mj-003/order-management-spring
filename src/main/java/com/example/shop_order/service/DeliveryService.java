package com.example.shop_order.service;

import com.example.shop_order.entity.Order;
import com.example.shop_order.enums.DeliveryType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for delivery options. Provides available delivery options and dates, calculates delivery cost
 */
@Service
@Transactional
public class DeliveryService {

    /**
     * Returns available delivery options based on the presence of large items in the order
     *
     * @param hasLargeItems true if the order contains large items
     * @return list of available delivery options
     */
    public List<DeliveryType> getAvailableDeliveryOptions(boolean hasLargeItems) {
        List<DeliveryType> options = new ArrayList<>();
        options.add(DeliveryType.COURIER);
        if (!hasLargeItems) {
            options.add(DeliveryType.INPOST);
        }
        options.add(DeliveryType.PICKUP);
        return options;
    }

    /**
     * Returns available delivery dates for the next 7 days
     *
     * @return list of available delivery dates
     */
    public List<LocalDateTime> getAvailableDeliveryDates() {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 7; i++) {
            dates.add(now.plusDays(i).withHour(12).withMinute(0));
        }

        return dates;
    }

    /**
     * Returns available delivery dates for the next 7 days excluding Sundays
     *
     * @return list of available delivery dates
     */
    private boolean isWorkingDay(LocalDateTime date) {
        return date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    /**
     * Calculates delivery cost based on the delivery type
     *
     * @param order order
     * @return delivery cost
     */
    public Double calculateDeliveryCost(Order order) {

        return switch (order.getDeliveryType()) {
            case COURIER -> 20.0;
            case INPOST -> 15.0;
            case PICKUP -> 0.0;
            case LARGE_COURIER -> 259.0;
            default -> 0.0;
        };
    }


}
