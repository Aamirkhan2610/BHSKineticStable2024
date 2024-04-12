package Model;

public class ReactionModel {
    private String reactioName="";

    public String getMsgSeqNumber() {
        return msgSeqNumber;
    }

    public void setMsgSeqNumber(String msgSeqNumber) {
        this.msgSeqNumber = msgSeqNumber;
    }

    private String msgSeqNumber="";

    public String getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(String reactionCount) {
        this.reactionCount = reactionCount;
    }

    private String reactionCount="0";
    public String getReactioName() {
        return reactioName;
    }

    public void setReactioName(String reactioName) {
        this.reactioName = reactioName;
    }

    public int getReactionIcon() {
        return reactionIcon;
    }

    public void setReactionIcon(int reactionIcon) {
        this.reactionIcon = reactionIcon;
    }

    private int reactionIcon=0;
}
