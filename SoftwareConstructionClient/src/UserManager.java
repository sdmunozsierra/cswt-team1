public class UserManager {

    public  enum kindOfUser{
        ticketAdmin,manager,sysAdmin,studentSupport
    }

    private static  UserManager current = null;
    private UserManager.kindOfUser kindOfUser;

    public static UserManager getCurrent(){
        if(current==null){
            current = new UserManager();
        }

        return current;
    }

    public void setKindOfUser(UserManager.kindOfUser kindOfUser) {
        this.kindOfUser = kindOfUser;
    }

    public UserManager.kindOfUser getKindOfUser() {
        return kindOfUser;
    }
}
