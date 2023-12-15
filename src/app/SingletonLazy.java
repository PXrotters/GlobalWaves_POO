package app;

/**
 * Implementare a design pattern-ului Singleton cu initializare lenesa.
 * Aceasta clasa furnizeaza o singura instanta a clasei Admin si permite
 * accesul la aceasta instanta prin intermediul metodei {@code getInstance}.
 */
public class SingletonLazy {
    /**
     * Instanta unica a clasei Admin.
     */
    private static Admin instance = null;

    /**
     * Constructor privat pentru a preveni instantierea directa.
     */
    private SingletonLazy() {
    }

    /**
     * Returneaza instanta unica a clasei Admin. Daca instanta nu exista,
     * aceasta este creata la prima solicitare si returnata.
     *
     * @return Instanta unica a clasei Admin.
     */
    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }
}