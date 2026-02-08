package finki.ukim.mk.onlineclothingstore.model;

import finki.ukim.mk.onlineclothingstore.model.enums.PaymentMethod;
import finki.ukim.mk.onlineclothingstore.model.enums.PaymentStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfo {
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

     @Enumerated(EnumType.STRING)
     private PaymentStatus status;

     public PaymentInfo(String method){
         if(method.equals("CASH")){
             this.method = PaymentMethod.CASH;
             this.status = PaymentStatus.NOT_PAID;
         }else {
             this.method = PaymentMethod.CARD;
             this.status = PaymentStatus.PAID;
         }
     }

     public void pay(){
         this.status = PaymentStatus.PAID;
     }
}