package catbutler.io.documents;

import catbutler.gui.ui.QuickInfoPopup;
import catbutler.io.parser.Parser;
import catbutler.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Doc {

    private Path path;
    private Parser parser;
    private DataModel dataModel;
    private String inputStr = "";
    private ReactionSet<Reaction> reactionSet = new ReactionSet<>();
    private FoodSet<Species> speciesFoodSet = new FoodSet<>();
    private SpeciesSet<Species> speciesSet = new SpeciesSet<>();
    private ArrayList<String> comments = new ArrayList<>();

    public Doc(Path path) {
        this.path = path;
    }

    public Doc(String inputStr) {
        this.inputStr = inputStr;
    }


    public Doc(DataModel dataModel) {
        setReactionSet(dataModel.getReactionSet());
        setSpeciesFoodSet(dataModel.getFoodSet());
        setSpeciesSet(dataModel.getSpeciesSet());
        comments.addAll(dataModel.getComments());
        setDataModel(dataModel);
    }

    public void readIn() {
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(parser::parseLine);
        } catch (IOException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
        dataModel = new DataModel(this);
        dataModel.initDescendants();
        int p = 0;
    }

    public void readIn(String in) {
        Arrays.asList(in.split("[\n]")).forEach(parser::parseLine);
        dataModel = new DataModel(this);
        dataModel.initDescendants();
        int p = 0;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public ReactionSet<Reaction> getReactionSet() {
        return reactionSet;
    }

    public void setReactionSet(ReactionSet<Reaction> reactionSet) {
        this.reactionSet = reactionSet;
    }

    public FoodSet<Species> getSpeciesFoodSet() {
        return speciesFoodSet;
    }

    public void setSpeciesFoodSet(FoodSet<Species> speciesFoodSet) {
        this.speciesFoodSet = speciesFoodSet;
    }

    public SpeciesSet<Species> getSpeciesSet() {
        return speciesSet;
    }

    public void setSpeciesSet(SpeciesSet<Species> speciesSet) {
        this.speciesSet = speciesSet;
    }

    public void addSpecies(Species s) {
        speciesSet.addSpecies(s);
    }

    public void addSpecies(String s) {
        speciesSet.addSpecies(s);
    }

    public void addFood(Species s) {
        speciesFoodSet.addFood(s);
    }

    public void addReaction(Reaction r) {
        reactionSet.addReaction(r);
    }


    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public String getInputStr() {
        return inputStr;
    }

    public void setInputStr(String inputStr) {
        this.inputStr = inputStr;
    }
}
