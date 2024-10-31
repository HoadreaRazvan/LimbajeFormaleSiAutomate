import java.util.Set;

class State {
    public Set<Item> items;

    public State(Set<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) return false;
        State other = (State) obj;
        return this.items.equals(other.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
