package com.sit.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sit.ruiji.dto.SetmealDto;
import com.sit.ruiji.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
