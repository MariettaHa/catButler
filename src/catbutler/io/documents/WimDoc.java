package catbutler.io.documents;

import catbutler.io.parser.WimParser;
import catbutler.model.DataModel;

import java.nio.file.Path;
import java.util.ArrayList;

public class WimDoc extends Doc {

    private ArrayList<String> metaData = new ArrayList<>();
    private int molCount = 0;
    private int foodCount = 0;
    private int reactionCount = 0;
    private WimParser wimParser = new WimParser(this);

    public WimDoc(Path path) {
        super(path);
        setParser(this.wimParser);
    }

    public WimDoc(String inputStr) {
        super(inputStr);
        setParser(this.wimParser);
    }

    public WimDoc(DataModel dataModel) {
        super(dataModel);
        molCount = dataModel.getSpeciesSet().size() - dataModel.getFoodSet().size();
        foodCount = dataModel.getFoodSet().size();
        reactionCount = dataModel.getReactionSet().size();
    }

    public ArrayList<String> getMetaData() {
        return metaData;
    }

    public void setMetaData(ArrayList<String> metaData) {
        this.metaData = metaData;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void setFoodCount(int foodCount) {
        this.foodCount = foodCount;
    }

    public void setMolCount(int molCount) {
        this.molCount = molCount;
    }

    public void setReactionCount(int reactionCount) {
        this.reactionCount = reactionCount;
    }

}
