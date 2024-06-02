package tables.semantics.expr;

public class IdManager {
    private static IdManager instance;
    private int nextId;

    public ID generateId(String image) {
        return new ID(this.nextId++, image);
    }
    private IdManager() {
        this.nextId = 0;
    }
    public static IdManager getIdManager() {
        if (instance == null) {
            instance = new IdManager();
        }
        return instance;
    }

}
