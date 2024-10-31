import java.util.List;

class Production {
    public NonTerminal lhs;
    public List<Symbol> rhs;

    public Production(NonTerminal lhs, List<Symbol> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Production)) return false;
        Production other = (Production) obj;
        return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
    }

    @Override
    public int hashCode() {
        return lhs.hashCode() + rhs.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lhs.name).append(" -> ");
        for (Symbol s : rhs) {
            sb.append(s.name).append(" ");
        }
        return sb.toString().trim();
    }
}
