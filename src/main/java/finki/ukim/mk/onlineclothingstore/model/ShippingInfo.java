package finki.ukim.mk.onlineclothingstore.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ShippingInfo {
    private String email;
    private String address;
    private String phone;
    private String city;
    private Integer zip;
    private String country;
}
