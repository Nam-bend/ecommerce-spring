    package org.example.library.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "products",uniqueConstraints = @UniqueConstraint(columnNames = {"name", "image"}))
    public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(unique = true,name = "product_id")
        private Long id;              // ID sản phẩm (primary key)
        private String name;          // Tên sản phẩm (ví dụ: "Áo sơ mi trắng")
        private String description;   // Mô tả chi tiết sản phẩm
        private double costPrice;     // Giá nhập (giá gốc)
        private double salePrice;     // Giá bán (cho khách hàng)
        private int currentQuantity;  // Số lượng tồn kho
        @Lob
        @Column(columnDefinition = "MEDIUMBLOB")
        private String image;
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name ="category_id",referencedColumnName = "category_id")
        private Category category;    // Thuộc về category nào (Áo, Quần, Giày, ...)

    }
