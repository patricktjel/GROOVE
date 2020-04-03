package groove.ocl;

import java.util.*;

public class OCL {
    public final static String NULL                 = "null";
    public final static String ARROW                = "->";

    public final static Set<String> PRIMARY_OPERATIONS = new HashSet<>(Arrays.asList("bool", "int", "real", "string"));

    public final static String EXCLUDES_ALL         = "excludesAll";
    public final static String EXCLUDES             = "excludes";
    public final static String EXISTS               = "exists";
    public final static String FORALL               = "forAll";
    public final static String INCLUDES_ALL         = "includesAll";
    public final static String INCLUDES             = "includes";
    public final static String IS_EMPTY             = "isEmpty";
    public final static String NOT_EMPTY            = "notEmpty";
    public final static String OCL_AS_TYPE          = "oclAsType";
    public final static String OCL_IS_KIND_OF       = "oclIsKindOf";
    public final static String OCL_IS_TYPE_OF       = "oclIsTypeOf";
    public final static String SIZE                 = "size";

    //SET OPERATIONS
    public final static String EXCLUDING            = "excluding";
    public final static String INCLUDING            = "including";
    public final static String INTERSECTION         = "intersection";
    public final static String MINUS                = "-";
    public final static String SYMMETRICDIFFERENCE  = "symmetricDifference";
    public final static String UNION                = "union";
    public final static Set<String> SET_OPERATIONS = new HashSet<>(Arrays.asList(
            EXCLUDING, INCLUDING, INTERSECTION, MINUS, SYMMETRICDIFFERENCE, UNION
    ));
}
