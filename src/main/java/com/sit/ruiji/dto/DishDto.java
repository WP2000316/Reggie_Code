package com.sit.ruiji.dto;

import com.sit.ruiji.entity.Dish;
import com.sit.ruiji.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
