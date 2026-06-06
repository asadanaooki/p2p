package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.entity.ApprovalTask;

@Mapper
public interface ApprovalTaskMapperCustom {
    
    void bulkInsert(List<ApprovalTask> tasks);

}
