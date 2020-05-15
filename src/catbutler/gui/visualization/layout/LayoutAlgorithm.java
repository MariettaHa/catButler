package catbutler.gui.visualization.layout;

import catbutler.gui.visualization.networkView.SimpleNetworkRepresentation;

public class LayoutAlgorithm {

    SimpleNetworkRepresentation net;

    public LayoutAlgorithm(SimpleNetworkRepresentation net) {
        this.net = net;
    }

    public SimpleNetworkRepresentation getNet() {
        return net;
    }

    public void apply() {
    }
}
