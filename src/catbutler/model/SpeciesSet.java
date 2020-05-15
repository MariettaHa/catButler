package catbutler.model;

import java.util.*;

public class SpeciesSet<S> extends HashSet<Species> {

    HashMap<String, Species> idToSpeciesMap = new HashMap<>();

    public SpeciesSet() {

    }

    public SpeciesSet(HashSet<Species> sortedSet) {
        for (Species s : sortedSet) {
            if (!s.getSpeciesId().isBlank()) {
                this.addSpecies(s);
            }
        }
    }

    public SpeciesSet(ArrayList<Species> sortedSet) {
        for (Species s : sortedSet) {
            if (!s.getSpeciesId().isBlank()) {
                this.addSpecies(s);
            }
        }
    }


    public void addSpecies(Species species) {
        if (!species.getSpeciesId().isBlank()) {
            idToSpeciesMap.put(species.getSpeciesId(), species);
            add(species);
        }
    }

    public void addSpecies(String speciesId) {
        if (!speciesId.isBlank()) {
            Species species = new Species(speciesId);
            idToSpeciesMap.put(speciesId, species);
            add(species);
        }
    }

    public void removeSpecies(Species species) {
        if (contains(species)) {
            remove(species);
            idToSpeciesMap.remove(species.getSpeciesId());
        }
    }


    public void removeAllSpecies(FoodSet<Species> species) {
        for (Species s : species) {
            this.removeSpecies(s);
        }
    }

    public HashMap<String, Species> getIdToSpeciesMap() {
        return idToSpeciesMap;
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
