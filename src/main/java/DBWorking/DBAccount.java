package DBWorking;

import DataBaseEntites.Family;
import DataBaseEntites.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;


public class DBAccount {
    private final SessionFactory sessionFactory;

    public DBAccount(){
        Configuration configuration = new Configuration();
        sessionFactory= configuration.configure().buildSessionFactory();
    }
    public boolean checkSignUp(String login){
        Session session=this.sessionFactory.openSession();
        session.beginTransaction();
        User checked= session.get(User.class,login);
        return checked == null;
    } //Проаерка для создания аккаунта
    public boolean checkSignIn(String login,String password){
        Session session=this.sessionFactory.openSession();
        session.beginTransaction();
        User checked= session.get(User.class,login);
        if(checked==null) return false;
        else return checked.getPassword().equals(password);
    } //Проверка для входа в аккаунт
    public void addUser(User add){
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        session.persist(add);
        session.getTransaction().commit();
        session.close();
    } //Добавить пользователя
    public User getUser(String login){
        Session session=this.sessionFactory.openSession();
        session.beginTransaction();
        return session.get(User.class,login);
    } //Получить пользователя по логину
    public void updateUser(User user){
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        User tmp= session.get(User.class,user.getLogin());
        tmp.setSurname(user.getSurname());
        tmp.setName(user.getName());
        tmp.setSecondName(user.getSecondName());
        tmp.setPassword(user.getPassword());
        tmp.setFamily(user.getFamily());
        tmp.setAdmin(user.isAdmin());
        tmp.setConnected(user.isConnected());
        session.merge(tmp);
        session.getTransaction().commit();
        session.close();
    } //Обновление пользователя
    public Family addFamily(Family family){
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        session.persist(family);
        session.getTransaction().commit();
        session.close();
        session=sessionFactory.openSession();
        session.beginTransaction();
        family=session.createQuery("FROM Family WHERE creator="+family.getCreator(), Family.class).uniqueResult();
        session.getTransaction().commit();
        session.close();
        return family;
    } //Добавление семьи
    public boolean checkFamily(int id){
        Session session=this.sessionFactory.openSession();
        session.beginTransaction();
        Family checked= session.get(Family.class,id);
        return checked != null;
    } //Проверка наличия семьи
    public List<Family> getFamily(){
        Session session=this.sessionFactory.openSession();
        session.beginTransaction();
        List<Family> list=session.createQuery("FROM Family").list();
        session.close();
        return list;
    } //Получение семьи
    public List<User> getUsersFromFamily(Family check, boolean connect){
        Session session=this.sessionFactory.openSession();
        session.beginTransaction();
        List<User> list=session.createQuery("FROM User WHERE connected="+connect+" and family="+check.getId()).list();
        session.close();
        return list;
    } //Получение членов семьи
    public void getNewCreator(Family family){
        Session session=this.sessionFactory.openSession();
        session.beginTransaction();
        List<User> list=session.createQuery("FROM User WHERE connected=true and family="+family.getId()).list();
        session.close();
        if(list.size()==0){
            session = this.sessionFactory.openSession();
            session.beginTransaction();
            Family tmp = (Family) session.get(Family.class, family.getId());
            session.remove(tmp);
            session.getTransaction().commit();
            session.close();
        }
        else for(User member: list){
            if(!member.getLogin().equals(family.getCreator())){
                family.setCreator(member.getLogin());
                updateFamily(family);
                break;
            }
        }
    } //Получение нового создателя семьи
    public void updateFamily(Family family){
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Family tmp= session.get(Family.class,family.getId());
        tmp.setName(family.getName());
        tmp.setIncome(family.getIncome());
        tmp.setExpenditure(family.getExpenditure());
        tmp.setCreator(family.getCreator());
        session.merge(tmp);
        session.getTransaction().commit();
        session.close();
    } //Обновление семьи
}
