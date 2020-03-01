package flynn.codepathflashcards;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class FlashcardDatabase {
    public final AppDatabase db;

    FlashcardDatabase(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "flashcard-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public List<Flashcard> getAllCards() {
        return db.flashcardDao().getAll();
    }

    public void insertCard(Flashcard flashcard) {
        db.flashcardDao().insertAll(flashcard);
    }

    public void rewriteAllCards(List<Flashcard> flashcards) {
        List<Flashcard> all = getAllCards();
        int prevSize = all.size();
        for(Flashcard card : flashcards)
            db.flashcardDao().insertAll(card);

        for(int i = 0; i < prevSize;i++)
            db.flashcardDao().delete(all.get(0));
    }

    public void deleteCard(Flashcard flashcard) {
        List<Flashcard> allCards = db.flashcardDao().getAll();
        for(Flashcard card : allCards)
            if (card.getQuestion().equals(flashcard.getQuestion()))
                db.flashcardDao().delete(card);
    }

    public void updateCard(Flashcard flashcard) {
        db.flashcardDao().update(flashcard);
    }
}