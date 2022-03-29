package ru.geekbrains.rabbitmq.console.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SenderApp {

    private final static String EXCHANGER_NAME = "myExchanger";
    private final static String QUEUE_NAME = "myQueue";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT);
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println("Привет");
            System.out.println("Это рассылка сообщений ИТ-блог:");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String message;
                String[] arr;
                String msg;
                while (!(message = reader.readLine()).equals("exit")) {
                    arr = message.split(" ");
                    if (arr.length > 1) {
                        msg = message.substring(arr[0].length() + 1);
                        channel.basicPublish(EXCHANGER_NAME, arr[0], null, msg.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
