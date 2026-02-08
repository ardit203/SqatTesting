package finki.ukim.mk.onlineclothingstore.model;

import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.model.exceptions.VariantOutOfStockException;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "variants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "size"})
)
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Size size;

    @Column(nullable = false)
    Integer stock;



    public Variant(Product product, Size size, Integer stock) {
        this.product = product;
        this.size = size;
        this.stock = stock;
    }

    public void addToStock(int quantity){
        this.stock = this.stock + quantity;
    }

    public void removeFromStock(int quantity){
        this.stock = this.stock - quantity;
    }
}
