package ma.ensa.notification_ms.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookStockOverEvent {
    private Long bookId;
    private String title;
    private String author;
    private int availableCopies;



    @Override
    public String toString(){
        return "This book is not available in the store : "
                + bookId + " -- " + title + " -- " + author;
    }

}
