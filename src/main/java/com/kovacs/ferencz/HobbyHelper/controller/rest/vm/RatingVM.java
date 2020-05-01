package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RatingVM {
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
