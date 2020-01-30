package groove;

import groove.ocl.OCLParser;

public class parseOCL {

    static public void main(String[] args) {
        OCLParser parser = new OCLParser();
        parser.parseOCL("context Place inv: self.token >= 0");
    }
}
