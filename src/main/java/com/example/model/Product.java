package com.example.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Product {
    private UUID id;
    private String name;
    private double price;
}