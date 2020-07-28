package com.xuecheng.test.rabiitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author WangPan
 * @date 2020/7/24 16:28
 */
public class Consumer02_subscribe_sms {
    // 队列
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    // 交换机
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 1.消费者和mq建立连接
        // 1.1 通过连接工厂，建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 1.2 设置参数
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("wangpan");
        connectionFactory.setPassword("wangpan");
        // 设置虚拟机, 一个mq五福可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        // 1.3建立连接
        Connection connection = connectionFactory.newConnection();
        // 1.4创建会话通道（生产者和mq服务所有通信都在channel，
        // 中完成）
        Channel channel = connection.createChannel();

        // 1.5声明队列,如果队列在mq中没有，则创建
        /**
         * 参数明细
         * queue 1.队列名称
         * durable 2.是否持久化，如果持久化，mq重启后队列还在
         * exclusive 3.是否独占连接，队列只允许在该连接中访问，如果连接关闭后队列也就自动删除了，，如果将此参数设置问true可用于临时队列的创建
         * autoDelete 4.自动删除，队列不再使用时是否自动删除此队列，，如果将此参数和exclusive设置为true就可以实现临时队列(队列不用了就自动删除)
         * arguments 5.参数,可以设置一个队列的扩展参数，比如：可设置存活时间
         *
         */
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
        channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);

        // 1.7 进行交换机和队列的绑定
        /**
         * 参数明细
         * 1、queue 队列名称
         * 2、exchange 交换机名称
         * 3、routingKey 路由Key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中设置为空字符串
         */
        channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");

        // 消费消息
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            // 当接收到消息后此方法将被调用

            /**
             *
             * @param consumerTag 消费者标签，，用来标识消费者，，如果设置 在监听队列时候设置 channel.basicConsume
             * @param envelope 信封，通过envelope
             * @param properties 消息的属性
             * @param body 消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 交换机
                String exchange = envelope.getExchange();
                //  消息id，mq在channel 中用来标识消息的id，，可用于确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("接收到消息:"+message);
            }
        };

        // 1.8 监听队列
        /**
         * 参数明细
         * 1、 queue  队列名称
         * 2、 autoAck 自动回复，当消费者接收到消息后要告诉mq消息已接收，，如果将此参数设置为true表示会自动回复mq，如果设置为false要通过编程实现回复
         * 3、 callback 消费方法，，当消费者接收到消息要执行的方法
         */
        channel.basicConsume(QUEUE_INFORM_SMS,true,defaultConsumer);
    }


}
