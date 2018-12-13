package ui.state;

import java.util.Collection;

import domainModel.Auditorium;
import domainServices.AuditoriumService;

/**
 * State for managing auditoriums
 */
public class AuditoriumManageMenu extends AbstractMenu {

    private AuditoriumService auditoriumService;

    public AuditoriumManageMenu(AuditoriumService svc) {
        auditoriumService = svc;
    }

    @Override
    protected int printMainActions() {
        System.out.println(" 1) Search auditorium by name");
        System.out.println(" 2) View all");
        return 2;
    }

    @Override
    protected void runAction(int action) {
        switch (action) {
            case 1:
                searchAuditorium();
                break;
            case 2:
                printDefaultInformation();
                break;
            default:
                System.err.println("Unknown action");
        }
    }

    private void searchAuditorium() {
        String searchTerm = readStringInput("Input auditorium name: ");
        Auditorium a = auditoriumService.getByName(searchTerm);
        if (a == null) {
            System.out.println("Not found (searched for: " + searchTerm + ")");
        } else {
            printAuditorium(a);
        }
    }

    @Override
    protected void printDefaultInformation() {
        System.out.println("All auditoriums:");
        Collection<Auditorium> all = auditoriumService.getAll();
        all.forEach(a -> printAuditorium(a));
    }

    private void printAuditorium(Auditorium a) {
        System.out.println(a.getName() + ", " + a.getNumberOfSeats() + " seats, vips: " + a.getVipSeats());
    }

}

