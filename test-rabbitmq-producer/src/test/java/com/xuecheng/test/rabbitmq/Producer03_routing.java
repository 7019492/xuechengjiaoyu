package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author WangPan
 * @date 2020/7/24 13:24
 */
public class Producer03_routing {

    // 队列
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    // 交换机
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    // routingKey
    private static final String ROUTING_EMAIL = "inform_email";
    private static final String ROUTING_SMS = "inform_sms";

    public static void main(String[] args) {
        // 1.生产者和mq建立连接
        // 1.1 通过连接工厂，建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 1.2 设置参数
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("wangpan");
        connectionFactory.setPassword("wangpan");
        // 设置虚拟机, 一个mq五福可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        Connection connection;
        try {
            // 1.3建立连接
            connection = connectionFactory.newConnection();
            // 1.4创建会话通道（生产者和mq服务所有通信都在channel，
            // 中完成）
            Channel channel = connection.createChannel();
            // 1.5声明队列,如果队列在mq中没有则创建
            /**
             * 参数明细
             * queue 1.队列名称
             * durable 2.是否持久化，如果持久化，mq重启后队列还在
             * exclusive 3.是否独占连接，队列只允许在该连接中访问，如果连接关闭后队列也就自动删除了，，如果将此参数设置问true可用于临时队列的创建
             * autoDelete 4.自动删除，队列不再使用时是否自动删除此队列，，如果将此参数和exclusive设置为true就可以实现临时队列(队列不用了就自动删除)
             * arguments 5.参数,可以设置一个队列的扩展参数，比如：可设置存活时间
             *
             */
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);

            // 1.6 声明交换机
            /**
             * 参数明细
             * 1、交换机的名称
             * 2、交换机的类型
             *      1)、fanout：对应的rabbitmq的工作模式是 publish/subscribe
             *      2)、direct：对应的Routing 工作模式
             *      3)、topic：对应的是 Topics 工作模式
             *      4)、headers：对应的 headers 工作模式
             */
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);

            // 1.7 进行交换机和队列的绑定
            /**
             * 参数明细
             * 1、queue 队列名称
             * 2、exchange 交换机名称
             * 3、routingKey 路由Key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中设置为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTING_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTING_SMS);

            // 1.8发送消息
            /**
             * 参数明细
             * 1、exchange。交换机，如果不指定将使用mq默认的交换机,设置为""
             * 2、routingkey。路由key，交换机根据路由key来将消息转发到指定的队列，，如果使用默认交换机，routingkey设置为队列的名称
             * 3、props。消息的属性
             * 4.body。消息内容
             */
            for (int i = 0; i < 5; i++) {
                String message = "send email inform message to user";
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTING_EMAIL, null, message.getBytes());
                System.out.println("send to mq :" + message);
            }

            for (int i = 0; i < 5; i++) {
                String message = "send sms inform message to user";
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTING_SMS, null, message.getBytes());
                System.out.println("send to mq :" + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
