package com.zzm.rocketmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author:钟志苗
 * @Date: 19:49 2017/5/12 
 * @Description:
 */
@Data
@ConfigurationProperties("rmq")
public class RocketMqProperties {

    private String namesrvAddr = "";
    private String instanceName = "";
    private String producer = "";
    private String consumer = "";
    private Set<String> topic = new LinkedHashSet<>();
    public String getNamesrvAddr() {
        return namesrvAddr;
    }
    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }
    public String getInstanceName() {
        return instanceName;
    }
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
    public String getProducer() {
        return producer;
    }
    public void setProducer(String producer) {
        this.producer = producer;
    }
    public String getConsumer() {
        return consumer;
    }
    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }
    public Set<String> getTopic() {
        return topic;
    }
    public void setTopic(Set<String> topic) {
        this.topic = topic;
    }
}