package com.darian.ecommerce.auth.exception;

import com.darian.ecommerce.shared.exception.ErrorCode;
import com.darian.ecommerce.shared.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
} 