package catbutler.gui.visualization.layout;

import catbutler.gui.visualization.networkView.SimpleNetworkRepresentation;
import catbutler.utils.Misc;

public class ForceAtlas extends LayoutAlgorithm {

    double inertia = 0.2;
    double repulsionStrength = 10000.0;
    double attractionStrength = 0.000001;
    double maxDisplacement = 10.0;
    boolean freezeBalance = true;
    double freezeStrength = 190.0;
    double freezeInertia = 0.2;
    double gravity = 1000.0;
    boolean outboundAttractionDistribution = false;
    boolean adjustSizes = false;
    double speed = 1.0;
    double cooling = 1.0;
    boolean dynamicWeight = false;
    int iterations = 10000;

    public ForceAtlas(SimpleNetworkRepresentation net) {
        super(net);
    }

    @Override
    public void apply() {

        for (String node : net.getCoords().keySet()) {
            double[] coords = net.getCoords().get(node);
            while (coords[0] + coords[1] == 0) {
                coords = Misc.getRandomCoords(0.0, 1500.0, 0.0, 500.0);
            }
            net.getOldDisp().put(node, new double[]{
                    net.getOldDisp().get(node)[0] * inertia, net.getOldDisp().get(node)[1] * inertia});
        }

        //repulsion
        if (adjustSizes) {
            for (String u : net.getCoords().keySet()) {
                for (String v : net.getCoords().keySet()) {
                    if (!u.equals(v)) {
                        fcBiRepulsor_noCollide(v, u, repulsionStrength * (1 + net.getNodeDegree().get(v)) * (1 + net.getNodeDegree().get(u)));
                    }
                }
            }
            for (String u : net.getCoords().keySet()) {
                for (String v : net.getCoords().keySet()) {
                    if (!u.equals(v)) {
                        fcBiRepulsor_noCollide(v, u, repulsionStrength * (1 + net.getNodeDegree().get(v)) * (1 + net.getNodeDegree().get(u)));
                    }
                }
            }
        } else {
            for (String u : net.getCoords().keySet()) {
                for (String v : net.getCoords().keySet()) {
                    if (!u.equals(v)) {
                        fcBiRepulsor(v, u, repulsionStrength * (1 + net.getNodeDegree().get(v)) * (1 + net.getNodeDegree().get(u)));
                    }
                }
            }
        }

        // attraction
        if (adjustSizes) {
            if (outboundAttractionDistribution) {
                for (String u : net.getCoords().keySet()) {
                    for (String v : net.getCoords().keySet()) {
                        if (!u.equals(v)) {
                            fcBiAttractor_noCollide(u, v, attractionStrength / (1 + net.getNodeDegree().get(u)));
                        }
                    }
                }
            } else {
                for (String u : net.getCoords().keySet()) {
                    for (String v : net.getCoords().keySet()) {
                        if (!u.equals(v)) {
                            fcBiAttractor_noCollide(u, v, attractionStrength);
                        }
                    }
                }
            }
        } else {
            if (outboundAttractionDistribution) {
                for (String u : net.getCoords().keySet()) {
                    for (String v : net.getCoords().keySet()) {
                        if (!u.equals(v)) {
                            fcBiAttractor(u, v, attractionStrength / (1 + net.getNodeDegree().get(u)));
                        }
                    }
                }
            } else {
                for (String u : net.getCoords().keySet()) {
                    for (String v : net.getCoords().keySet()) {
                        if (!u.equals(v)) {
                            fcBiAttractor(u, v, attractionStrength);
                        }
                    }
                }
            }
        }
        for (String node : net.getCoords().keySet()) {
            double[] coords = net.getCoords().get(node);
            double d = 0.0001 + Math.sqrt(coords[0] * coords[0] + coords[1] * coords[1]);
            double gf = 0.0001 * gravity * d;
            net.getDisp().put(node, new double[]{
                    net.getDisp().get(node)[0] - gf * coords[0] / d, net.getDisp().get(node)[1] - gf * coords[1] / d});
        }

        // speed
        if (freezeBalance) {
            for (String node : net.getCoords().keySet()) {
                net.getDisp().put(node, new double[]{
                        net.getDisp().get(node)[0] * speed * 10.0,
                        net.getDisp().get(node)[1] * speed * 10.0,
                });
            }
        } else {
            for (String node : net.getCoords().keySet()) {
                net.getDisp().put(node, new double[]{
                        net.getDisp().get(node)[0] * speed,
                        net.getDisp().get(node)[1] * speed,
                });
            }
        }
        // apply forces
        for (String node : net.getCoords().keySet()) {
            double d = 0.0001 + Math.sqrt(net.getDisp().get(node)[0] * net.getDisp().get(node)[0] + net.getDisp().get(node)[1] * net.getDisp().get(node)[1]);
            double ratio;
            if (freezeBalance) {
                net.getFreeze().put(node, freezeInertia * net.getFreeze().get(node) + (1 - freezeInertia) * 0.1 * freezeStrength * (Math.sqrt(
                        Math.sqrt((net.getOldDisp().get(node)[0] - net.getDisp().get(node)[0]) * (net.getOldDisp().get(node)[0] - net.getDisp().get(node)[0])
                                + (net.getOldDisp().get(node)[1] - net.getDisp().get(node)[1]) * (net.getOldDisp().get(node)[1] - net.getDisp().get(node)[1]))))
                );
                ratio = (float) Math.min((d / (d * (1f + net.getFreeze().get(node)))), maxDisplacement / d);
            } else {
                ratio = (float) Math.min(1, maxDisplacement / d);
            }

            net.getDisp().put(node, new double[]{
                    net.getDisp().get(node)[0] * ratio / cooling,
                    net.getDisp().get(node)[1] * ratio / cooling,
            });

            net.getCoords().put(node, new double[]{net.getCoords().get(node)[0] + net.getDisp().get(node)[0], net.getCoords().get(node)[1] + net.getDisp().get(node)[1]});
        }
    }

