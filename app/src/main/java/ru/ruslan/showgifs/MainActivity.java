package ru.ruslan.showgifs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<PostModel> posts;
    private Disposable disposable;
    private ImageView imageView;
    private ImageButton fab_next, fab_prev;
    private TextView textError, description;
    private ProgressBar progressBar;
    private int currentPost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        posts = new ArrayList<>();
        imageView = findViewById(R.id.imageView);
        fab_next = findViewById(R.id.fab_next);
        fab_prev = findViewById(R.id.fab_prev);
        textError = findViewById(R.id.textError);
        progressBar = findViewById(R.id.progressBar);
        description = findViewById(R.id.tv_description);
        fab_prev.setClickable(false);

        fab_next.setOnClickListener(view -> {
            setUndoEnabled();
            clearDisposable();
            loadPage();
        });

        fab_prev.setOnClickListener(view -> {

            if (currentPost > 0) {
                currentPost--;
                Glide.with(MainActivity.this)
                        .load(posts.get(currentPost).gifUrl)
                        .into(imageView);
            }
            description.setText(posts.get(currentPost).description);
            if (currentPost <= 0) setUndoDisabled();
        });

        loadPage();

        if (currentPost <= 0) setUndoDisabled();

    }

    private void loadPage() {
        App.getApi().getData("random")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<PostModel>() {


                    @Override
                    public void onSubscribe(@NotNull Disposable disposable) {
                        //сохраняем подписку
                        setState(State.Loading);
                        MainActivity.this.disposable = disposable;

                    }

                    @Override
                    public void onSuccess(@NotNull PostModel postModel) {
                        setState(State.Content);
                        posts.add(postModel);
                        Log.d("", "");
                        Glide.with(MainActivity.this)
                                .load(postModel.gifUrl)
                                .into(imageView);
                        currentPost++;
                        description.setText(postModel.description);

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        setState(State.Error);
                        Toast.makeText(MainActivity.this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUndoEnabled() {
        fab_prev.setClickable(true);
        fab_prev.setActivated(true);
    }

    private void setUndoDisabled() {
        fab_prev.setClickable(false);
        fab_prev.setActivated(false);
    }


    private void setState(State state) {
        imageView.setVisibility(View.INVISIBLE);
        fab_next.setVisibility(View.INVISIBLE);
        fab_prev.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        textError.setVisibility(View.INVISIBLE);
        switch (state) {
            case Loading:
                progressBar.setVisibility(View.VISIBLE);

                break;

            case Error:
                textError.setVisibility(View.VISIBLE);
                break;

            case Content:
                imageView.setVisibility(View.VISIBLE);
                fab_next.setVisibility(View.VISIBLE);
                fab_prev.setVisibility(View.VISIBLE);
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearDisposable();
    }

    private void clearDisposable() {
        disposable.dispose();
        disposable = null;
    }

    enum State {
        Loading,
        Error,
        Content
    }

}

