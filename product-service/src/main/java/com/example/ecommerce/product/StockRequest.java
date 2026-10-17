package com.example.ecommerce.product;

import jakarta.validation.constraints.Positive;

public record StockRequest(@Positive int quantity) {
}
