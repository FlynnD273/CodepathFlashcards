package flynn.codepathflashcards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
{
    Random random;

    Drawable rounded_drawable;
    Resources res;

    TextView flashcard_question;

    ImageView button_next;
    Button[] answer_views;

    ImageView add;
    ImageView edit;

    List<Flashcard> flashcards;

    ArrayList<UserAnswer> user_answers;

    int answer_num = -1;
    int flashcard_number = 0;

    boolean end_screen = false;
    boolean answered = false;
    boolean show_answers = true;

    Flashcard current_flashcard;

    FlashcardDatabase flashcardDatabase;

    //Reload all flashcards from resource strings
    public void resetFlashcards()
    {
        /*String[] questions = res.getStringArray(R.array.questions);
        String[] answers = res.getStringArray(R.array.answers);
        flashcards = new ArrayList<Flashcard>();


        for(int i = 0; i < answers.length; i++)
        {
            flashcards.add(new Flashcard(questions[i], answers[i].split(";")));
        }*/
        flashcards = flashcardDatabase.getAllCards();
    }

    //Sets state to starting state
    public void resetFlashcardApp()
    {
        user_answers = new ArrayList<>();
        flashcard_number = 0;
        end_screen = false;

        loadFlashcard(flashcard_number);
    }

    public void loadFlashcard(int index)
    {
        if(index > -1 && index < flashcards.size())
        {
            current_flashcard = flashcards.get(index);
        }
        else
        {
            current_flashcard = null;

        }
        displayFlashcard(current_flashcard, true);
    }

    //Delete a flashcard
    public void deleteCard(Flashcard card)
    {
        flashcardDatabase.deleteCard(card);
        flashcards = flashcardDatabase.getAllCards();
        loadFlashcard(random.nextInt(Math.max(flashcards.size(),1)));
    }

    //Handles deleting a card
    public void onClickDeleteFlashcard(View v)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteCard(flashcardDatabase.getAllCards().get(flashcard_number));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
        if(current_flashcard != null)
            builder.show();
    }

    //Handles clicking on an answer
    public void onClickAnswer(View v)
    {
        if(!answered && current_flashcard != null)
        {
            Button tv = (Button) v;

            //Find out which answer was clicked
            answer_num = 0;
            for(int i = 0; i < answer_views.length;i++)
            {
                if(answer_views[i] == tv)
                {
                    answer_num = i;
                    break;
                }
            }

            //No out of bounds exceptions for the user_answers List anymore!
            while(user_answers.size() <= flashcard_number)
            {
                user_answers.add(new UserAnswer("", "", false));
            }

            //Takes care of the appearance of the wuestion textView
            flashcard_question.setText(current_flashcard.getAnswers()[current_flashcard.getCorrectIndex()]);
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);
            user_answers.set(flashcard_number, new UserAnswer(current_flashcard.getCorrectAnswer(), current_flashcard.getAnswers()[answer_num], false));

            //If answered correctly, sets color to intense green
            if (answer_num == current_flashcard.getCorrectIndex())
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
                user_answers.get(flashcard_number).setIsCorrect(true);
            }
            //Otherwise highlight correct answer and chosen answer
            else
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                answer_views[current_flashcard.getCorrectIndex()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                user_answers.get(flashcard_number).setIsCorrect(false);
            }

            //Show the "next question" button
            answered = true;
            button_next.setVisibility(VISIBLE);
        }
    }

    public void onClickNextQuestion(View v)
    {
        if(flashcards.size()>0)
        {
            flashcard_number = ++flashcard_number>flashcards.size()-1?0:flashcard_number;

            loadFlashcard(flashcard_number);
        }
    }

    public void onClickNextRandomQuestion(View v)
    {
        flashcard_number = random.nextInt(Math.max(flashcards.size(),1));

        loadFlashcard(flashcard_number);
    }


    public void displayFlashcard(Flashcard card, boolean randomize)
    {
        //Always show answers by default if loading a new flashcard
        show_answers = true;
        updateVisibility();

        edit.setVisibility(VISIBLE);
        add.setVisibility(VISIBLE);

        if(card != null)
        {
            //Sets color of the question textView to yellow and shows question
            flashcard_question.setText(card.getQuestion());
            rounded_drawable.setColorFilter(res.getColor(R.color.colorQuestion), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            //Takes care of visibility and content of the "next question" button
            //button_next.setText(getString(R.string.NextQuestion));
            answered = false;
            //button_next.setVisibility(INVISIBLE);

            //Randomize the answers so the first answer isn't always the correct one
            if (randomize)
                card.randomize();

            //Set the answer ButtonViews to the appropriate text and color
            for (int i = 0; i < answer_views.length; i++) {
                answer_views[i].setBackgroundColor(res.getColor(R.color.colorWhite));
                answer_views[i].setText(card.getAnswers()[i]);
            }
        }
        else
        {
            flashcard_question.setText("");
            rounded_drawable.setColorFilter(res.getColor(R.color.colorQuestion), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            //Takes care of visibility and content of the "next question" button
            //button_next.setText(getString(R.string.NextQuestion));
            answered = false;
            //button_next.setVisibility(INVISIBLE);

            //Set the answer ButtonViews to the appropriate text and color
            for (Button answer_view : answer_views) {
                answer_view.setBackgroundColor(res.getColor(R.color.colorWhite));
                answer_view.setText("");
            }
        }
    }

    /*public void toggleAnswerVisibility(View v)
    {
        if(!end_screen)
        {
            show_answers = !show_answers;
            //updateVisibility();
        }
    }*/

    //Takes care of the visibility of the answers and the state of the eye button
    public void updateVisibility()
    {
        if(show_answers && !end_screen)
        {
            //((ImageView)findViewById(R.id.visibility_icon)).setVisibility(VISIBLE);
            //((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_hide);
            for (Button answer_view : answer_views)
            {
                answer_view.setVisibility(VISIBLE);
            }
        }
        else
        {
            for (Button answer_view : answer_views)
                answer_view.setVisibility(INVISIBLE);
        }
    }

    //Shows the summary screen
    /*public void displayEndScreen()
    {
        end_screen = true;
        int num_correct_answers = 0;
        for(UserAnswer userAnswer : user_answers)
        {
            if(userAnswer.isCorrect())
                num_correct_answers++;
        }

        //Score
        flashcard_question.setText(getString(R.string.endText, String.valueOf(num_correct_answers), String.valueOf(flashcards.size())));
        rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
        flashcard_question.setBackground(rounded_drawable);

        //Hide answer buttons
        show_answers = false;
        updateVisibility();

        button_next.setVisibility(VISIBLE);
        //button_next.setText(getString(R.string.Restart));

        edit.setVisibility(INVISIBLE);
        add.setVisibility(INVISIBLE);
    }*/

    public void updateLayoutState()
    {
        /*if(end_screen)
        {
            displayEndScreen();
        }
        else */if(answered)
        {
            edit.setVisibility(VISIBLE);
            add.setVisibility(VISIBLE);
            displayFlashcard(current_flashcard, false);
            answered = true;
            flashcard_question.setText(current_flashcard.getAnswers()[current_flashcard.getCorrectIndex()]);
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            if (answer_num == current_flashcard.getCorrectIndex())
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
            }
            else
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                for(int i = 0; i < answer_views.length; i++)
                {
                    answer_views[current_flashcard.getCorrectIndex()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                }
            }

            button_next.setVisibility(VISIBLE);
        }
        else
        {
            displayFlashcard(current_flashcard, false);
        }
    }

    public void onClickEditFlashcard(View v)
    {
        if(current_flashcard != null)
        {
            Intent intent = new Intent(MainActivity.this, EditFlashcardActivity.class);
            intent.putExtra("current_flashcard", (Serializable) current_flashcard);
            MainActivity.this.startActivityForResult(intent, 1);
        }
    }

    //Go to add flashcard activity
    public void onClickAddFlashcard(View v)
    {
        Intent intent = new Intent(MainActivity.this, EditFlashcardActivity.class);
        MainActivity.this.startActivityForResult(intent, 0);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of item position
        outState.putInt("flashcard_number", flashcard_number);
        outState.putInt("answer_num", answer_num);
        outState.putBoolean("answered", answered);
        outState.putBoolean("end_screen", end_screen);
        //outState.putBoolean("show_answers", show_answers);
        outState.putSerializable("flashcard", current_flashcard);
        outState.putSerializable("user_answers", user_answers);
        outState.putSerializable("flashcards", (Serializable)flashcards);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        answer_num = savedInstanceState.getInt("answer_num");
        flashcard_number = savedInstanceState.getInt("flashcard_number");
        user_answers = (ArrayList<UserAnswer>) savedInstanceState.getSerializable("user_answers");
        answered = savedInstanceState.getBoolean("answered");
        end_screen = savedInstanceState.getBoolean("end_screen");
        current_flashcard = (Flashcard)savedInstanceState.getSerializable("flashcard");
        flashcards = (ArrayList<Flashcard>)savedInstanceState.getSerializable("flashcards");

        updateLayoutState();
        //show_answers = savedInstanceState.getBoolean("show_answers");
        //updateVisibility();
    }

    //Retrieve data from flashcard adding activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("add", "Added or edited flashcard");
        if (resultCode == RESULT_OK)
        {
            Flashcard card = (Flashcard) data.getExtras().getSerializable("flashcard");
            switch(requestCode)
            {
                case 0:
                    Toast.makeText(getApplicationContext(), "Successfully Added Flashcard", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    flashcardDatabase.deleteCard(current_flashcard);
                    Toast.makeText(getApplicationContext(), "Successfully Edited Flashcard", Toast.LENGTH_SHORT).show();
                    break;
            }
            flashcardDatabase.insertCard(card);
            flashcards = flashcardDatabase.getAllCards();
            loadFlashcard(flashcard_number);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();

        //INITIALIZE EVERYTHING
        flashcardDatabase = new FlashcardDatabase(getApplicationContext());

        flashcard_question = findViewById(R.id.flashcard_content);

        answer_views = new Button[]{findViewById(R.id.answer0), findViewById(R.id.answer1), findViewById(R.id.answer2)};
        for (Button answer_view : answer_views) {
            answer_view.setBackgroundColor(res.getColor(R.color.colorWhite));
        }

        rounded_drawable = res.getDrawable(R.drawable.round_corner);
        button_next = findViewById(R.id.next_button);

        add = findViewById(R.id.add);
        edit = findViewById(R.id.edit);

        random = new Random();

        //Show flashcard
        resetFlashcards();
        resetFlashcardApp();
    }
}
