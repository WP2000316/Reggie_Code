package com.sit.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sit.ruiji.common.CustomException;
import com.sit.ruiji.dto.SetmealDto;
import com.sit.ruiji.entity.Setmeal;
import com.sit.ruiji.entity.SetmealDish;
import com.sit.ruiji.mapper.SetmealMapper;
import com.sit.ruiji.service.SetmealDishService;
import com.sit.ruiji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，即操作Setmeal表，进行insert操作
        //通过save方法后，setmealDto中的id属性(即Setmeal的id值)已经进行了赋值
        this.save(setmealDto);

        //保存套餐对应的菜品种类信息，即操作setmeal_dish表，进行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //删除套餐功能，只有停售状态的套餐可以删除
        //删除套餐需要操作两张表，一张为setmeal表，一张为setmeal_dish表

        //查询套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);

        //如果不能删除，抛出一个业务异常
        if (count > 0){
            throw new CustomException("套餐售卖中，不能删除");
        }

        //如果可以删除，对setmeal表进行delete操作
        this.removeByIds(ids);

        //如果可以删除，对setmeal_dish表进行delete操作
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);
    }
}
