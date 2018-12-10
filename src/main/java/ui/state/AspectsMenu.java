package ui.state;

import aspects.CounterAspect;
import aspects.DiscountAspect;
import aspects.EventStatistics;
import aspects.LuckyWinnerAspect;
import domainModel.Event;
import domainModel.User;

import java.util.HashMap;
import java.util.Map;

public class AspectsMenu extends  AbstractMenu {

    private CounterAspect counterAspect;
    private DiscountAspect discountAspect;
    private LuckyWinnerAspect luckyWinnerAspect;

    public AspectsMenu(CounterAspect counterAspect,
                       DiscountAspect discountAspect,
                       LuckyWinnerAspect luckyWinnerAspect ){
        this.counterAspect = counterAspect;
        this.discountAspect = discountAspect;
        this.luckyWinnerAspect = luckyWinnerAspect;
    }

    @Override
    protected void printDefaultInformation() {
        print("Information given by aspects.");
    }

    @Override
    protected int printMainActions() {
        print(" 1) Get counters info");
        print(" 2) Get discount info");
        print(" 3) Get lucky winners info");
        return 3;
    }

    @Override
    protected void runAction(int action) {
        switch (action){
            case 1:
                showCountersInfo();
                break;
            case 2:
                showDiscountInfo();
                break;
            case 3:
                showLuckyWinnersInfo();
                break;
            default:
                print("Unknown action");
        }
    }

    private void showLuckyWinnersInfo() {
        Map<User,Integer> winners = luckyWinnerAspect.getStatistics();

        print("Lucky users info: ");

        for(Map.Entry<User,Integer> e : winners.entrySet()){
            print(e.getKey().toString() + e.getValue().toString());
        }
    }

    private void showDiscountInfo() {
        Map<String, HashMap<User, Integer>> discounts = discountAspect.getStatistics();

        printDelimiter();
        print("Discounts info: ");
        print("");

        for(Map.Entry<String,HashMap<User, Integer>> d : discounts.entrySet()) {
            String discountType = d.getKey();
            HashMap<User, Integer> discountDetails = d.getValue();

            print(discountType + ": ");

            Integer totalCount = 0;
            for(Map.Entry<User, Integer> stat: discountDetails.entrySet()){
                print(stat.getKey().toString() + " " + stat.getValue());
                totalCount += stat.getValue();
            }
            print("Total: " + totalCount);
            print("");
        }
    }

    private void showCountersInfo() {
        Map<Event, EventStatistics> stat = counterAspect.getStatistics();

        printDelimiter();
        print("Counters for events: ");
        print("");

        for(Map.Entry<Event, EventStatistics> e: stat.entrySet()){
            Event evt = e.getKey();
            EventStatistics st = e.getValue();

            print(evt.getName() + "-----------");
            print("Booked count: "  + st.getBookedTicketsCount());
            print("Price queried count: "  + st.getPriceQueriedCount());
            print("By name access count: " + st.getByNameAccessCount());

            print("");
        }
    }

    private void print(String s){
        System.out.println(s);
    }
}
