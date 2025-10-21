package ma.ensa.notification_ms.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookCreatedEvent {
    private String title;
    private String author;
    private int availableCopies;

    @Override
    public String toString(){
        return "New book added: \"" + title + "\" by " + author +
                " (" + availableCopies + " copies available)";
    }

}

