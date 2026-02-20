package com.sit.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sit.ruiji.common.R;
import com.sit.ruiji.dto.DishDto;
import com.sit.ruiji.entity.Category;
import com.sit.ruiji.entity.Dish;
import com.sit.ruiji.entity.DishFlavor;
import com.sit.ruiji.service.CategoryService;
import com.sit.ruiji.service.DishFlavorService;
import com.sit.ruiji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        //新增菜品后，需要清除缓存
        String key = "dish_"+dishDto.getCategoryId()+"status_1";
        redisTemplate.delete(key);

        return R.success("新增成功");
    }

    /**
     * 菜品分页查询功能
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //构造分页器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo);

        //进行对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");

        //利用records中存储的数据
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long id = item.getCategoryId();
            Category category = categoryService.getById(id);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);


        return R.success(dishDtoPage);
    }

    /**
     * 修改菜品信息功能
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return  R.success(dishDto);
    }

    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        //对菜品数据进行更新后，需要清除缓存
        String key = "dish_"+dishDto.getCategoryId()+"status_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
//        queryWrapper.eq(Dish::getStatus,1);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    /**
     * 增强list方法
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        //将菜品种类id作为缓存的key，这里采用动态拼接
        String key = "dish_"+dish.getCategoryId()+"status_"+dish.getStatus();

        //从Redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null){
            //如果缓存不为空，则返回数据
            return R.success(dishDtoList);
        }

        //如果缓存数据为空，查询数据库
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        queryWrapper.eq(Dish::getStatus,1);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long id = item.getCategoryId();
            Category category = categoryService.getById(id);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //增加口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        //缓存数据不存在，查询数据库后，将其存入缓存
        redisTemplate.opsForValue().set(key, dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
