package com.zzm.mapper;

import java.util.List;

import com.zzm.model.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface TUserMapper {

	@Select("select * from t_user")
	List<TUser> getTUserList();
	
	/*@Insert("INSERT INTO t_user (name) VALUES(#{name})")*/
	Integer saveTUser(TUser user);
}
