package com.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Обработчик клиента
public class ClientHandler {
    Socket socket;
    ServerMain server;
    DataOutputStream out;
    DataInputStream in;
    private String nickname;

                    //lesson_5 (lesson_4/2) Реализация ExecutorService
    /**
     * Ничего толком не понял про экзекуторы, а pool.shutdown() вообще не ясно где ставить
     * в данном контексте и нужно ли, если сервер насильно отрубается?....
     * */
    ExecutorService pool = Executors.newCachedThreadPool(r -> {
        System.out.println("NewThread");
        return new Thread(r);
    });
    public void sendMsg(String msg){
        System.out.println("Клиенту " + nickname +" сообщение от " + msg);
        try {
            out.writeUTF(msg + "\n");
        } catch (SocketException e){
            //Ловит ошибку при закрытии окна клиента
            System.out.println("SocketException -> sendMsg() -Client Handler 1");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException -> sendMsg() -Client Handler 2");
        }
    }
    public void sendServiceMsg(String msg){
        System.out.println("Client send service message: " + msg);
        try {
            out.writeUTF(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException -> sendServiceMsg()-Client Handler");
        }
    }
    public String getNickname() {
        return nickname;
    }
    private boolean isUserCorrect (String nickname, ServerMain server) {
        if(server.isNickFree(nickname)){
            server.subscribe(ClientHandler.this);
            //печатает в консоль клиента
            sendServiceMsg("/authok " + "Вы залогинены под ником: " + nickname+"\n");
            //печатает в арею клиента
            try {
                out.writeUTF("Вы залогинены под ником: " + nickname+"\n");
            } catch (IOException e) {
                System.out.println("IOException isUserCorrect -> ClientHandler");
                e.printStackTrace();
            }
            server.sendOnlineUsers();
            return true;
        } else {
            sendMsg("Wrong Login/Password -> isUserCorrect -ClientHandler");
            return false;
        }
    }

    public void sendSecretMsg(String str){
        //отправляет в случае совпадения формата записи и ника получателя:
        //("/w" + " " + to_nickname + " " + message)...
        String[] strArr = str.split(" ");
        StringBuilder formatTxt = new StringBuilder();
        for (int i = 2; i < strArr.length; i++) {
            formatTxt.append(strArr[i]).append(" ");
        }
        if(strArr.length >= 2){
            server.sendToOnly(nickname+" secret: "+ formatTxt, strArr[1]);
        }
    }

    public ClientHandler(Socket socket,ServerMain serverMain) {
        this.socket = socket;
        this.server = serverMain;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            pool.execute(() -> {

   System.out.println("!!!" + Thread.currentThread().getName());

try {
   while (true){
       //авторизация
           String str = in.readUTF(); // */auth log pass
       //Задание нового пользователя
       if(str.startsWith("/new")){
           String[] fmt = str.split(" ");
           if(!AuthServer.setLoginPasswordNickname(fmt[1], fmt[2], fmt[3])){
               System.out.println("Ошибка уникальности имён ->run() -ClientHandler");
               out.writeUTF("/regoff");
           }else {
               System.out.println("Зарегистрирован новый пользователь: "+nickname+" (/new ->run() -ClientHandler)");
               out.writeUTF("/regok");

           }
       }
       //авторизация
       if(str.startsWith("/auth")){
               String[] creds = str.split(" ");
               String nick = AuthServer.getNickByLogPassSQL(creds[1],creds[2]);

               if(nick == null){
                   System.out.println("Wrong log/pass ->getNickByLogPassSQL() -AuthServer => return null!");
                   out.writeUTF("Wrong log/pass!!!");
               }
               if (nick != null) {
                   //задали никнейм
                   nickname = nick;
                   if(isUserCorrect(nickname,server))break;
               }
           }

   }
   //После окончания аутентификации:
   //Обработка служебных команд и последующая отправка сообщения

   while (true) {
       String str;
       str = in.readUTF();
       if (str.equals("/end")) {
           out.writeUTF("/end");
           break;
       }
       //deleteThisUser
       if(str.equals("/deleteThisUser")) {
           System.out.println("Удаление пользователя " +getNickname() );
           AuthServer.deleteUserFromBD(getNickname());
           break;
       }
       //modifiedUser
       if(str.startsWith("/modifiedUser")) {
           System.out.println("Изменение данных пользователя: " +getNickname() );
           String[] modArr = str.split(" ");

               if (AuthServer.isUserFree(modArr[1],modArr[3])){
                   AuthServer.modifiedLogPassNick(getNickname(),modArr[1].trim(),modArr[2].trim(),modArr[3].trim());
                   System.out.println("Новый Логин: "+modArr[1].trim());
                   System.out.println("Новый Никнейм: "+modArr[3].trim());
                   break;
               }else{
                   System.out.println("/modifiedUser-> Логин или Никнейм занят!!! -ClientHandler");
                   out.writeUTF("Логин или Никнейм занят!!!");
                   continue;
               }

       }
       //show
       if (str.startsWith("/show")){
           server.sendOnlineUsers();
       }
       //метод /w!
       if (str.startsWith("/w")){
           sendSecretMsg(str);

       }
       //Рассылка принятого от ClientController sendMessage() сообщения!!!
       else {
           serverMain.sendToAll(nickname + ": " + str);
       }
       //Завершение
   }

} catch (IOException e) {
   e.printStackTrace();
   System.out.println("IOException в обработчике служебных команд -ClientHandler");
} finally {
   try {
       out.writeUTF("/end");
   } catch (IOException e) {
       e.printStackTrace();
   }
   try {
       in.close();
   } catch (IOException e) {
       e.printStackTrace();
   }
   try {
       out.close();
   } catch (IOException e) {
       e.printStackTrace();
   }
   try {
       socket.close();
   } catch (IOException e) {
       e.printStackTrace();
   }
}
server.unsubscribe(ClientHandler.this);


});
            pool.shutdown();//?????

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
