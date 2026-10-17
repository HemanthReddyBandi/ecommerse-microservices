package com.example.ecommerce.product;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.name(), request.description(), request.price(), request.stockQuantity());
        return ProductResponse.from(products.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return products.findAll().stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = getProduct(id);
        product.updateDetails(request.name(), request.description(), request.price());
        // Stock changes use a dedicated reservation operation so edits cannot silently overwrite inventory.
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        Product product = getProduct(id);
        return new AvailabilityResponse(id, quantity, product.isAvailable(quantity));
    }

    public ProductResponse reserveStock(Long id, int quantity) {
        Product product = getProduct(id);
        product.reduceStock(quantity);
        // @Version makes concurrent updates conflict instead of silently losing a stock decrement.
        return ProductResponse.from(products.save(product));
    }

    private Product getProduct(Long id) {
        return products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
