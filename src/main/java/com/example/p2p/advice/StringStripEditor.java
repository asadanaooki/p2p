package com.example.p2p.advice;

import java.beans.PropertyEditorSupport;

public class StringStripEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(text == null ? null : text.strip());
    }

}
