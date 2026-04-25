package com.ctbe.productservice.service;

import com.ctbe.productservice.dto.ProductRequest;
import com.ctbe.productservice.dto.ProductResponse;
import com.ctbe.productservice.exception.ResourceNotFoundException;
import com.ctbe.productservice.model.Product;
import com.ctbe.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return toResponse(product);
    }

    public ProductResponse create(ProductRequest req) {
        Product product = toEntity(req);
        Product savedProduct = productRepository.save(product);
        return toResponse(savedProduct);
    }

    public ProductResponse update(Long id, ProductRequest req) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        
        existingProduct.setName(req.getName());
        existingProduct.setPrice(req.getPrice());
        existingProduct.setStockQty(req.getStockQty());
        existingProduct.setCategory(req.getCategory());
        
        Product updatedProduct = productRepository.save(existingProduct);
        return toResponse(updatedProduct);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStockQty(), p.getCategory());
    }

    private Product toEntity(ProductRequest req) {
        return new Product(req.getName(), req.getPrice(), req.getStockQty(), req.getCategory());
    }
}
