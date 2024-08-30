package com.workintech.s18d1.dao;

import com.workintech.s18d1.entity.BreadType;
import com.workintech.s18d1.entity.Burger;
import java.util.List;


public interface BurgerDao {
    Burger save(Burger burger);

    Burger findById(Long id);

    List<Burger> findAll();

    List<Burger> findByPrice(Double price);

    List<Burger> findByBreadType(BreadType breadType);

    List<Burger> findByContent(String contents);

    Burger update(Burger burger);

    Burger remove(Long id);
}
