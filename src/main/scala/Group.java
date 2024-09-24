import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupName;
    private List<User> members;

    public Group(String groupName, User creator) {
        this.groupName = groupName;
        this.members = new ArrayList<>();
        this.members.add(creator);
    }

    public String getGroupName() {
        return groupName;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public List<User> getMembers() {
        return members;
    }

    public int getMemberCount() {
        return members.size();
    }
}
