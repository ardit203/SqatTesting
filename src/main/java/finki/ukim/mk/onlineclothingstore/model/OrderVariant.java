package finki.ukim.mk.onlineclothingstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    private Variant variant;

    private int quantity;

    public OrderVariant(Order order, Variant variant, int quantity) {
        this.order = order;
        this.variant = variant;
        this.quantity = quantity;
    }
}
