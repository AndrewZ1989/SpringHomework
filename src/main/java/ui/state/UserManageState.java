package ui.state;

import java.util.Locale;
import org.springframework.context.ApplicationContext;
import domainModel.User;
import domainServices.UserService;

/**
 */
public class UserManageState extends DomainManageState<User, UserService> {

    public UserManageState(UserService userService) {
        super(userService);
    }

    @Override
    protected int printSubActions(int maxDefaultActions) {
        int index = maxDefaultActions;
        System.out.println(" " + (++index) + ") Find user by e-mail");
        return index - maxDefaultActions;
    }

    @Override
    protected void runSubAction(int action, int maxDefaultActions) {
        int index = action - maxDefaultActions;
        switch (index) {
            case 1:
                findUserByEmail();
                break;
            default:
                System.err.println("Unknown action");
        }
    }

    private void findUserByEmail() {
        String email = readStringInput("Input user e-mail: ");
        User user = service.getUserByEmail(email);
        if (user == null) {
            System.out.println("Not found (searched for " + email + ")");
        } else {
            printObject(user);
        }
    }

    @Override
    protected String getObjectName() {
        return User.class.getSimpleName().toLowerCase(Locale.ROOT);
    }

    @Override
    protected void printObject(User user) {
        System.out.println("[" + user.getId() + "] " + user.getFirstName() + " " + user.getLastName() + ", "
                + user.getEmail() + ", bought " + user.getTickets().size() + " tickets");
    }

    @Override
    protected User createObject() {
        System.out.println("Adding user");
        String firstName = readStringInput("First name: ");
        String lastName = readStringInput("Last name: ");
        String email = readStringInput("E-mail: ");

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
    }

}

