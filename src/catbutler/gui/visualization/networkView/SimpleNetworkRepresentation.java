package catbutler.gui.visualization.networkView;

import catbutler.model.DataModel;
import catbutler.model.Reaction;
import catbutler.model.Species;
import catbutler.utils.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SimpleNetworkRepresentation {

    public int[][] adjacencyMatrix = null;
    public HashMap<String, double[]> coords = new HashMap<>();
    String[][] adjacencyIdLstMatrix = null;
    ArrayList<String> species = new ArrayList<>();
    ArrayList<String> reactions = new ArrayList<>();
    String type = null;
    HashMap<String, double[]> oldCoords = new HashMap<>();
    HashMap<String, double[]> oldDisp = new HashMap<>();
    HashMap<String, double[]> disp = new HashMap<>();
    HashMap<String, Double> freeze = new HashMap<>();
    HashMap<String, Integer> nodeDegree = new HashMap<>();
    DataModel dataModel;
    String[] iIdx = null;
    String[] jIdx = null;


    public SimpleNetworkRepresentation(DataModel dataModel, String type) {
        this.type = type;
        this.dataModel = dataModel;
        initData();
        initArr();
    }

    private void initData() {
        if (type.contains("sp")) {
            for (Species s : dataModel.getSpeciesSet()) {
                species.add(s.getSpeciesId());
            }
            adjacencyIdLstMatrix = new String[species.size()][dataModel.getReactionSet().size()];
            adjacencyMatrix = new int[species.size()][dataModel.getReactionSet().size()];
            iIdx = new String[species.size()];
            jIdx = new String[dataModel.getReactionSet().size()];
            for (Reaction reaction : dataModel.getReactionSet()) {
                reactions.add(reaction.getReactionId());
                HashSet<String> output = new HashSet<String>(reaction.getOutputSpeciesStrIds());
                HashSet<String> input = new HashSet<String>(reaction.getInputSpeciesStrIds());
                String r = reaction.getReactionId();
                for (String s : output) {
                    if (species.contains(s) && reactions.contains(r)) {
                        adjacencyIdLstMatrix[species.indexOf(s)][reactions.indexOf(r)] += ";o";
                        adjacencyMatrix[species.indexOf(s)][reactions.indexOf(r)] += 1;
                        iIdx[species.indexOf(s)] = s;
                        jIdx[reactions.indexOf(r)] = r;
                        addNodeDegree(s);
                        addNodeDegree(r);
                    }
                }
                for (String s : input) {
                    if (species.contains(s) && reactions.contains(r)) {
                        adjacencyIdLstMatrix[species.indexOf(s)][reactions.indexOf(r)] += ";i";
                        adjacencyMatrix[species.indexOf(s)][reactions.indexOf(r)] += 1;
                        iIdx[species.indexOf(s)] = s;
                        jIdx[reactions.indexOf(r)] = r;
                        addNodeDegree(s);
                        addNodeDegree(r);
                    }
                }
            }
        } else {
            adjacencyIdLstMatrix = new String[dataModel.getReactionSet().size()][dataModel.getReactionSet().size()]; ///[from][to]
            adjacencyMatrix = new int[dataModel.getReactionSet().size()][dataModel.getReactionSet().size()];
            iIdx = new String[dataModel.getReactionSet().size()];
            jIdx = new String[dataModel.getReactionSet().size()];
            for (Reaction reaction : dataModel.getReactionSet()) {
                String r1 = reaction.getReactionId();
                if (!reactions.contains(r1)) {
                    reactions.add(r1);
                }
                HashSet<String> outputR1 = new HashSet<String>(reaction.getOutputSpeciesStrIds());

                for (Reaction reaction2 : dataModel.getReactionSet()) {
                    String r2 = reaction2.getReactionId();
                    if (!reactions.contains(r2)) {
                        reactions.add(r2);
                    }
                    if (!r1.equals(r2)) {
                        HashSet<String> inputR2 = new HashSet<String>(reaction2.getInputSpeciesStrIds());
                        for (String o : outputR1) {
                            if (inputR2.contains(o)) {
                                adjacencyIdLstMatrix[reactions.indexOf(r1)][reactions.indexOf(r2)] += o;
                                adjacencyMatrix[reactions.indexOf(r1)][reactions.indexOf(r2)] += 1;
                                addNodeDegree(r1);
                                addNodeDegree(r2);
                                iIdx[reactions.indexOf(r1)] = r1;
                                jIdx[reactions.indexOf(r2)] = r2;
                            }
                        }
                    }
                }
            }
        }
        /*switch (type) {
            case "speciesReactionsCount":
                for (Species s : dataModel.getSpeciesSet()) {
                    species.add(s.getSpeciesId());
                }
                adjacencyMatrix = new int[species.size()][dataModel.getReactionSet().size()];
                for (Reaction reaction : dataModel.getReactionSet()) {
                    reactions.add(reaction.getReactionId());
                    HashSet<String> participants = new HashSet<String>(reaction.getOutputSpeciesStrIds());
                    participants.addAll(reaction.getInputSpeciesStrIds());
                    String r = reaction.getReactionId();
                    for (String s : participants) {
                        if (species.contains(s) && reactions.contains(r)) {
                            adjacencyMatrix[species.indexOf(s)][reactions.indexOf(r)] += 1;
                            addNodeDegree(s);
                            addNodeDegree(r);
                        }
                    }
                }
                break;
            case "reactionsCount":
                adjacencyMatrix = new int[dataModel.getReactionSet().size()][dataModel.getReactionSet().size()];
                for (Reaction reaction : dataModel.getReactionSet()) {
                    String r1 = reaction.getReactionId();
                    if (!reactions.contains(r1)) {
                        reactions.add(r1);
                    }
                    HashSet<String> outputR1 = new HashSet<String>(reaction.getOutputSpeciesStrIds());

                    for (Reaction reaction2 : dataModel.getReactionSet()) {
                        String r2 = reaction2.getReactionId();
                        if (!reactions.contains(r2)) {
                            reactions.add(r2);
                        }
                        if (!r1.equals(r2)) {
                            HashSet<String> inputR2 = new HashSet<String>(reaction2.getInputSpeciesStrIds());
                            for (String o : outputR1) {
                                if (inputR2.contains(o)) {
                                    adjacencyMatrix[reactions.indexOf(r1)][reactions.indexOf(r2)] += 1;
                                    addNodeDegree(r1);
                                    addNodeDegree(r2);
                                }
                            }
                        }
                    }
                }
                break;
            case "speciesReactionsIdLst":
                for (Species s : dataModel.getSpeciesSet()) {
                    species.add(s.getSpeciesId());
                }
                adjacencyIdLstMatrix = new String[species.size()][dataModel.getReactionSet().size()];
                for (Reaction reaction : dataModel.getReactionSet()) {
                    reactions.add(reaction.getReactionId());
                    HashSet<String> output = new HashSet<String>(reaction.getOutputSpeciesStrIds());
                    HashSet<String> input = new HashSet<String>(reaction.getInputSpeciesStrIds());
                    String r = reaction.getReactionId();
                    for (String s : output) {
                        if (species.contains(s) && reactions.contains(r)) {
                            adjacencyIdLstMatrix[species.indexOf(s)][reactions.indexOf(r)] += ";o";
                            addNodeDegree(s);
                            addNodeDegree(r);
                        }
                    }
                    for (String s : input) {
                        if (species.contains(s) && reactions.contains(r)) {
                            adjacencyIdLstMatrix[species.indexOf(s)][reactions.indexOf(r)] += ";i";
                            addNodeDegree(s);
                            addNodeDegree(r);
                        }
                    }
                }
                break;
            case "reactionsIdLst":
                adjacencyIdLstMatrix = new String[dataModel.getReactionSet().size()][dataModel.getReactionSet().size()]; ///[from][to]
                for (Reaction reaction : dataModel.getReactionSet()) {
                    String r1 = reaction.getReactionId();
                    if (!reactions.contains(r1)) {
                        reactions.add(r1);
                    }
                    HashSet<String> outputR1 = new HashSet<String>(reaction.getOutputSpeciesStrIds());

                    for (Reaction reaction2 : dataModel.getReactionSet()) {
                        String r2 = reaction2.getReactionId();
                        if (!reactions.contains(r2)) {
                            reactions.add(r2);
                        }
                        if (!r1.equals(r2)) {
                            HashSet<String> inputR2 = new HashSet<String>(reaction2.getInputSpeciesStrIds());
                            for (String o : outputR1) {
                                if (inputR2.contains(o)) {
                                    adjacencyIdLstMatrix[reactions.indexOf(r1)][reactions.indexOf(r2)] += o;
                                    addNodeDegree(r1);
                                    addNodeDegree(r2);
                                }
                            }
                        }
                    }
                }
                break;
        }*/
    }

    public String[] getiIdx() {
        return iIdx;
    }

    public String[] getjIdx() {
        return jIdx;
    }

    private void addNodeDegree(String s) {
        if (nodeDegree.containsKey(s)) {
            nodeDegree.put(s, nodeDegree.get(s) + 1);
        } else {
            nodeDegree.put(s, 1);
        }
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public String[][] getAdjacencyIdLstMatrix() {
        return adjacencyIdLstMatrix;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getSpecies() {
        return species;
    }

    public ArrayList<String> getReactions() {
        return reactions;
    }

    public void initArr() {
        for (String r : reactions) {
            coords.put(r, Misc.getRandomCoords(0.0, 1500.0, 0.0, 500.0));
            oldCoords.put(r, Misc.getRandomCoords(0.0, 1500.0, 0.0, 500.0));
            disp.put(r, new double[]{0.0, 0.0});
            oldDisp.put(r, new double[]{0.0, 0.0});
            freeze.put(r, 1.0);
            nodeDegree.put(r, 0);
        }
        if (type.contains("sp")) {
            for (String s : species) {
                coords.put(s, Misc.getRandomCoords(0.0, 1500.0, 0.0, 500.0));
                oldCoords.put(s, Misc.getRandomCoords(0.0, 1500.0, 0.0, 500.0));
                disp.put(s, new double[]{0.0, 0.0});
                oldDisp.put(s, new double[]{0.0, 0.0});
                freeze.put(s, 1.0);
                nodeDegree.put(s, 0);
            }
        }
    }

    public double getMass(String string) {
        return nodeDegree.get(string) + 1;
    }

    public HashMap<String, double[]> getCoords() {
        return coords;
    }

    public HashMap<String, double[]> getOldDisp() {
        return oldDisp;
    }

    public HashMap<String, double[]> getDisp() {
        return disp;
    }


    public HashMap<String, Integer> getNodeDegree() {
        return nodeDegree;
    }

    public HashMap<String, Double> getFreeze() {
        return freeze;
    }
}


