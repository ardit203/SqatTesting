package finki.ukim.mk.onlineclothingstore.model;

import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
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
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public Delivery(Order order) {
        this.order = order;
        this.status = DeliveryStatus.PENDING;
    }

    public void confirm(){
        status = DeliveryStatus.CONFIRMED;
    }

    public void ship(){
        status = DeliveryStatus.SHIPPED;
    }

    public void deliver(){
        status = DeliveryStatus.DELIVERED;
    }

    public void cancel(){
        status = DeliveryStatus.CANCELLED;
    }
}
