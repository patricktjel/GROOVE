package groove.ocl.lax;

import java.util.HashMap;
import java.util.Map;

public class VariableFactory {

    // a map from variable name to variable
    private static Map<String, Variable> variables = new HashMap<>();
    private static int unique_name = 0;

    public static Variable createVariable(String clazz) {
        return createVariable(String.valueOf(unique_name++), clazz);
    }

    public static Variable createVariable(String var, String clazz) {
        Variable variable = new Variable(var, clazz);
        variables.put(var, variable);
        return variable;
    }

    public static Variable getVariable(String var) {
        return variables.get(var);
    }

    public static boolean contains(String var) {
        return variables.containsKey(var);
    }
}
