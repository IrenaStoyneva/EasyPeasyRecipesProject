package com.softuni.easypeasyrecipes_app.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RatingDto {
    @Min(1)
    @Max(5)
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
