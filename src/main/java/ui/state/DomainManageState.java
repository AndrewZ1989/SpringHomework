package ui.state;

import domainModel.DomainObject;
import domainServices.AbstractDomainObjectService;

/**
 */
public abstract class DomainManageState<T extends DomainObject, S extends AbstractDomainObjectService<T>>
        extends AbstractState {

    protected S service;

    public DomainManageState(S service) {
        this.service = service;
    }

    @Override
    protected void printDefaultInformation() {
        System.out
                .println("Currently there are " + service.getAll().size() + " " + getObjectName() + "s in the system");
    }

    @Override
    protected final int printMainActions() {
        System.out.println(" 1) Add " + getObjectName());
        System.out.println(" 2) View all " + getObjectName() + "s");
        System.out.println(" 3) Find " + getObjectName() + " by id");
        System.out.println(" 4) Remove " + getObjectName());
        int subActions = printSubActions(4);
        return 4 + subActions;
    }

    protected abstract T createObject();

    protected abstract String getObjectName();

    protected abstract void printObject(T object);

    protected abstract int printSubActions(int maxDefaultActions);

    protected abstract void runSubAction(int action, int maxDefaultActions);

    @Override
    protected final void runAction(int action) {
        switch (action) {
            case 1:
                addObject();
                break;
            case 2:
                printAllObjects();
                break;
            case 3:
                findObjectById();
                break;
            case 4:
                removeObject();
                break;
            default:
                runSubAction(action, 4);
                break;
        }
    }

    private void removeObject() {
        int id = readIntInput("Input id: ");
        T obj = service.getById(Long.valueOf(id));
        if (obj == null) {
            System.out.println("Not found (searched for " + id + ")");
        } else {
            service.remove(obj);
            System.out.println("Removed");
            printDefaultInformation();
        }
    }

    private void findObjectById() {
        int id = readIntInput("Input id: ");
        T obj = service.getById(Long.valueOf(id));
        if (obj == null) {
            System.out.println("Not found (searched for " + id + ")");
        } else {
            printObject(obj);
        }
    }

    private void printAllObjects() {
        service.getAll().forEach(obj -> printObject(obj));
    }

    private void addObject() {
        T obj = createObject();

        T newObj = service.save(obj);

        System.out.println("Added");
        printObject(newObj);
    }

}

