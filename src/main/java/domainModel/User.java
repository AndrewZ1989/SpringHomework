package domainModel;

import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class User extends DomainObject {

    public User(Long id, LocalDateTime birthDate)
    {
        super(id);
        this.birthDate = birthDate;
    }

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime birthDate;

    private Set<Long> ticketsIds = new TreeSet<>();


    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime date){
        birthDate = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Long> getTicketsIds() {
        return ticketsIds;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.ticketsIds = tickets.stream().map(t -> t.getId()).collect(Collectors.toSet());
    }

    public void setTicketsIds(Set<Long> ticketsIds) {
        this.ticketsIds = ticketsIds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return this.getId() + "  " + this.firstName + ' ' + this.lastName + ": ";
    }

}

