package com.zzm.controller;

import com.alibaba.rocketmq.client.producer.*;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;


/**
 * @Author:钟志苗
 * @Date: 8:36 2017/5/13 
 * @Description:
 */
@RestController
public class ProducerController {
	
	@Autowired
    @Qualifier(value = "default")
    private DefaultMQProducer defaultProducer;
    
    @Autowired
    private TransactionMQProducer transactionProducer;

    
    @RequestMapping(value = "/sendMsg", method = RequestMethod.GET)
    public void sendMsg() {
    	 Message msg = new Message("TopicTest1",// topic
                 "TagA",// tag
                 "OrderID001",// key
                 ("Hello jyqlove333").getBytes());// body
         try {
			defaultProducer.send(msg,new SendCallback(){
				
				@Override
				public void onSuccess(SendResult sendResult) {
					 System.out.println(sendResult);
					 //TODO 发送成功处理
				}
				
				@Override
				public void onException(Throwable e) {
					 System.out.println(e);
					//TODO 发送失败处理
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @RequestMapping("/rocketMq/{tag}/{body}/{total}")
    @ResponseBody
    public String rocketMq(@PathVariable String tag, @PathVariable String body, @PathVariable Integer total) throws Exception{
        long t1 = System.currentTimeMillis();
        SendResult sendResult = null;
        int fail = 0;
        for (int i = 1; i < total+1; i++) {
            Message msg = new Message("test",// topic
                    tag,// tag
                    "OrderID188",// key
                    (i+"").getBytes(RemotingHelper.DEFAULT_CHARSET));// body
            sendResult = defaultProducer.send(msg);
            if(sendResult.getSendStatus().name().equals(SendStatus.SEND_OK.name())){
                fail++;
            }else{
                System.out.println(sendResult.getSendStatus().name());
            }
        }
        long t2 = System.currentTimeMillis();
        return "所花时间："+(t2 -t1)+"成功次数"+fail;
    }

    @RequestMapping(value = "/sendTransactionMsg", method = RequestMethod.GET)
    public String sendTransactionMsg() {
    	SendResult sendResult = null;
    	try {
    		//构造消息
            Message msg = new Message("TopicTest1",// topic
                    "TagA",// tag
                    "OrderID001",// key
                    ("Hello jyqlove333").getBytes());// body
            
            //发送事务消息，LocalTransactionExecute的executeLocalTransactionBranch方法中执行本地逻辑
            sendResult = transactionProducer.sendMessageInTransaction(msg, (Message msg1,Object arg) -> {
                int value = 1;
                
                //TODO 执行本地事务，改变value的值
                //===================================================
                System.out.println("执行本地事务。。。完成");
                if(arg instanceof Integer){
                	value = (Integer)arg;
                }
                //===================================================
                
                if (value == 0) {
                    throw new RuntimeException("Could not find db");
                } else if ((value % 5) == 0) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else if ((value % 4) == 0) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }, 4);
            System.out.println(sendResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendResult.toString();
    }
}
