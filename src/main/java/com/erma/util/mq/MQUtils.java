package com.erma.util.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Date 2022/8/3 14:41
 * @Created by erma66
 */
@Slf4j
public class MQUtils {
    public static void basicAck(Channel channel, long tag, boolean reDelivered) {
        try {
            if (reDelivered) {
                channel.basicReject(tag, false);
            } else {
                channel.basicNack(tag, false, true);// 重新入队一次
            }
        } catch (Exception e) {
            log.error("[MQ-R] -basic tag = {}", tag, e);
        }
    }
}
