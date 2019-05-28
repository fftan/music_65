package com.sun_asterisk.music_65.screen.library;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.sun_asterisk.music_65.R;
import com.sun_asterisk.music_65.data.model.Song;
import com.sun_asterisk.music_65.data.source.SongRepository;
import com.sun_asterisk.music_65.data.source.local.SongLocalDataSource;
import com.sun_asterisk.music_65.data.source.remote.SongRemoteDataSource;
import com.sun_asterisk.music_65.screen.library.adapter.LibraryAdapter;
import com.sun_asterisk.music_65.utils.OnItemRecyclerViewClickListener;
import java.util.List;

public class LibraryFragment extends Fragment
        implements LibraryContract.View, OnItemRecyclerViewClickListener<Song> {
    private LibraryContract.Presenter mPresenter;
    private static final int PERMISSION_REQUEST_CODE = 1616;
    private LibraryAdapter mLibraryAdapter;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onGetLocalSongsSuccess(List<Song> songs) {
        if (songs != null) {
            mLibraryAdapter.updateData(songs);
        }
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClickListener(Song item) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPresenter.getLocalSongs();
        }
    }

    public void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerViewLocalSongs);
        mRecyclerView.setHasFixedSize(true);
        mLibraryAdapter = new LibraryAdapter();
        mRecyclerView.setAdapter(mLibraryAdapter);
        mLibraryAdapter.setOnItemRecyclerViewClickListener(this);
    }

    public void initData() {
        SongRemoteDataSource remoteDataSource = SongRemoteDataSource.getInstance();
        SongLocalDataSource localDataSource =
                SongLocalDataSource.getInstance(getActivity().getApplicationContext());
        SongRepository repository = SongRepository.getInstance(localDataSource, remoteDataSource);
        mPresenter = new LibraryPresenter(repository);
        mPresenter.setView(this);
        if (isCheckPermission()) {
            mPresenter.getLocalSongs();
        }
    }

    public boolean isCheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                        PERMISSION_REQUEST_CODE);
                return false;
            }
            return true;
        }
        return true;
    }
}
