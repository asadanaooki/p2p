package com.example.p2p.dto.app;

import java.util.List;

import com.example.p2p.dto.admin.PaymentTermOptionDto;
import com.example.p2p.dto.admin.SupplierOptionDto;

import lombok.Data;

@Data
public class PurchaseOrderCreateViewDto {
    
    // カタログ購買＆フリー入力(コンボからサプライヤー選択)の場合のみ値入る
    private String selectedSupplierId;
    
    // カタログ購買＆フリー入力で常に値入る
    private String supplierName;
    
    private String purchaser;
    
    private String paymentTermName;
    
    private List<Integer> relatedPrNumbers;
    
    private List<PurchaseOrderLineViewDto> lines;
}
