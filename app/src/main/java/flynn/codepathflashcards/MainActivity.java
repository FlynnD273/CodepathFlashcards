package flynn.codepathflashcards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
{
    Random random;

    Resources res;

    TextView flashcard_question;
    TextView flashcard_answer;

    Button[] answer_views;

    ImageView add;
    ImageView edit;
    ImageView button_next;

    List<Flashcard> flashcards;

    int user_answered = 0;
    int flashcard_number = 0;

    boolean answered = false;

    Flashcard current_flashcard;

    FlashcardDatabase flashcardDatabase;

    Animation leftOutAnim;
    Animation rightInAnim;

    //Reload all flashcards from resource strings
    public void resetFlashcards()
    {
        flashcards = flashcardDatabase.getAllCards();
    }

    //Sets state to starting state
    public void resetFlashcardApp()
    {
        flashcard_number = 0;

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
            user_answered = 0;
            for(int i = 0; i < answer_views.length;i++)
            {
                if(answer_views[i] == tv)
                {
                    user_answered = i;
                    break;
                }
            }

            //Takes care of the appearance of the wuestion textView
            flashcard_answer.setText(current_flashcard.getAnswers()[current_flashcard.getCorrectIndex()]);



            // get the center for the clipping circle
            int cx = flashcard_answer.getWidth() / 2;
            int cy = flashcard_answer.getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(flashcard_answer, cx, cy, 0f, finalRadius);

            // hide the question and show the answer to prepare for playing the animation!
            flashcard_answer.setVisibility(View.VISIBLE);

            anim.setDuration(500);
            anim.start();



            //If answered correctly, sets color to intense green
            if (user_answered == current_flashcard.getCorrectIndex())
            {
                answer_views[user_answered].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
            }
            //Otherwise highlight correct answer and chosen answer
            else
            {
                answer_views[user_answered].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                answer_views[current_flashcard.getCorrectIndex()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
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
        edit.setVisibility(VISIBLE);
        add.setVisibility(VISIBLE);
        flashcard_answer.setVisibility(View.INVISIBLE);

        flashcard_question.startAnimation(leftOutAnim);

        if(card != null)
        {
            //Sets color of the question textView to yellow and shows question
            flashcard_question.setText(card.getQuestion());

            answered = false;

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

            answered = false;

            //Set the answer ButtonViews to the appropriate text and color
            for (Button answer_view : answer_views) {
                answer_view.setBackgroundColor(res.getColor(R.color.colorWhite));
                answer_view.setText("");
            }
        }
    }

    public void updateLayoutState()
    {
        if(answered)
        {
            edit.setVisibility(VISIBLE);
            add.setVisibility(VISIBLE);
            displayFlashcard(current_flashcard, false);
            answered = true;
            flashcard_answer.setText(current_flashcard.getAnswers()[current_flashcard.getCorrectIndex()]);

            if (user_answered == current_flashcard.getCorrectIndex())
            {
                answer_views[user_answered].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
            }
            else
            {
                answer_views[user_answered].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                for(int i = 0; i < answer_views.length; i++)
                {
                    answer_views[current_flashcard.getCorrectIndex()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                }
            }
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
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    //Go to add flashcard activity
    public void onClickAddFlashcard(View v)
    {
        Intent intent = new Intent(MainActivity.this, EditFlashcardActivity.class);
        MainActivity.this.startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of item position
        outState.putInt("flashcard_number", flashcard_number);
        outState.putInt("answer_num", user_answered);
        outState.putBoolean("answered", answered);
        outState.putSerializable("flashcard", current_flashcard);
        outState.putSerializable("flashcards", (Serializable)flashcards);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        user_answered = savedInstanceState.getInt("answer_num");
        flashcard_number = savedInstanceState.getInt("flashcard_number");
        answered = savedInstanceState.getBoolean("answered");
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

        flashcard_question = findViewById(R.id.flashcard_question);
        flashcard_answer = findViewById(R.id.flashcard_answer);;

        answer_views = new Button[]{findViewById(R.id.answer0), findViewById(R.id.answer1), findViewById(R.id.answer2)};
        for (Button answer_view : answer_views) {
            answer_view.setBackgroundColor(res.getColor(R.color.colorWhite));
        }

        button_next = findViewById(R.id.next_button);

        add = findViewById(R.id.add);
        edit = findViewById(R.id.edit);

        random = new Random();

        leftOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
        rightInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);

        leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // this method is called when the animation first starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                flashcard_question.startAnimation(rightInAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // we don't need to worry about this method
            }
        });

        //Show flashcard
        resetFlashcards();
        resetFlashcardApp();
    }
}
