package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.DueDateType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTermEditForm {

    @NotBlank
    @Length(max = 50)
    private String name;

    @Positive
    private int days;

    private DueDateType dueDateType;

    private boolean active;

    @AssertTrue
    public boolean isDueDateValid() {
        if (dueDateType == DueDateType.THIS_MONTH_DAY ||
                dueDateType == DueDateType.NEXT_MONTH_DAY) {
           return days <= 31;
        }
        return true;
    }

}
