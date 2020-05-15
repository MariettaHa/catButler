package catbutler.io.documents;

import catbutler.io.parser.CRSParser;
import catbutler.model.DataModel;

import java.nio.file.Path;

public class CRSDoc extends Doc {

    private CRSParser crsParser = new CRSParser(this);

    public CRSDoc(Path path) {
        super(path);
        setParser(this.crsParser);
    }

    public CRSDoc(String inputStr) {
        super(inputStr);
        setParser(this.crsParser);
    }

    public CRSDoc(DataModel dataModel) {
        super(dataModel);
    }
}
