package com.jgdabc.dto;

import com.jgdabc.entity.Setmeal;
import com.jgdabc.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes; //菜品

    private String categoryName;//分类
}
