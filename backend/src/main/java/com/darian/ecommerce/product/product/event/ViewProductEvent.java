package com.darian.ecommerce.product.event;

import com.darian.ecommerce.auth.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Setter
@Getter
public class ViewProductEvent extends ApplicationEvent {
    private final Long productId;
    private final Integer userId;
    private final UserRole role;

    public ViewProductEvent(Object source,  Long productId, Integer userId, UserRole role) {
        super(source);
        this.productId = productId;
        this.userId = userId;
        this.role = role;
    }
}
