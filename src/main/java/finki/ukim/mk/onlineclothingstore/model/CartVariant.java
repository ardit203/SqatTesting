package finki.ukim.mk.onlineclothingstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ShoppingCart cart;

    @ManyToOne
    private Variant variant;

    private int quantity;

    public CartVariant(ShoppingCart cart, Variant variant, int quantity) {
        this.cart = cart;
        this.variant = variant;
        this.quantity = quantity;
    }
}