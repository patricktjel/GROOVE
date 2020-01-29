package groove.gui.jgraph;

import groove.graph.Edge;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.RuleTransition;
import groove.util.Groove;

/**
 * JEdge class that describes the underlying edge as a graph transition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSJEdge extends AJEdge<GTS,LTSJGraph,LTSJModel,LTSJVertex>implements LTSJCell {
    /**
     * Constructs an uninitialised instance.
     * Call {@link #setJModel(JModel)} to initialise.
     */
    private LTSJEdge() {
        super();
    }

    @Override
    public void initialise() {
        super.initialise();
        this.visibleFlag = true;
    }

    @Override
    public void setSource(Object port) {
        super.setSource(port);
        if (port != null) {
            LTSJVertex sourceVertex = getSourceVertex();
            assert sourceVertex != null; // because port != null
            sourceVertex.changeOutVisible(true, getEdges().size());
        }
    }

    @Override
    public void setTarget(Object port) {
        super.setTarget(port);
        if (port != null) {
            LTSJVertex targetVertex = getTargetVertex();
            assert targetVertex != null; // because port != null
            GraphState target = targetVertex.getNode();
            if (target instanceof GraphNextState && getEdges().contains((GraphNextState) target)) {
                targetVertex.setParentEdge(this);
            }
        }
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (!super.isCompatible(edge)) {
            return false;
        }
        GraphTransition trans = (GraphTransition) edge;
        if (isInternal() != trans.isInternalStep()) {
            return false;
        }
        if (isAbsent() != (trans.source()
            .isAbsent()
            || trans.target()
                .isAbsent())) {
            return false;
        }
        return true;
    }

    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        // updates the look on the basis of the edge
        setLook(Look.RECIPE, isInternal());
        setLook(Look.ABSENT, isAbsent());
        LTSJVertex sourceVertex = getSourceVertex();
        if (sourceVertex != null && getVisuals().isVisible()) {
            sourceVertex.changeOutVisible(true, 1);
        }
    }

    @Override
    public GraphTransition getEdge() {
        return (GraphTransition) super.getEdge();
    }

    @Override
    StringBuilder getEdgeKindDescription() {
        return new StringBuilder("transition");
    }

    @Override
    String getLabelDescription() {
        StringBuffer result = new StringBuffer(", generated by ");
        String[] displayedLabels = new String[getEdges().size()];
        int labelIndex = 0;
        for (Object part : getEdges()) {
            RuleTransition trans = (RuleTransition) part;
            String description;
            JGraph<?> jGraph = getJGraph();
            assert jGraph != null; // guaranteed by now
            if (jGraph.isShowAnchors()) {
                description = trans.getEvent()
                    .toString();
            } else {
                description = trans.getEvent()
                    .getRule()
                    .getQualName()
                    .toString();
            }
            displayedLabels[labelIndex] = HTMLConverter.STRONG_TAG.on(description, true);
            labelIndex++;
        }
        if (displayedLabels.length == 1) {
            result.append(displayedLabels[0]);
        } else {
            result.append(Groove.toString(displayedLabels, "<br>- ", "", "<br>- "));
        }
        return result.toString();
    }

    /** Indicates that this edge is active. */
    final boolean isActive() {
        return getLooks().contains(Look.ACTIVE);
    }

    /** Indicates that the node or target of this edge is absent. */
    final boolean isAbsent() {
        return getEdge().source()
            .isAbsent()
            || getEdge().target()
                .isAbsent();
    }

    /** Indicates that this edge is part of a recipe. */
    final boolean isInternal() {
        return getEdge().isInternalStep();
    }

    @Override
    public boolean setVisibleFlag(boolean visible) {
        boolean result = this.visibleFlag != visible;
        if (result) {
            boolean oldVisible = getVisuals().isVisible();
            this.visibleFlag = visible;
            if (visible != oldVisible) {
                setStale(VisualKey.VISIBLE);
                LTSJVertex sourceVertex = getSourceVertex();
                if (sourceVertex != null && visible == getVisuals().isVisible()) {
                    sourceVertex.changeOutVisible(visible, getEdges().size());
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasVisibleFlag() {
        return this.visibleFlag;
    }

    /** Changes the active status of this edge.
     * @return {@code true} if the active status changed as a result of this call.
     */
    @Override
    public final boolean setActive(boolean active) {
        return setLook(Look.ACTIVE, active);
    }

    @Override
    protected Look getStructuralLook() {
        return Look.TRANS;
    }

    private boolean visibleFlag;

    /** Constructs a fresh instance.
     * Call {@link #setJModel(JModel)} to initialise.
     */
    public static LTSJEdge newInstance() {
        return new LTSJEdge();
    }
}