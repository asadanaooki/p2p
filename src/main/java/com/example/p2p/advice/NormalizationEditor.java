package com.example.p2p.advice;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

import com.ibm.icu.text.Transliterator;
import com.ibm.icu.text.UnicodeSet;

public class NormalizationEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // 小文字化
        text = text.toLowerCase();
        // 全角→半角(英数字のみ)
        Transliterator fullToHalf = Transliterator.getInstance("Fullwidth-Halfwidth");
        fullToHalf.setFilter(new UnicodeSet("[０-９Ａ-Ｚａ-ｚ]"));
        text = fullToHalf.transliterate(text);
        // ひらがな→カタカナ
        Transliterator hiraganaToKatakana = Transliterator.getInstance("Hiragana-Katakana");
        text = hiraganaToKatakana.transliterate(text);
        // 記号削除(空白-()[])
        text = text.replaceAll("[\\p{Zs}\\-()\\[\\]]", "");
        // 空文字→NULL
        text = text.equals("") ? null : text;
        
        this.setValue(text);
    }

}
