package com.sit.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sit.ruiji.entity.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
