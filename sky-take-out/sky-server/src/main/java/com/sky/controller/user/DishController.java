package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Tag(name = "菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        // 构造redis中的key，规则：dish_分类id
        // String cacheKey = "dish_" + categoryId;
        
        // 查询redis中是否存在菜品数据
        // List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(cacheKey);
        // if (list != null && list.size() > 0) {
        //     // 如果存在，直接返回，无须查询数据库
        //     return Result.success(list);
        // }
        
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        // 如果不存在，查询数据库，将查询到的数据放入redis中
        List<DishVO> list = dishService.listWithFlavor(dish);
        // redisTemplate.opsForValue().set(cacheKey, list);

        return Result.success(list);
    }

}