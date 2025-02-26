package model;

import java.util.Objects;

public abstract class FEObject {
    public int id;
    public String name;

    public boolean equals(Object o) {
        return this == o || o instanceof FEObject && id == ((FEObject) o).id;
    }

    public int hashCode() {
        return Objects.hash(id);
    }

    public String toString() {
        return name;
    }
}
