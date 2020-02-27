package flynn.codepathflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;

public class AddFlashcardActivity extends AppCompatActivity
{
    Flashcard flashcard;

    public void addFlashcardCancel(View v)
    {
        Intent data = new Intent(getApplicationContext(), AddFlashcardActivity.class);
        data.putExtra("flashcard", flashcard);
        setResult(RESULT_OK, data);
        finish();
    }

    public void addFlashcardConfirm(View v)
    {
        EditText editText_question = findViewById(R.id.editTextQuestion);
        EditText editText_correct = findViewById(R.id.editTextCorrectAnswer);
        EditText editText_wrong1 = findViewById(R.id.editTextWrongAnswer1);
        EditText editText_wrong2 = findViewById(R.id.editTextWrongAnswer2);

        boolean added = false;

        if(!editText_question.getText().toString().equals("") && !editText_correct.getText().toString().equals("") && !editText_wrong1.getText().toString().equals("") && !editText_wrong2.getText().toString().equals(""))
        {
            added = true;
            flashcard = new Flashcard(editText_question.getText().toString(), new String[] {editText_correct.getText().toString(), editText_wrong1.getText().toString(), editText_wrong2.getText().toString()});
        }

        if(added)
        {
            addFlashcardCancel(editText_question);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard);
    }
}
