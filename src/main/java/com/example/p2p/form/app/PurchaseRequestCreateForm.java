package com.example.p2p.form.app;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class PurchaseRequestCreateForm {

    private LocalDate dueDate;
    
    private String userId;
    
    private String note;
    
    private List<PurchaseRequestDetailForm> details;
    
}
