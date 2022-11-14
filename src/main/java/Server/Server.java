package Server;

import DBWorking.DBAccount;
import DataBaseEntites.Family;
import DataBaseEntites.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;

public class Server extends Thread {
    private final Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectMapper jsonConvert;
    private StringWriter writer;

    public Server(Socket client) {
        this.client = client;
        try {
            this.in = new DataInputStream(client.getInputStream());
            this.out = new DataOutputStream(client.getOutputStream());
        } catch (IOException ignored) {
        }
        start();
    }

    public void signUp() {
        jsonConvert=new ObjectMapper();
        writer=new StringWriter();
        String result;
        boolean userNotFound;
        User user;
        DBAccount db = new DBAccount();
        try {
            result=in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            user = jsonConvert.readValue(result, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        userNotFound = db.checkSignUp(user.getLogin());
        if (!userNotFound) {
            try {
                out.write(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            db.addUser(user);
            try {
                out.write(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    } //Регистрация
    public void signIn() {
        jsonConvert=new ObjectMapper();
        writer=new StringWriter();
        String result;
        boolean userNotFound;
        User user;
        DBAccount db = new DBAccount();
        try {
            result=in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println(result);
            user = jsonConvert.readValue(result, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        userNotFound = db.checkSignIn(user.getLogin(), user.getPassword());
        if (!userNotFound) {
            try {
                out.write(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            user = db.getUser(user.getLogin());
            try {
                out.write(1);
                jsonConvert.writeValue(writer,user);
                result=writer.toString();
                out.writeUTF(result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    } //Вход
    public void updateUser() {
        jsonConvert=new ObjectMapper();
        writer=new StringWriter();
        String result;
        User user = null;
        DBAccount db = new DBAccount();
        try {
            result = in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            user = jsonConvert.readValue(result, User.class);
        } catch (JsonProcessingException e) {
            try {
                out.writeByte(0);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (user != null) {
            db.updateUser(user);
        }
        try {
            out.write(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    } //Обновление пользователя
    public void disconnect(){
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    } //Отключение
    public void addFamily() {
        jsonConvert=new ObjectMapper();
        writer=new StringWriter();
        Family family;
        DBAccount db = new DBAccount();
        String result;
        try {
            result = in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            family = jsonConvert.readValue(result, Family.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        family=db.addFamily(family);
        try {
            jsonConvert.writeValue(writer,family);
            result=writer.toString();
            out.writeUTF(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }//Регистрация
    public void getFamily() {
        jsonConvert = new ObjectMapper();
        writer = new StringWriter();
        String result;
        List<Family> family;
        DBAccount db = new DBAccount();
        family = db.getFamily();
        try {
            JSONArray array=new JSONArray();
            for (Family fam: family){
                array.add(fam);
            }
            jsonConvert.writeValue(writer, array);
            result = writer.toString();
            out.writeUTF(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    } //Получение семьи из базы данных
    public void getUsersInFamily(boolean connect){
        jsonConvert = new ObjectMapper();
        writer = new StringWriter();
        List<User> users;
        String result;
        Family family;
        DBAccount db=new DBAccount();
        try {
            result = in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            family = jsonConvert.readValue(result, Family.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        users=db.getUsersFromFamily(family,connect);
        try {
            JSONArray array=new JSONArray();
            for (User user: users){
                array.add(user);
            }
            jsonConvert.writeValue(writer, array);
            result = writer.toString();
            out.writeUTF(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    } //Получение пользователей семьи
    public void getNewCreator(){
        jsonConvert = new ObjectMapper();
        writer = new StringWriter();
        String result;
        Family family;
        DBAccount db=new DBAccount();
        try {
            result = in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            family = jsonConvert.readValue(result, Family.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        db.getNewCreator(family);
    } //Установка нового создателя
    public void deleteUserFromFamily(){
        String login;
        try {
            login= in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DBAccount db=new DBAccount();
        User user=db.getUser(login);
        user.setFamily(null);
        db.updateUser(user);
    }
    @Override
    public void run() {
        byte check;
        while(!client.isClosed()) {
            try {
                check = in.readByte();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            switch (check) {
                case 0 -> disconnect();
                case 1 -> signUp();
                case 2 -> signIn();
                case 3 -> updateUser();
                case 4 -> addFamily();
                case 5 -> getFamily();
                case 6 -> getUsersInFamily(false);
                case 7 -> getUsersInFamily(true);
                case 8 -> getNewCreator();
                case 9 -> deleteUserFromFamily();
            }
        }
    }
}
