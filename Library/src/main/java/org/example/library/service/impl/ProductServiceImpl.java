package org.example.library.service.impl;

import org.example.library.dto.ProductDto;
import org.example.library.entity.Product;
import org.example.library.repository.ProductRepository;
import org.example.library.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<ProductDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::convertToDto);
    }

    @Override
    public Page<ProductDto> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.search(keyword, pageable);
        return productPage.map(this::convertToDto);

    }

    @Override
    public List<ProductDto> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(ProductDto productDto) {
        try {
            Product product = new Product();
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setCostPrice(productDto.getCostPrice());
            product.setSalePrice(productDto.getSalePrice());
            product.setCurrentQuantity(productDto.getCurrentQuantity());
            product.setCategory(productDto.getCategory());
            product.setImage(productDto.getImage());

            return productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Product update(ProductDto productDto) {
        try {
            Product product = productRepository.findById(productDto.getId()).orElse(null);
            if (product != null) {
                product.setName(productDto.getName());
                product.setDescription(productDto.getDescription());
                product.setCostPrice(productDto.getCostPrice());
                product.setSalePrice(productDto.getSalePrice());
                product.setCurrentQuantity(productDto.getCurrentQuantity());
                product.setCategory(productDto.getCategory());

                // Chỉ update image nếu có image mới
                if (productDto.getImage() != null && !productDto.getImage().isEmpty()) {
                    product.setImage(productDto.getImage());
                }

                return productRepository.save(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        return product != null ? convertToDto(product) : null;
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCostPrice(product.getCostPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setCurrentQuantity(product.getCurrentQuantity());
        dto.setCategory(product.getCategory());
        dto.setImage(product.getImage());
        return dto;
    }

    @Override
    public Page<ProductDto> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        return productPage.map(this::convertToDto);
    }
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

}