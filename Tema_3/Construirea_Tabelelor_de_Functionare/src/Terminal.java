class Terminal extends Symbol {
    public Terminal(String name) {
        super(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Terminal)) return false;
        return this.name.equals(((Terminal) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
