package ui.state;


import java.util.HashMap;
        import java.util.Map;

import aspects.CounterAspect;
import aspects.DiscountAspect;
import aspects.LuckyWinnerAspect;
import domainServices.AuditoriumService;
import domainServices.BookingService;
import domainServices.EventService;
import domainServices.UserService;
import org.springframework.context.ApplicationContext;

/**
 */
public class InitialMenu extends AbstractMenu {

    private Map<Integer, AbstractMenu> childStates = new HashMap<>();

    public InitialMenu(ApplicationContext context) {

        UserService usersSvc = context.getBean(UserService.class);
        EventService eventsSvc = context.getBean(EventService.class);
        AuditoriumService auditoriumSvc = context.getBean(AuditoriumService.class);
        BookingService bookingSvc = context.getBean(BookingService.class);

        childStates.put(1, new AuditoriumManageMenu(auditoriumSvc));

        childStates.put(2, new EventsManageMenu(eventsSvc, auditoriumSvc)
        );
        childStates.put(3, new UserManageMenu(usersSvc));

        childStates.put(4, new BookingManageMenu(bookingSvc, usersSvc, eventsSvc)
        );

        CounterAspect counterAspect = context.getBean(CounterAspect.class);
        DiscountAspect discountAspect = context.getBean(DiscountAspect.class);
        LuckyWinnerAspect luckyWinnerAspect = context.getBean(LuckyWinnerAspect.class);

        childStates.put(5, new AspectsMenu(counterAspect, discountAspect, luckyWinnerAspect));
    }

    @Override
    protected void runAction(int action) {
        AbstractMenu state = childStates.get(action);
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
        System.out.println(" 5) Aspects");
        return 5;
    }

    @Override
    protected void printDefaultInformation() {
        // Doing nothing here
    }

}

