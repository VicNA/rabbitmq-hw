package ru.geekbrains.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ReceiverApp {

    private final static String EXCHANGER_NAME = "myExchanger";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT);

        String queueName = channel.queueDeclare().getQueue();

        System.out.println("Привет будущий подписщик!");
        System.out.println("Прежде чем получать рассылки, укажи тему на которую ты хочешь подписаться:");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String command;
            String topic = null;

            while (!(command = reader.readLine()).isEmpty() || topic == null) {
                if (command.startsWith("set_topic") && command.split(" ").length == 2) {
                    topic = command.split(" ")[1];
                    break;
                } else {
                    System.out.println("Укажите тему в формате: 'set_topic тема_подписки'");
                }
            }
            channel.queueBind(queueName, EXCHANGER_NAME, topic);
            System.out.println("Подписка на тему " + topic);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(message);
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
    }
}
