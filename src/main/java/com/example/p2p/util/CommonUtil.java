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
    
    public static int calculateOffset(int page, int size) {
        return (page - 1) * size;
    }

}
