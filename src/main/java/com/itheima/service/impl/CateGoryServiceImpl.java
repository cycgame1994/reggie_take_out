package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import com.itheima.mapper.CategoryMapper;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CateGoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
  @Autowired
  private DishService dishService;
  @Autowired
  private SetmealService setmealService;
  /**
   * 根据id删除分类，删除之前再进行判断
   * @param id
   */
  @Override
  public void remove(Long id) {
    LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
    dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
    long count1 = dishService.count(dishLambdaQueryWrapper);
    //判断当前分类是否关联了菜品，如果已经关联，抛出一个异常
    if (count1 > 0) {
      //已经关联，抛出业务异常
      throw new CustomException("当前分类关联了菜品，不能删除");
    }
    //查询当前分类是否关联的套餐，如果已经关联，抛出一个异常
    LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
    setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
    long count2 = setmealService.count(setmealLambdaQueryWrapper);
    if (count2 > 0) {
      throw new CustomException("当前关联了套餐，不能删除");
    }


    //正常删除
    super.removeById(id);
  }
}
