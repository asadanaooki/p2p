package com.example.p2p.util;

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
        int start = currentPage - sidePageCount;
        int end = currentPage + sidePageCount;

        if (start < 1) {
            end += sidePageCount - currentPage + 1;
            start = 1;
        }
        if (end > totalPage) {
            start -= sidePageCount - totalPage + currentPage;
            end = totalPage;
        }
        if (start < 1) {
            start = 1;
        }

        return IntStream.rangeClosed(start, end).boxed().toList();
    }

}
