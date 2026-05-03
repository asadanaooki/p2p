package com.example.p2p.dto.app;

import java.util.List;

import com.example.p2p.dto.admin.SupplierOptionDto;
import com.example.p2p.dto.admin.UnitOptionDto;

import lombok.Data;

@Data
public class PurchaseRequestCreateViewDto {

    private String requester;
    
    private List<UnitOptionDto> unitOptions;
    
    private List<SupplierOptionDto> supplierOptions;

}
