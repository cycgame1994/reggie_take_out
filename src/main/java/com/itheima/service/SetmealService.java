package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
  /**
   * 新增套餐
   * @param setmealDto
   */
  public void saveWithDish(SetmealDto setmealDto);

  public void removeWithDish(List<Long> ids);
}
