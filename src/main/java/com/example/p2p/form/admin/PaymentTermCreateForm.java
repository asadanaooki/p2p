package com.example.p2p.form.admin;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.DueDateType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentTermCreateForm {

    @NotBlank
    @Length(max = 50)
    private String name;
    
    @NotNull
    @Positive
    private Integer days;

    private DueDateType dueDateType;


    @AssertTrue(message = "{error.paymentTerm.dueDate.invalid}")
    public boolean isDueDateValid() {
        if (days == null) {
            return true;
        }
        
        if (dueDateType == DueDateType.THIS_MONTH_DAY ||
                dueDateType == DueDateType.NEXT_MONTH_DAY) {
           return days <= 31;
        }
        return true;
    }
}