    public void fcBiRepulsor(String v, String u, double c) {
        double[] delta = {net.getCoords().get(v)[0] - net.getCoords().get(u)[0], net.getCoords().get(v)[1] - net.getCoords().get(u)[1]};
        double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);

        if (dist > 0) {
            double f = repulsion(c, dist);

            net.getDisp().put(v, new double[]{
                    net.getDisp().get(v)[0] + delta[0] / dist * f,
                    net.getDisp().get(v)[1] + delta[1] / dist * f,
            });

            net.getDisp().put(u, new double[]{
                    net.getDisp().get(u)[0] - delta[0] / dist * f,
                    net.getDisp().get(u)[1] - delta[1] / dist * f,
            });
        }
    }

    public void fcBiRepulsor_noCollide(String v, String u, double c) {
        double[] delta = {net.getCoords().get(v)[0] - net.getCoords().get(u)[0], net.getCoords().get(v)[1] - net.getCoords().get(u)[1]};
        double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
        if (dist > 0) {
            double f = repulsion(c, dist);

            net.getDisp().put(v, new double[]{
                    net.getDisp().get(v)[0] + delta[0] / dist * f,
                    net.getDisp().get(v)[1] + delta[1] / dist * f,
            });

            net.getDisp().put(u, new double[]{
                    net.getDisp().get(u)[0] - delta[0] / dist * f,
                    net.getDisp().get(u)[1] - delta[1] / dist * f,
            });


        } else if (dist != 0) {
            double f = -c;    //flat repulsion

            net.getDisp().put(v, new double[]{
                    net.getDisp().get(v)[0] + delta[0] / dist * f,
                    net.getDisp().get(v)[1] + delta[1] / dist * f,
            });

            net.getDisp().put(u, new double[]{
                    net.getDisp().get(u)[0] - delta[0] / dist * f,
                    net.getDisp().get(u)[1] - delta[1] / dist * f,
            });
        }
    }


    public void fcBiAttractor(String v, String u, double c) {
        double[] delta = {net.getCoords().get(v)[0] - net.getCoords().get(u)[0], net.getCoords().get(v)[1] - net.getCoords().get(u)[1]};
        double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);

        if (dist > 0) {
            double f = attraction(c, dist);

            net.getDisp().put(v, new double[]{
                    net.getDisp().get(v)[0] + delta[0] / dist * f,
                    net.getDisp().get(v)[1] + delta[1] / dist * f,
            });

            net.getDisp().put(u, new double[]{
                    net.getDisp().get(u)[0] - delta[0] / dist * f,
                    net.getDisp().get(u)[1] - delta[1] / dist * f,
            });
        }
    }

    public void fcBiAttractor_noCollide(String v, String u, double c) {
        double[] delta = {net.getCoords().get(v)[0] - net.getCoords().get(u)[0], net.getCoords().get(v)[1] - net.getCoords().get(u)[1]};
        double dist = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);

        if (dist > 0) {
            double f = attraction(c, dist);

            net.getDisp().put(v, new double[]{
                    net.getDisp().get(v)[0] + delta[0] / dist * f,
                    net.getDisp().get(v)[1] + delta[1] / dist * f,
            });

            net.getDisp().put(u, new double[]{
                    net.getDisp().get(u)[0] - delta[0] / dist * f,
                    net.getDisp().get(u)[1] - delta[1] / dist * f,
            });
        }

    }

    private double attraction(double c, double dist) {
        return 0.01 * -c * dist;
    }

    private double repulsion(double c, double dist) {
        return 0.001 * c / dist;
    }

}
