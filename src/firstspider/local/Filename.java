package firstspider.local;

import java.util.Objects;

public class Filename implements Comparable {

    private String name = "";
    private String infix = "";

    public Filename(String name, String infix) {
        this.name = name;
        this.infix = infix;
    }

    @Override
    public int hashCode() {
        return this.infix.hashCode() * this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Filename ob = (Filename) obj;
        return obj instanceof Filename && ob.getInfix().equals(this.infix) && ob.getName().equals(this.name);
    }

    public String getName() {
        return name;
    }

    public String getInfix() {
        return infix;
    }

    @Override
    public String toString() {
        return this.name + "." + this.infix;
    }

    @Override
    public int compareTo(Object o) {
        return Objects.hashCode(o.toString()) - Objects.hashCode(this.toString());
    }
}
