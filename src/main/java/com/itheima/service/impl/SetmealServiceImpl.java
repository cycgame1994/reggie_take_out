package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.mapper.SetmealMapper;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
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
    //保存套餐的基本信息
    this.save(setmealDto);
    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
     setmealDishes.stream().map((item) -> {
      item.setSetmealId(setmealDto.getId());
      return item;
    }).collect(Collectors.toList());

    //保存关联xinxi
    setmealDishService.saveBatch(setmealDishes);
  }

  @Override
  @Transactional
  public void removeWithDish(List<Long> ids) {
    //查询套餐状态
    LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.in(Setmeal::getId,ids);
    queryWrapper.eq(Setmeal::getStatus,1);
    long count = this.count(queryWrapper);
    if (count > 0) {
      //如果不能删除，抛出一个业务异常
      throw new CustomException("套餐正在售卖中");
    }


    //如果可以删除，先删除套餐中的数据
    this.removeByIds(ids);

    //删除关系表中的数据
    LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
    lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
    //删除关系表中的数据
    setmealDishService.remove(lambdaQueryWrapper);

  }
}
