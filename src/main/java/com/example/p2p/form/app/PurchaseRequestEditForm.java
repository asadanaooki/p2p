package com.example.p2p.form.app;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PurchaseRequestEditForm {
    
    private LocalDate dueDate;
    
    @Length(max = 500)
    private String note;
    
    @NotEmpty
    private List<@Valid PurchaseRequestDetailEditForm> details;
    
}
