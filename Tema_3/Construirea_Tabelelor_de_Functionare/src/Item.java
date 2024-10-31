class Item {
    public Production production;
    public int dotPosition;
    public Terminal lookahead;

    public Item(Production production, int dotPosition, Terminal lookahead) {
        this.production = production;
        this.dotPosition = dotPosition;
        this.lookahead = lookahead;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Item other)) {
            return false;
        } else {
            return this.production.equals(other.production) && this.dotPosition == other.dotPosition && this.lookahead.equals(other.lookahead);
        }
    }

    public int hashCode() {
        return this.production.hashCode() + this.dotPosition + this.lookahead.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.production.lhs.name).append(" -> ");

        for(int i = 0; i < this.production.rhs.size(); ++i) {
            if (i == this.dotPosition) {
                sb.append(". ");
            }

            sb.append(((Symbol)this.production.rhs.get(i)).name).append(" ");
        }

        if (this.dotPosition == this.production.rhs.size()) {
            sb.append(". ");
        }

        sb.append(", ").append(this.lookahead.name);
        return sb.toString().trim();
    }
}
