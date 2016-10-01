package stonehorse.grit.map.hash;

public class Result {
    public final Node node;
    public final boolean isResized;

    private Result(Node node, boolean isResized) {
        this.node = node;
        this.isResized = isResized;
    }

    public static Result result(Node node) {
        return new Result(node, false);
    }

    public Result withResize(boolean isResize) {
        return new Result(node, isResize);
    }

    public Result withResizeOf(Result result) {
        return new Result(node, result.isResized);
    }

}
