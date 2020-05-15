package catbutler.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.*;

public class ReactionSet<R> extends HashSet<Reaction> {

    HashMap<String, Reaction> idToReactionMap = new HashMap<>();
    SimpleDoubleProperty translateXCenterProperty = new SimpleDoubleProperty();
    SimpleDoubleProperty translateYCenterProperty = new SimpleDoubleProperty();
    SimpleSetProperty<Reaction> setProperty = new SimpleSetProperty(FXCollections.observableSet());

    public ReactionSet() {
        initListeners();
    }

    public ReactionSet(Collection<? extends Reaction> c) {
        super(c);
    }

    private void initListeners() {
        setProperty.addListener(new ChangeListener<ObservableSet<Reaction>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableSet<Reaction>> observableValue, ObservableSet<Reaction> reactions, ObservableSet<Reaction> t1) {
                double x = 0.0;
                double y = 0.0;
                int size = setProperty.getSize();
                for (Reaction r : setProperty.getValue()) {
                    x += r.getCenterPosX();
                    y += r.getCenterPosY();
                }
                setTranslateXCenterProperty(x / size);
                setTranslateYCenterProperty(y / size);
            }
        });
    }

    public void addReaction(Reaction reaction) {
        idToReactionMap.put(reaction.getReactionId(), reaction);
        add(reaction);
        setProperty.add(reaction);
    }

    public ArrayList<Reaction> getSortedList() {
        TreeMap<String, Reaction> sortedIds = new TreeMap<>();
        for (Reaction reaction : this) {
            sortedIds.put(reaction.getReactionId(), reaction);
        }

        ArrayList<String> sortedKeysList = new ArrayList<String>(sortedIds.keySet());
        Collections.sort(sortedKeysList);

        ArrayList<Reaction> sortedSet = new ArrayList<>();
        for (String s : sortedKeysList) {
            sortedSet.add(sortedIds.get(s));
        }

        return sortedSet;
    }

    public void setTranslateXCenterProperty(double translateXCenterProperty) {
        this.translateXCenterProperty.set(translateXCenterProperty);
    }

    public void setTranslateYCenterProperty(double translateYCenterProperty) {
        this.translateYCenterProperty.set(translateYCenterProperty);
    }

}
