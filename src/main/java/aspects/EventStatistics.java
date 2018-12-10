package aspects;

public class EventStatistics{
    private Integer byNameAccessCount;
    private Integer priceQueriedCount;
    private Integer bookedTicketsCount;

    public  EventStatistics(){
        byNameAccessCount = 0;
        priceQueriedCount = 0;
        bookedTicketsCount = 0;
    }

    public Integer getByNameAccessCount(){
        return byNameAccessCount;
    }
    public void setByNameAccessCount(Integer value){
        byNameAccessCount = value;
    }

    public Integer getPriceQueriedCount() {
        return priceQueriedCount;
    }
    public void setPriceQueriedCount(Integer priceQueriedCount) {
        this.priceQueriedCount = priceQueriedCount;
    }

    public Integer getBookedTicketsCount() {
        return bookedTicketsCount;
    }
    public void setBookedTicketsCount(Integer bookedTicketsCount) {
        this.bookedTicketsCount = bookedTicketsCount;
    }
}
