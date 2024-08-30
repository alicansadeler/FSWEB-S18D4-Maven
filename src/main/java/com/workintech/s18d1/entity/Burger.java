package com.workintech.s18d1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "burger", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Burger {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(name = "name")
     private String name;

     @Column(name = "price")
     private Double price;

     @Column(name = "vegan")
     private Boolean isVegan;

     @Enumerated(EnumType.STRING)
     @Column(name = "bread_type")
     private BreadType breadType;


     @Column(name = "contents")
     private String contents;

     public Boolean getIsVegan() {
          return isVegan;
     }

     public void setIsVegan(Boolean vegan) {
          isVegan = vegan;
     }
}
