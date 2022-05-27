package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
  @Autowired
  private SetmealService setmealService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private SetmealDishService setmealDishService;

  @PostMapping
  public R<String> save(@RequestBody SetmealDto setmealDto) {
    log.info("套餐信息{}",setmealDto);
    setmealService.saveWithDish(setmealDto);
    return R.success("新增套餐成功");
  }

  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    Page<Setmeal> pageInfo=new Page<>(page,pageSize);
    Page<SetmealDto> dtoPage=new Page<>();
    LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.like(name!=null,Setmeal::getName,name);
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    setmealService.page(pageInfo,queryWrapper);

    BeanUtils.copyProperties(pageInfo,dtoPage,"records");
    List<Setmeal> records = pageInfo.getRecords();
    List<SetmealDto> list=records.stream().map((item)->{
      SetmealDto setmealDto=new SetmealDto();
      BeanUtils.copyProperties(item,setmealDto);
      Long categoryId = item.getCategoryId();
      Category category = categoryService.getById(categoryId);
      if (category != null) {
        String categoryName = category.getName();
        setmealDto.setCategoryName(categoryName);
      }
      return setmealDto;
    }).collect(Collectors.toList());
    dtoPage.setRecords(list);

    return R.success(dtoPage);
  }

  @DeleteMapping
  public R<String> delete(@RequestParam List<Long> ids) {
    log.info("ids:{}",ids);
    setmealService.removeWithDish(ids);
    return R.success("套餐数据删除成功");
  }

}
