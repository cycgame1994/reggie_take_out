package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
  @Autowired
  private DishService dishService;
  @Autowired
  private DishFlavorService dishFlavorService;
  @Autowired
  private CategoryService categoryService;

  @PostMapping
  public R<String> save(@RequestBody DishDto dishDto) {
    log.info(dishDto.toString());
    dishService.saveWithFlavor(dishDto);
    return R.success("新增菜品成功");
  }

  /**
   * 菜品信息查询
   *
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    //分页构造器
    Page<Dish> pageInfo = new Page<>(page, pageSize);
    Page<DishDto> dishDtoPage = new Page<>();
    //条件构造器
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name != null, Dish::getName, name);
    //添加排序条件
    queryWrapper.orderByDesc(Dish::getUpdateTime);
    dishService.page(pageInfo, queryWrapper);

    //对象拷贝
    BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
    List<Dish> records = pageInfo.getRecords();
    List<DishDto> list = records.stream().map((item) -> {
      DishDto dishDto = new DishDto();
      BeanUtils.copyProperties(item, dishDto);
      Long categoryId = item.getCategoryId();
      Category category = categoryService.getById(categoryId);
      if (category != null) {

        String categoryName = category.getName();
        dishDto.setCategoryName(categoryName);
      }
      return dishDto;
    }).collect(Collectors.toList());

    dishDtoPage.setRecords(list);

    return R.success(dishDtoPage);
  }

  /**
   * 根据id来查询菜品信息和口味
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<DishDto> get(@PathVariable Long id) {
    DishDto dishDto = dishService.getByIdWithFlavor(id);
    return R.success(dishDto);
  }

  @PutMapping
  public R<String> update(@RequestBody DishDto dishDto) {
    log.info(dishDto.toString());
    dishService.updateWithFlavor(dishDto);
    return R.success("修改菜品成功");
  }

  /**
   * 根据条件查询对应的
   * @param dish
   * @return
   */
  @GetMapping("/list")
  public R<List<Dish>> list(Dish dish) {
    //构造查询条件
    LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    queryWrapper.eq(Dish::getStatus,1);
    //排序条件
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    List<Dish> list = dishService.list(queryWrapper);

    return R.success(list);
  }



}
