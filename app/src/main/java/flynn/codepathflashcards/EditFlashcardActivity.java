package flynn.codepathflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditFlashcardActivity extends AppCompatActivity
{
    Flashcard flashcard;
    EditText editText_question;
    EditText editText_correct;
    EditText editText_wrong1;
    EditText editText_wrong2;

    public void flashcardCancel(View v)
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void sendFlashcard()
    {
        Intent data = new Intent();
        data.putExtra("flashcard", flashcard);
        setResult(RESULT_OK, data);
        finish();
    }

    public void flashcardConfirm(View v)
    {
        if(!editText_question.getText().toString().equals("") && !editText_correct.getText().toString().equals("") && !editText_wrong1.getText().toString().equals("") && !editText_wrong2.getText().toString().equals(""))
        {
            flashcard = new Flashcard(editText_question.getText().toString(), new String[] {editText_correct.getText().toString(), editText_wrong1.getText().toString(), editText_wrong2.getText().toString()});
            sendFlashcard();
        }
        else
            Toast.makeText(getApplicationContext(), "All Fields Must Contain Text", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard);

        editText_question = findViewById(R.id.editTextQuestion);
        editText_correct = findViewById(R.id.editTextCorrectAnswer);
        editText_wrong1 = findViewById(R.id.editTextWrongAnswer1);
        editText_wrong2 = findViewById(R.id.editTextWrongAnswer2);

        editText_wrong2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    flashcardConfirm(v);
                    handled = true;
                }
                return handled;
            }
        });

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("current_flashcard"))
        {
            flashcard = (Flashcard) getIntent().getExtras().getSerializable("current_flashcard");

            editText_question.setText(flashcard.getQuestion());

            String[] answers =  flashcard.getAnswers();
            editText_correct.setText(flashcard.getCorrectAnswer());

            switch (flashcard.getCorrectIndex())
            {
                case 0:
                    editText_wrong1.setText(answers[1]);
                    editText_wrong2.setText(answers[2]);
                    break;
                case 1:
                    editText_wrong1.setText(answers[0]);
                    editText_wrong2.setText(answers[2]);
                    break;
                case 2:
                    editText_wrong1.setText(answers[0]);
                    editText_wrong2.setText(answers[1]);
                    break;
            }
        }
    }
}
