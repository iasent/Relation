package relation.repository;

import koreatech.cse.domain.Authority;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import relation.domain.HotKeyword;
import relation.domain.RelationToJsonWrapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by DK on 2016-12-06.
 */
@Repository
public interface HotKeywordMapper {
    @Insert("INSERT INTO relation.hotkeywords (KEYWORD, IMAGE, GOOGLE, NAVER, DAUM, DATETIME) VALUES (#{keyword}, #{image}, #{google}, #{naver}, #{daum}, #{datetime})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void insert(HotKeyword hotKeyword);

    @Select("SELECT * FROM relation.hotkeywords WHERE KEYWORD = #{keyword}")
    List<HotKeyword> findByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM relation.hotkeywords ORDER BY ID DESC LIMIT #{count}")
    List<HotKeyword> getRecentlyKeyword(@Param("count") int count);

    @Select("SELECT max(ID) as id,keyword,image,google,naver,daum,datetime FROM relation.hotkeywords group by keyword ORDER BY ID DESC LIMIT #{count}")
    List<HotKeyword> getDictinctRecentlyKeyword(@Param("count") int count);

    @Select("SELECT * FROM relation.hotkeywords WHERE DATETIME >= #{startdate} AND DATETIME <= #{enddate} ORDER BY ID DESC LIMIT #{count}")
    List<HotKeyword> getByDatetime(@Param("startdate")Timestamp startdate, @Param("enddate")Timestamp enddate, @Param("count")int count);

    @Select("SELECT max(ID) as id,keyword,image,google,naver,daum,datetime FROM relation.hotkeywords WHERE DATETIME >= #{startdate} AND DATETIME <= #{enddate} group by keyword ORDER BY ID DESC LIMIT #{count}")
    List<HotKeyword> getDistinctByDatetime(@Param("startdate")Timestamp startdate,@Param("enddate")Timestamp enddate, @Param("count")int count);
}
