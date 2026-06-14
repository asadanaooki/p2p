package com.example.p2p.exception;

import com.example.p2p.enums.BusinessErrorCode;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BusinessException extends RuntimeException{

    private BusinessErrorCode errorCode;
    
    public BusinessException(BusinessErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
