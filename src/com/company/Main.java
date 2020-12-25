package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Main {

    private static ArrayDeque<Task> taskArrayDeque = new ArrayDeque<>(); // очередь заданий для вычислений
    private static ArrayList<Client> clientArrayList = new ArrayList<>(); // список клиентов, отправившие запрос

    private static class TaskCollector extends Thread {
        TaskCollector() {
            super("Task Collector");
        }

        // формирование очереди тасков и списка клиентов
        public void run() {
            String input;

            while (true) {
                for (int i = 0; i < clientArrayList.size(); i++) {
                    // проверка подключения клиента
                    // если соединение разорвано, то убираем клиента из списка
                    if (!clientArrayList.get(i).CheckConnection()) {
                        clientArrayList.remove(i);
                        i--;
                        continue;
                    }

                    // проверка ввода
                    // если запрос не был отправлен, то убираем клиента из списка
                    // если запрос был отправлен, то добавляем таск в очередь
                    if (clientArrayList.get(i).CheckReader()) {
                        input = clientArrayList.get(i).Read();
                        if (input == null) {
                            clientArrayList.remove(i);
                            i--;
                            continue;
                        }
                        taskArrayDeque.push(new Task(input, clientArrayList.get(i)));
                    }
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // выполнение тасков
    private static class TaskCompleter extends Thread {
        TaskCompleter() {
            super("Task Completer");
        }

        public void run() {
            Task task;
            String inputLine, outline = "";
            String[] arr; // массив, состоящий из двух чисел и одного знака
                            // формируется в результате сплита строки запроса

            while (true) {
                // проверка на наличие тасков
                if (taskArrayDeque.isEmpty()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.getMessage();
                    }
                    continue;
                }

                task = taskArrayDeque.pop();
                inputLine = task.getRequest();
                arr = inputLine.split(" ");

                if (arr.length != 3) {
                    outline = "Incorrect input!";
                } else { // выполнение запроса
                    switch (arr[1]) {
                        case "+":
                            outline = Float.toString(Float.parseFloat(arr[0]) + Float.parseFloat(arr[2]));
                            break;
                        case "*":
                            outline = Float.toString(Float.parseFloat(arr[0]) * Float.parseFloat(arr[2]));
                            break;
                        case "-":
                            outline = Float.toString(Float.parseFloat(arr[0]) - Float.parseFloat(arr[2]));
                            break;
                        case "/":
                            outline = Float.toString(Float.parseFloat(arr[0]) / Float.parseFloat(arr[2]));
                            break;
                    }
                }

                task.getClient().Write(outline);
            }
        }
    }

    public static void main(String[] args) {
        int portNumber = Integer.parseInt(args[0]);
        int client_count = 0;

        new TaskCollector().start(); // запуск потока для формирование очереди тасков и списка клиентов
        new TaskCompleter().start(); // запуск потока выполнения тасков

        while (true) {
            // ожидание подключения новых клиентов
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                clientArrayList.add(new Client(serverSocket.accept(), client_count));
                client_count++;
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}
