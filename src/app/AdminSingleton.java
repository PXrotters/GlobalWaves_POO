package app;

public class AdminSingleton {
    private static Admin instance;

    private AdminSingleton() {

    }
    public static Admin getInstance() {
        if (instance == null) {
            synchronized (AdminSingleton.class) {
                if (instance == null) {
                    instance = new Admin();
                }
            }
        }
        return instance;
    }
}
