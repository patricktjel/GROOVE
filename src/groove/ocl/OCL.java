package groove.ocl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OCL {
    public final static String NULL = "null";

    public final static List<String> PRIMARY_OPERATIONS = new ArrayList<>(Arrays.asList("bool", "int", "real", "string"));

    public final static String EXCLUDES_ALL     = "excludesAll";
    public final static String EXCLUDES         = "excludes";
    public final static String EXISTS           = "exists";
    public final static String FORALL           = "forall";
    public final static String INCLUDES_ALL     = "includesAll";
    public final static String INCLUDES         = "includes";
    public final static String IS_EMPTY         = "isEmpty";
    public final static String NOT_EMPTY        = "notEmpty";
    public final static String OCL_AS_TYPE      = "oclAsType";
    public final static String OCL_IS_KIND_OF   = "oclIsKindOf";
    public final static String OCL_IS_TYPE_OF   = "oclIsTypeOf";
    public final static String SIZE             = "size";
}
