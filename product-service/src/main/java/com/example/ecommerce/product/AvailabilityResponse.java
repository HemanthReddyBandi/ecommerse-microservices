package com.example.ecommerce.product;

public record AvailabilityResponse(Long productId, int requestedQuantity, boolean available) {
}
