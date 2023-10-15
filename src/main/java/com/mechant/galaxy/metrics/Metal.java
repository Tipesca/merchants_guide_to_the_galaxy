package com.mechant.galaxy.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Metal {
    private String name;
    private int quantity;
    private int price;
}
