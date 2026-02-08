package finki.ukim.mk.onlineclothingstore.model;

import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "shop_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime madeAt;

    private Double total;

    @Embedded
    private ShippingInfo shipping;

    @Embedded
    private PaymentInfo payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Delivery delivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderVariant> orderVariants = new ArrayList<>();

    public Order(User user, LocalDateTime madeAt, Double total, String address, String email, String phone, String city, Integer zip, String country, String paymentMethod){
        this.user = user;
        this.madeAt = madeAt;
        this.total = total;
        this.shipping = new ShippingInfo(email, address, phone, city, zip, country);
        this.payment = new PaymentInfo(paymentMethod);
    }
}
