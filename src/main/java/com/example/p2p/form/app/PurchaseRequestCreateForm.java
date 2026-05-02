package com.example.p2p.form.app;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PurchaseRequestCreateForm {

    private LocalDate dueDate;
    
    private String note;
    
    @NotEmpty
    private List<@Valid PurchaseRequestDetailForm> details;
    
}
