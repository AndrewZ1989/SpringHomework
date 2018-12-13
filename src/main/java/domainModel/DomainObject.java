package domainModel;

public class DomainObject {

    public DomainObject(Long id){
        this.id = id;
    }

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

