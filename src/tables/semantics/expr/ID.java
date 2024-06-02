package tables.semantics.expr;

import java.util.Objects;

public record ID(int id, String image) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ID id1 = (ID) o;
        return id == id1.id && Objects.equals(image, id1.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image);
    }
}
