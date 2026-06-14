package com.example.p2p.exception;

import com.example.p2p.enums.BusinessErrorCode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException{

    private BusinessErrorCode errorCode;
    
    public BusinessException(BusinessErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
