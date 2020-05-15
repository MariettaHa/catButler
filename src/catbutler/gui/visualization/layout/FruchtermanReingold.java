package catbutler.gui.visualization.layout;


import catbutler.gui.visualization.networkView.SimpleNetworkRepresentation;

public class FruchtermanReingold extends LayoutAlgorithm {

    double temp;
    double width;
    double height;
    double depth;
    double area = 10000;
    double repulseRad;
    double k;
    double gravity = 10;
    double speed = 1;
    double speedDivisor = 800.0;
    double maxDisplace = 1;
    double maxXNode = 0.0;
    double maxYNode = 0.0;
    double maxZNode = 0.0;
    int iterations = 1;
    double cooling = 0.01;
    boolean is3D = false;

    public FruchtermanReingold(SimpleNetworkRepresentation net) {
        super(net);
    }

    public void apply() {

        k = area / (net.getCoords().size());
        area = 333 * net.getCoords().size();
        for (String u : net.getCoords().keySet()) {
            for (String v : net.getCoords().keySet()) {
                if (!u.equals(v)) {
                    double[] delta = {
                            net.getCoords().get(u)[0] - net.getCoords().get(v)[0],
                            net.getCoords().get(u)[1] - net.getCoords().get(v)[1]};

                    double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
                    if (dist > 0) {
                        double repulsiveF = k * k / dist;
                        net.getDisp().put(u, new double[]{
                                net.getDisp().get(u)[0] + (delta[0] / dist) * repulsiveF,
                                net.getDisp().get(u)[1] + (delta[1] / dist) * repulsiveF});
                    }
                }
            }
        }

        //calculate attractive forces
        for (String u : net.getCoords().keySet()) {
            for (String v : net.getCoords().keySet()) {
                if (!u.equals(v)) {
                    double[] delta = {net.getCoords().get(u)[0] - net.getCoords().get(v)[0], net.getCoords().get(u)[1] - net.getCoords().get(v)[1]};
                    double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
                    double attractiveF = (dist * dist) / k;

                    if (dist > 0) {
                        net.getDisp().put(u, new double[]{
                                net.getDisp().get(u)[0] - (delta[0] / dist) * attractiveF,
                                net.getDisp().get(u)[1] - (delta[1] / dist) * attractiveF});
                        net.getDisp().put(v, new double[]{
                                net.getDisp().get(v)[0] + (delta[0] / dist) * attractiveF,
                                net.getDisp().get(v)[1] + (delta[1] / dist) * attractiveF});
                    }
                }
            }
        }

        //gravity (from gephi implementation)
        for (String node : net.getCoords().keySet()) {
            double d = Math.sqrt(net.getCoords().get(node)[0] * net.getCoords().get(node)[0] + net.getCoords().get(node)[1] * net.getCoords().get(node)[1]);
            double gf = 0.01 * k * gravity * d;
            net.getDisp().put(node, new double[]{
                    net.getDisp().get(node)[0] - gf * net.getCoords().get(node)[0] / d,
                    net.getDisp().get(node)[1] - gf * net.getCoords().get(node)[1] / d,
            });
        }

        //speed (from gephi implementation)
        for (String node : net.getCoords().keySet()) {
            net.getDisp().put(node, new double[]{
                    net.getDisp().get(node)[0] * speed / speedDivisor,
                    net.getDisp().get(node)[1] * speed / speedDivisor});
        }

        for (String node : net.getCoords().keySet()) {
            double dist = Math.sqrt(net.getDisp().get(node)[0] * net.getDisp().get(node)[0] + net.getDisp().get(node)[1] * net.getDisp().get(node)[1]);
            if (dist > 0) {
                double limitedDist = Math.min(maxDisplace * (speed / speedDivisor), dist);
                net.getCoords().put(node, new double[]{net.getCoords().get(node)[0] + net.getDisp().get(node)[0] * limitedDist,
                        net.getCoords().get(node)[1] + net.getDisp().get(node)[1] * limitedDist});
            }
        }

        temp *= cooling;
    }

}