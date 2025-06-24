package com.darian.ecommerce.product.event;

import com.darian.ecommerce.auth.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class SearchProductEvent extends ApplicationEvent {
    private final Integer userId;
    private final String keyword;
    private final UserRole role;

    public SearchProductEvent(Object source, Integer userId, String keyword, UserRole role) {
        super(source);
        this.userId = userId;
        this.keyword = keyword;
        this.role = role;
    }

}