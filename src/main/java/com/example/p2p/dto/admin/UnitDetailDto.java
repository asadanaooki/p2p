package com.example.p2p.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitDetailDto {

    private String unitId;
    
    private String name;
    
    private boolean active;
}
