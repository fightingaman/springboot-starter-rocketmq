package com.zzm.service.impl;

import java.util.List;

import com.zzm.mapper.TUserMapper;
import com.zzm.model.TUser;
import com.zzm.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TUserServiceImpl implements TUserService {

	@Autowired
	private TUserMapper tUserMapper;
	
	@Override
	public List<TUser> getTUserLsit() {
		// TODO Auto-generated method stub
		return tUserMapper.getTUserList();
	}
	
	@Override
	@Transactional
	public boolean saveTUser(TUser user) {
		// TODO Auto-generated method stub
		return tUserMapper.saveTUser(user)==1;
	}

}
