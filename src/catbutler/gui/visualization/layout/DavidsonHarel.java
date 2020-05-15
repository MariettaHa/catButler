package catbutler.gui.visualization.layout;

import catbutler.gui.visualization.networkView.SimpleNetworkRepresentation;
import catbutler.utils.Misc;

import java.util.HashSet;

public class DavidsonHarel extends LayoutAlgorithm {

    double currentEnergy = 0.0;
    double boltzmanConstant = 0.00000001;
    double temperature = 1000.0;
    double temperatureThreshold = 1.0;
    double cooling = 0.75;
    double radius = 100.0;
    double shrinkingContant = 0.8;

    HashSet<String> processedNodes = new HashSet<>();

    public DavidsonHarel(SimpleNetworkRepresentation net) {
        super(net);
    }

    public void apply() {
        double itMax = 20 * net.getCoords().size();

        HashSet<String> processedUs = new HashSet<>();
        for (String u : net.getCoords().keySet()) {
            if (!processedUs.contains(u)) {
                for (String v : net.getCoords().keySet()) {
                    if (!u.equals(v)) {
                        currentEnergy += attractionForce(u, v);
                        currentEnergy += repulsionForce(u, v);
                    }
                }
                processedUs.add(u);
            }
        }

        //initialize random node
        String initialRandomReaction = net.getReactions().get(Misc.randInt(0, net.getReactions().size() - 1));
        net.getCoords().put(initialRandomReaction, new double[]{600., 200.0});
        processedNodes.add(initialRandomReaction);

        while (temperature >= temperatureThreshold) {
            for (String u : net.getCoords().keySet()) {
                double randX = net.getCoords().get(u)[0] + Math.cos(Misc.randDouble(0, 2 * Math.PI)) * radius;
                double randY = net.getCoords().get(u)[1] + Math.sin(Misc.randDouble(0, 2 * Math.PI)) * radius;

                double newE = 0.0;
                for (String v : net.getCoords().keySet()) {
                    double[] delta = {net.getCoords().get(v)[0] - net.getCoords().get(u)[0], net.getCoords().get(v)[1] - net.getCoords().get(u)[1]};
                    double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
                    newE += dist * dist + 1 / ((dist + 1) * (dist + 1));
                }
                if (newE <= currentEnergy) {
                    net.getCoords().put(u, new double[]{randX, randY});
                    currentEnergy = newE;
                } else {
                    double p = Math.exp(-(newE - currentEnergy) / (boltzmanConstant * temperature));
                    double phi = Misc.randDouble(0.0, 1.0);
                    if (p < phi) {
                        net.getCoords().put(u, new double[]{randX, randY});
                        currentEnergy = newE;
                    }
                }
            }

            temperature *= cooling;
            radius *= shrinkingContant;
        }


    }

    private double attractionForce(String u, String v) {
        double[] delta = {net.getCoords().get(v)[0] - net.getCoords().get(u)[0], net.getCoords().get(v)[1] - net.getCoords().get(u)[1]};
        double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
        return dist * dist;
    }

    private double repulsionForce(String u, String v) {
        double[] delta = {net.getCoords().get(v)[0] - net.getCoords().get(u)[0], net.getCoords().get(v)[1] - net.getCoords().get(u)[1]};
        double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
        return 1 / ((dist + 1) * (dist + 1));
    }


}
