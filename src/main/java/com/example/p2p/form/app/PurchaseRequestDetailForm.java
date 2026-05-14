package com.example.p2p.form.app;

import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.ItemKind;

public interface PurchaseRequestDetailForm {

    DetailInputType getDetailInputType();
    
    String getItemId();
    
    ItemKind getKind();
    
    String getItemName();
    
    String getSupplierId();
    
    String getSupplierName();
    
    String getUnitId();
    
    String getUnitName();
    
    Integer getPrice();
    
    Integer getQuantity();
    }
