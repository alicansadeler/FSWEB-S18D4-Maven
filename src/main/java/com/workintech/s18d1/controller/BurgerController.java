package com.workintech.s18d1.controller;

import com.workintech.s18d1.dao.BurgerDao;
import com.workintech.s18d1.entity.BreadType;
import com.workintech.s18d1.entity.Burger;
import com.workintech.s18d1.exceptions.BurgerException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/burger")
public class BurgerController {

    private BurgerDao burgerDao;

    @Autowired
    public BurgerController(BurgerDao burgerDao) {
        this.burgerDao = burgerDao;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Burger> getAllBurgers() {
      return burgerDao.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Burger getBurger(@PathVariable Long id) {
        if (id <= 0) {
            throw new BurgerException("ID değer 0 ve negatif olamaz. ID: " + id, HttpStatus.BAD_REQUEST);
        }
        return burgerDao.findById(id);
    }


    @GetMapping("/price/{price}")
    @ResponseStatus(HttpStatus.OK)
    public List<Burger> getBurgerByPrice(@PathVariable Double price) {
        if (price <= 0) {
            throw new BurgerException("Price değeri 0 ve negatif olamaz. Price: " + price, HttpStatus.BAD_REQUEST);
        }
        return burgerDao.findByPrice(price);
    }

    @GetMapping("/breadType/{breadType}")
    @ResponseStatus(HttpStatus.OK)
    public List<Burger> getBurgerByBreadType(@PathVariable BreadType breadType) {
        return burgerDao.findByBreadType(breadType);
    }

    @GetMapping("/content/{content}")
    @ResponseStatus(HttpStatus.OK)
    public List<Burger> getBurgerContent(@PathVariable String content) {
        return burgerDao.findByContent(content);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public Burger postBurger(@RequestBody Burger burger) {
        return burgerDao.save(burger);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public Burger updateBurger(@RequestBody Burger burger) {
        return burgerDao.update(burger);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public Burger deleteBurger(@PathVariable Long id) {

        return   burgerDao.remove(id);

    }


}
