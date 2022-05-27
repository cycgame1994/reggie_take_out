package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishMapper;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
  @Autowired
  private DishFlavorService dishFlavorService;
  /**
   * 新增菜品，添加口味数据
   * @param dishDto
   */
  @Override
  @Transactional
  public void saveWithFlavor(DishDto dishDto) {
    //保存菜品的基本信息到菜品表dish
    this.save(dishDto);
    Long dishId = dishDto.getId();
    //菜品口味
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors = flavors.stream().map((item) -> {
      item.setDishId(dishId);
      return item;
    }).collect(Collectors.toList());

    //菜品口味数据到菜品口味表
    dishFlavorService.saveBatch(flavors);

  }

  /**
   *
   * @param id
   * @return
   */
  @Override
  public DishDto getByIdWithFlavor(Long id) {
    //查询菜品基本信息
    Dish dish = this.getById(id);
    DishDto dishDto=new DishDto();
    BeanUtils.copyProperties(dish,dishDto );
    //查询口味信息
    LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId,dish.getId());
    List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
    dishDto.setFlavors(flavors);
    return dishDto;
  }

  @Override
  @Transactional
  public void updateWithFlavor(DishDto dishDto) {
    //更新dish表
    this.updateById(dishDto);
    //清理当前菜品对应的口味数据
    LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper();
    queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
    dishFlavorService.remove(queryWrapper);
    //添加当前提交过来的口味数据
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors = flavors.stream().map((item) -> {
      item.setDishId(dishDto.getId());
      return item;
    }).collect(Collectors.toList());
    dishFlavorService.saveBatch(flavors);

  }

}
