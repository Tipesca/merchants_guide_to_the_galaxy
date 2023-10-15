package com.mechant.galaxy.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Symbol {
    private String intergalacticUnit;
    private int value;
    private int repeated;
    private List<String> subtracted;
}
