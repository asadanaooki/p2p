package com.example.p2p.advice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NormalizationEditorTest {

    NormalizationEditor editor = new NormalizationEditor();
    
    @Test
    void setAsText_toLowerCase() {
        editor.setAsText("AbfC");
        
        assertThat(editor.getAsText()).isEqualTo("abfc");
    }
    
    @Test
    void setAsText_fullToHalf_alphanumeric() {
        editor.setAsText("ｖu５カ％");
        
        assertThat(editor.getAsText()).isEqualTo("vu5カ％");
    }
    
    @Test
    void setAsText_hiraganaToKatakana() {
        editor.setAsText("たをブｼ");
        
        assertThat(editor.getAsText()).isEqualTo("タヲブシ");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"[", "]", "(", ")", "-", " ", "　"})
    void setAsText_symbols(String symbol) {
        editor.setAsText(symbol);
        assertThat(editor.getAsText()).isEmpty();
    }
    
    @Test
    void setAsText_mixed() {
        editor.setAsText("　asBDｇ( う漢８字　をカ%#ﾀ    ");
        
        assertThat(editor.getAsText()).isEqualTo("asbdgウ漢8字ヲカ%#タ");
    }
}
