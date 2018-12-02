package ui.state;


import java.util.HashMap;
        import java.util.Map;

import domainServices.AuditoriumService;
import domainServices.BookingService;
import domainServices.EventService;
import domainServices.UserService;
import org.springframework.context.ApplicationContext;

/**
 */
public class InitialState extends AbstractState {

    private Map<Integer, AbstractState> childStates = new HashMap<>();

    public InitialState(ApplicationContext context) {

        UserService usersSvc = context.getBean(UserService.class);
        EventService eventsSvc = context.getBean(EventService.class);
        AuditoriumService auditoriumSvc = context.getBean(AuditoriumService.class);
        BookingService bookingSvc = context.getBean(BookingService.class);

        childStates.put(1, new AuditoriumManageState(auditoriumSvc));

        childStates.put(2, new EventsManageState(eventsSvc, auditoriumSvc)
        );
        childStates.put(3, new UserManageState(usersSvc));

        childStates.put(4, new BookingManageState(bookingSvc, usersSvc, eventsSvc)
        );
    }

    @Override
    protected void runAction(int action) {
        AbstractState state = childStates.get(action);
        if (state != null) {
            state.run();
        } else {
            System.err.println("No state configured for selected action :(");
        }
    }

    @Override
    protected int printMainActions() {
        System.out.println(" 1) View auditoriums");
        System.out.println(" 2) Manage events");
        System.out.println(" 3) Manage users");
        System.out.println(" 4) Book tickets");
        return 4;
    }

    @Override
    protected void printDefaultInformation() {
        // Doing nothing here
    }

}

