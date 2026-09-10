package com.example.p2p.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.example.p2p.dto.app.RelatedPrDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RelatedPurchaseRequestListTypeHandler extends BaseTypeHandler<List<RelatedPrDto>> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<RelatedPrDto> parameter, JdbcType jdbcType)
            throws SQLException {
        // 不要
    }

    @Override
    public List<RelatedPrDto> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return toDistinctPrList(rs.getString(columnName));
        }
        catch (JsonProcessingException e) {
            // TODO 自動生成された catch ブロック
            throw new SQLException(e);
        }

    }

    @Override
    public List<RelatedPrDto> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return toDistinctPrList(rs.getString(columnIndex));
        }
        catch (JsonProcessingException e) {
            // TODO 自動生成された catch ブロック
            throw new SQLException(e);
        }
    }

    @Override
    public List<RelatedPrDto> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return toDistinctPrList(cs.getString(columnIndex));
        }
        catch (JsonProcessingException e) {
            // TODO 自動生成された catch ブロック
            throw new SQLException(e);
        }
    }

    private List<RelatedPrDto> toDistinctPrList(String value) throws JsonMappingException, JsonProcessingException {
        LinkedHashMap<String, RelatedPrDto> map = new LinkedHashMap<String, RelatedPrDto>();
        List<RelatedPrDto> list = Arrays.asList(objectMapper.readValue(value, RelatedPrDto[].class));

        for (RelatedPrDto pr : list) {
            map.putIfAbsent(pr.getPrId(), pr);
        }
        return new ArrayList<>(map.values());
    }

}
