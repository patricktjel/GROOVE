package groove.ocl.lax;

public class NavigationVariable implements Expression{

    private Variable from;
    private String role;
    private Variable to;

    public NavigationVariable(Variable from, String role, Variable to) {
        this.from = from;
        this.role = role;
        this.to = to;
    }

    public Variable getFrom() {
        return from;
    }

    public String getRole() {
        return role;
    }

    public Variable getTo() {
        return to;
    }

    @Override
    public void renameVar(String o, String n) {
        from.renameVar(o,n);
        to.renameVar(o, n);
    }

    @Override
    public String toString() {
        return String.format("%s\u2192(role)%s", from, to);
    }
}
