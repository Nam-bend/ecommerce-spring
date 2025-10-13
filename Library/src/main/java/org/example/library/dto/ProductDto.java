    package org.example.library.dto;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.example.library.entity.Category;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ProductDto {
        private Long id;
        private String name;
        private String description;
        private double costPrice;
        private double salePrice;
        private int currentQuantity;
        private Category category;
        private String image;

    }
