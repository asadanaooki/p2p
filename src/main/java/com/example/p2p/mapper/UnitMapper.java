package com.example.p2p.mapper;

import com.example.p2p.entity.Unit;
import com.example.p2p.entity.UnitExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UnitMapper {
    long countByExample(UnitExample example);

    int deleteByExample(UnitExample example);

    int deleteByPrimaryKey(String unitId);

    int insert(Unit row);

    int insertSelective(Unit row);

    List<Unit> selectByExample(UnitExample example);

    Unit selectByPrimaryKey(String unitId);

    int updateByExampleSelective(@Param("row") Unit row, @Param("example") UnitExample example);

    int updateByExample(@Param("row") Unit row, @Param("example") UnitExample example);

    int updateByPrimaryKeySelective(Unit row);

    int updateByPrimaryKey(Unit row);
}