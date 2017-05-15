package com.zzm;


import com.zzm.mapper.TUserMapper;
import com.zzm.model.TUser;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author:钟志苗
 * @Date: 13:29 2017/5/13
 * @Description:测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {RocketmqStarterTestApplication.class})
@ActiveProfiles("dev")
public class Test {
    @Autowired
    private TUserMapper tUserMapper;
    @org.junit.Test
    public  void  test(){
        TUser tUser = new TUser();
        tUser.setId(1l);
        Integer num = tUserMapper.saveTUser(tUser);
        System.out.println(num);
    }



}
