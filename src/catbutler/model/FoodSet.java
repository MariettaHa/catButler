package catbutler.model;

import java.util.*;

public class FoodSet<S> extends HashSet<Species> {

    public FoodSet() {
    }

    public FoodSet(Collection<? extends Species> c) {
        super(c);
    }

    public void addFood(Species species) {
        add(species);
    }

    public void removeFood(Species species) {
        remove(species);
    }

    public ArrayList<Species> getSortedSet() {
        TreeMap<String, Species> sortedIds = new TreeMap<>();
        for (Species species : this) {
            sortedIds.put(species.getSpeciesId(), species);
        }

        ArrayList<String> sortedKeysList = new ArrayList<String>(sortedIds.keySet());
        Collections.sort(sortedKeysList);

        ArrayList<Species> sortedSet = new ArrayList<>();
        for (String s : sortedIds.keySet()) {
            sortedSet.add(sortedIds.get(s));
        }

        return sortedSet;
    }


}
