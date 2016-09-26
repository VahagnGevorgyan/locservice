package com.locservice.utils;

import com.locservice.api.entities.Order;

import java.util.Comparator;

public class OrderHistoryComparator implements Comparator<Order> {

    @Override
    public int compare(Order lhs, Order rhs) {
        if (lhs.getStatus().equals(rhs.getStatus())) {
            return 0;
        }
        return getStatusCode(lhs) - getStatusCode(rhs);
    }

    private int getStatusCode(Order order) {
        switch (order.getStatus()) {
            case "CC"://Gray
            case "NC":
            case "BR":
                return 1;
            case "CP"://White
                return 0;
            case "R": // Orange
                return -4;
            case "OW"://Blue
                return -3;
            case "A"://Green
                return -2;
            case "RC":// Yellow
                return -1;
            default:
                return 0;
        }
    }
}
