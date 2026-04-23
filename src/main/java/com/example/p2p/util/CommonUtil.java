package com.example.p2p.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static List<Integer> createPageNumbers(int totalItemCount, int pageSize, int currentPage,
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
        String[] regexEscapeTargets = { ".", "+", "/", "-" };
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
    
    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        return token;
    }

    public static String hashToken(String token) {
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
             logger.error("トークンのハッシュ化に失敗しました");
            throw new RuntimeException(e);
        }
        HexFormat hex = HexFormat.of();
        return hex.formatHex(sha256.digest(token.getBytes(StandardCharsets.UTF_8)));
    }

}
