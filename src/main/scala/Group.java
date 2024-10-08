import java.util.HashSet;
import java.util.Set;

public class Group {
    private String name;
    private Set<ClientHandler> members;

    public Group(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<ClientHandler> getMembers() {
        return members;
    }

    public void addMember(ClientHandler member) {
        members.add(member);
    }

    public void removeMember(ClientHandler member) {
        members.remove(member);
    }

    // Modificamos este método para que reciba el nombre del remitente
    public void sendMessage(String message, String sender) {
        for (ClientHandler member : members) {
            member.sendMessage(message, sender);  // Ahora pasamos también el remitente
        }
    }
}
