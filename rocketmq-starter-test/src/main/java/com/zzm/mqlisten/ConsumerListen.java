package com.zzm.mqlisten;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.zzm.model.TUser;
import com.zzm.rocketmq.event.RocketMqEvent;
import com.zzm.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author:钟志苗
 * @Date: 8:35 2017/5/13 
 * @Description:消费监听器
 */
@Component
public class ConsumerListen {

	@Autowired
	private TUserService tUserService;

	@Async
	@EventListener(condition = "#event.topic=='test'")
	public void testListen(RocketMqEvent event) {
		DefaultMQPushConsumer consumer = event.getConsumer();
		try {
			String id = new String(event.getMessageExt().getBody(),"utf-8");
			Long sid = Long.valueOf(id);
			TUser tUser = new TUser();
			tUser.setId(sid);
			System.out.println("bl"+tUserService.saveTUser(tUser));
		} catch (Exception e) {
			e.printStackTrace();
			if (event.getMessageExt().getReconsumeTimes() <= 1) {// 重复消费3次
				try {
					consumer.sendMessageBack(event.getMessageExt(), 1);
				} catch (RemotingException | MQBrokerException | InterruptedException | MQClientException e1) {
					e1.printStackTrace();
					//消息进行定时重试
				}
			} else {
				System.out.println("消息消费失败，定时重试");
			}
		}
	}
	@Async
	@EventListener(condition = "#event.topic=='TopicTest1'")
	public void normalListen(RocketMqEvent event) {
		System.out.println("通用topic监听");
		DefaultMQPushConsumer consumer = event.getConsumer();
		try {
			System.out.println("tag筛选：" + new String(event.getMessageExt().getBody(),"utf-8"));
		}catch (Exception e){
			e.printStackTrace();
		}

	}
}
