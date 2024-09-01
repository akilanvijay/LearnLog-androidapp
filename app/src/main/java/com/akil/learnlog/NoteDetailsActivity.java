package com.akil.learnlog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText titleEditText,contentEditText;
    ImageButton saveNotebtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode= false;
    TextView deleteNoteTextViewBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        titleEditText=findViewById(R.id.notes_title_text);
        contentEditText=findViewById(R.id.notes_content_text);
        saveNotebtn=findViewById(R.id.save_note_btn);
        pageTitleTextView=findViewById(R.id.page_title);
        deleteNoteTextViewBtn=findViewById(R.id.delete_note_text_view_btn);
//get data
        title= getIntent().getStringExtra("title");
        content= getIntent().getStringExtra("content");
        docId=getIntent().getStringExtra("docId");
        if(docId!=null && !docId.isEmpty()){
            isEditMode=true;
        }
        titleEditText.setText(title);
        contentEditText.setText(content);
        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }


        saveNotebtn.setOnClickListener((v)->saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());
    }
    void saveNote(){
        String noteTitle=titleEditText.getText().toString();
        String noteContent=contentEditText.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }
        Note note=new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        saveNoteToFirebase(note);
    }
    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //updating the note
            documentReference=Utility.getCollectionReferenceForNotes().document(docId);

        }
        else{
            //create new note
            documentReference=Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   //notes saved.....
                   Utility.showToast(NoteDetailsActivity.this,"Note added successfully in cloud !");
                   finish();

               }
               else{
                   //note not added
                Utility.showToast(NoteDetailsActivity.this,"Not saved 😢 !");
                }
            }
        });
    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

        documentReference=Utility.getCollectionReferenceForNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //notes saved.....
                    Utility.showToast(NoteDetailsActivity.this,"Note added successfully in cloud !");
                    finish();
                }
                else{
                    //note not added
                    Utility.showToast(NoteDetailsActivity.this,"Not saved 😢 !");
                }
            }
        });
    }
}