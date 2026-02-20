package com.sit.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sit.ruiji.common.CustomException;
import com.sit.ruiji.entity.Category;
import com.sit.ruiji.entity.Dish;
import com.sit.ruiji.entity.Setmeal;
import com.sit.ruiji.mapper.CategoryMapper;
import com.sit.ruiji.service.CategoryService;
import com.sit.ruiji.service.DishService;
import com.sit.ruiji.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id进行菜品种类删除，在删除前判断种类是否关联菜品和套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);

        //查询当前套餐是否关联菜品，如果关联，抛出一个业务异常
        if (dishCount > 0){
            //已关联菜品
            throw new CustomException("已关联菜品，无法删除");
        }

        //查询当前套餐是否关联套餐，如果关联，抛出一个业务异常
        if (setmealCount > 0){
            //已关联套餐
            throw new CustomException("已关联套餐，无法删除");
        }

        //没有关联，正常删除
        super.removeById(id);
    }
}
