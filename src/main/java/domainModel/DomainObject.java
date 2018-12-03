package domainModel;

public class DomainObject {

    public DomainObject(Long id){
        _id = id;
    }

    private Long _id;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

}

