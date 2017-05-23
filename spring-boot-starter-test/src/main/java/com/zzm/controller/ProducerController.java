package com.zzm.controller;


import org.apache.rocketmq.client.Validators;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageAccessor;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author:钟志苗
 * @Date: 8:36 2017/5/13
 * @Description:
 */
@RestController
public class ProducerController {
    private static Logger log = LoggerFactory.getLogger(ProducerController.class);
    @Autowired
    @Qualifier(value = "default")
    private DefaultMQProducer defaultProducer;

    @Autowired
    private TransactionMQProducer transactionProducer;

    @RequestMapping(value = "/sendMsg", method = RequestMethod.GET)
    public void sendMsg() throws Exception {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Message msg = new Message("test", "TagA", "OrderID001", (i + ":" + j).getBytes());
                defaultProducer.send(msg, (List<MessageQueue> mqs, Message msg1, Object arg) -> {
                    // TODO Auto-generated method stub
                    int value = arg.hashCode();
                    if (value < 0) {
                        value = Math.abs(value);
                    }
                    value = value % mqs.size();
                    return mqs.get(value);
                }, i);
            }

        }

    }

    @RequestMapping("/rocketMq/{tag}/{body}/{total}")
    @ResponseBody
    public String rocketMq(@PathVariable String tag, @PathVariable String body, @PathVariable Integer total)
            throws Exception {
        long t1 = System.currentTimeMillis();
        SendResult sendResult = null;
        int fail = 0;
        for (int i = 1; i < total + 1; i++) {
            Message msg = new Message("test", // topic
                    tag, // tag
                    "OrderID188", // key
                    (i + "").getBytes(RemotingHelper.DEFAULT_CHARSET));// body
            sendResult = defaultProducer.send(msg);
            if (sendResult.getSendStatus().name().equals(SendStatus.SEND_OK.name())) {
                fail++;
            } else {
                System.out.println(sendResult.getSendStatus().name());
            }
        }
        long t2 = System.currentTimeMillis();
        return "所花时间：" + (t2 - t1) + "成功次数" + fail;
    }

    @RequestMapping(value = "/sendTransactionMsg/{id}/{arg1}", method = RequestMethod.GET)
    public String sendTransactionMsg(@PathVariable String id, @PathVariable String arg1) {
        SendResult sendResult = null;
        try {
            // 构造消息
            Message msg = new Message("test", // topic
                    "TagA", // tag
                    "OrderID001", // key
                    id.getBytes());// body

            // 发送事务消息，LocalTransactionExecute的executeLocalTransactionBranch方法中执行本地逻辑
            sendResult = sendMessageInTransaction(msg, (Message msg1, Object arg) -> {
                int value = Integer.valueOf(arg1);
                System.out.println("执行本地事务(结合自身的业务逻辑)。。。完成");
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

    public TransactionSendResult sendMessageInTransaction(final Message msg, final LocalTransactionExecuter tranExecuter, final Object arg)
            throws MQClientException {
        if (null == tranExecuter) {
            throw new MQClientException("tranExecutor is null", null);
        }
        Validators.checkMessage(msg, transactionProducer);

        SendResult sendResult = null;
        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_TRANSACTION_PREPARED, "true");
        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_PRODUCER_GROUP, transactionProducer.getProducerGroup());
        try {
            sendResult = transactionProducer.send(msg);
        } catch (Exception e) {
            throw new MQClientException("send message Exception", e);
        }

        LocalTransactionState localTransactionState = LocalTransactionState.UNKNOW;
        Throwable localException = null;
        switch (sendResult.getSendStatus()) {
            case SEND_OK: {
                try {
                    if (sendResult.getTransactionId() != null) {
                        msg.putUserProperty("__transactionId__", sendResult.getTransactionId());
                    }
                    localTransactionState = tranExecuter.executeLocalTransactionBranch(msg, arg);
                    if (null == localTransactionState) {
                        localTransactionState = LocalTransactionState.UNKNOW;
                    }

                    if (localTransactionState != LocalTransactionState.COMMIT_MESSAGE) {
                        log.info("executeLocalTransactionBranch return {}", localTransactionState);
                        log.info(msg.toString());
                    }
                } catch (Throwable e) {
                    log.info("executeLocalTransactionBranch exception", e);
                    log.info(msg.toString());
                    localException = e;
                }
            }
            break;
            case FLUSH_DISK_TIMEOUT:
            case FLUSH_SLAVE_TIMEOUT:
            case SLAVE_NOT_AVAILABLE:
                localTransactionState = LocalTransactionState.ROLLBACK_MESSAGE;
                break;
            default:
                break;
        }

        try {
            //测试事务回查
            //this.endTransaction(sendResult, localTransactionState, localException);
        } catch (Exception e) {
            log.warn("local transaction execute " + localTransactionState + ", but end broker transaction failed", e);
        }

        TransactionSendResult transactionSendResult = new TransactionSendResult();
        transactionSendResult.setSendStatus(sendResult.getSendStatus());
        transactionSendResult.setMessageQueue(sendResult.getMessageQueue());
        transactionSendResult.setMsgId(sendResult.getMsgId());
        transactionSendResult.setQueueOffset(sendResult.getQueueOffset());
        transactionSendResult.setTransactionId(sendResult.getTransactionId());
        transactionSendResult.setLocalTransactionState(localTransactionState);
        return transactionSendResult;
    }
}
