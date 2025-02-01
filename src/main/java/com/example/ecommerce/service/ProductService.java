package com.example.ecommerce.service;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public Product createProduct(CreateProductRequest req) {
        Product product = new Product();
        product.setName(req.getName());
        product.setPrice(req.getPrice());
        product.setStockQuantity(req.getStockQuantity());
        return productRepository.save(product);
    }
    
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    public Product decreaseStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        if(product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }
}
