class NonTerminal extends Symbol {
    public NonTerminal(String name) {
        super(name);
    }

    public boolean equals(Object obj) {
        return !(obj instanceof NonTerminal) ? false : this.name.equals(((NonTerminal)obj).name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
