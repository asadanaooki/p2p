package com.example.p2p.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class CommonUtil {

    public static List<Integer> createPageNumbers(
            int totalItemCount,
            int pageSize,
            int currentPage,
            int sidePageCount) {
        if (totalItemCount == 0) {
            return Collections.EMPTY_LIST;
        }

        int totalPage = (int) Math.ceil((double) totalItemCount / pageSize);
        int displayCount = sidePageCount * 2 + 1;

        int start = Math.max(1, currentPage - sidePageCount);
        int end = Math.min(totalPage, currentPage + sidePageCount);

        int currentCount = end - start + 1;

        if (currentCount < displayCount) {
            int shortage = displayCount - currentCount;
            end = Math.min(totalPage, end + shortage);
            start = Math.max(1, end - displayCount + 1);
        }

        return IntStream.rangeClosed(start, end).boxed().toList();
    }
    
    public static String toRegex(String keyword) {
        String[] regexEscapeTargets = {".", "+", "/", "-"};
        List<String> escapedKeywords = Arrays.asList(keyword.split("[\\p{Zs}]+")).stream().map(kw -> {
            for (String es : regexEscapeTargets) {
                kw = kw.replace(es, "\\" + es);
            }
            return kw;
        }).toList();

        return "^(?=.*" + String.join(".*)(?=.*", escapedKeywords) + ".*).*$";
    }
    
    public static int calculateOffset(int page, int size) {
        return (page - 1) * size;
    }

}
