package com.zzm.service;

import com.zzm.model.TUser;

import java.util.List;


public interface TUserService {

	List<TUser> getTUserLsit();
	
	boolean saveTUser(TUser user);
}
