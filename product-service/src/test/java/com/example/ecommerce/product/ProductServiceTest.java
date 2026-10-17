package com.example.ecommerce.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ProductServiceTest {

    @Test
    void reserveStockDecreasesQuantityAndSavesProduct() {
        ProductRepository repository = org.mockito.Mockito.mock(ProductRepository.class);
        ProductService service = new ProductService(repository);
        Product product = new Product("Keyboard", "Mechanical keyboard", new BigDecimal("49.99"), 5);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(org.mockito.ArgumentMatchers.any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse result = service.reserveStock(1L, 2);

        assertEquals(3, result.stockQuantity());
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        assertEquals(3, captor.getValue().getStockQuantity());
    }

    @Test
    void reserveStockRejectsQuantityAboveAvailableStock() {
        ProductRepository repository = org.mockito.Mockito.mock(ProductRepository.class);
        ProductService service = new ProductService(repository);
        when(repository.findById(1L)).thenReturn(Optional.of(
                new Product("Keyboard", null, new BigDecimal("49.99"), 1)));

        assertThrows(InsufficientStockException.class, () -> service.reserveStock(1L, 2));
    }
}
