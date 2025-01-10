package music.saidweb.playlist.offline;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import music.saidweb.playlist.offline.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListSongsFragmentInteractionListener}
 * interface.
 */
public class SongFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListSongsFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongFragment() {
    }

    @SuppressWarnings("unused")
    public static SongFragment newInstance(int columnCount) {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        // Set the adapter
        MysongRecyclerViewAdapter adapter = new MysongRecyclerViewAdapter(DummyContent.ITEMS, mListener);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener((position, button) -> {
                System.out.println("Option Button Clicked At Position: " + position);
                //registerForContextMenu(button);
                DummyContent.DummyItem item = DummyContent.ITEMS.get(position);
                print("------- the Name of The Song: "+item);
                //getContext().getAssets().open("");
                popUp(position, button);
            });


        }
        return view;
    }

    private void popUp(int position, View button) {
        PopupMenu popup = new PopupMenu(getContext(), button);
        popup.getMenuInflater().inflate(R.menu.rington_menu, popup.getMenu());
        popup.setForceShowIcon(true);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.callRingtone:
                        // your first action code
                        print("Call Ring Tone Called");
                        mListener.optionButtonClicked(position, RingtoneManager.TYPE_RINGTONE);

                        return true;

                    case R.id.notificationRingtone:
                        // your second action code
                        print("Notification Ring Tone Called");
                        mListener.optionButtonClicked(position, RingtoneManager.TYPE_ALARM);
                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListSongsFragmentInteractionListener) {
            mListener = (OnListSongsFragmentInteractionListener) context;


        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListSongsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListSongsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListSongsFragmentInteraction(int songNumber);

        void optionButtonClicked(int position, int type);
    }

    private void print(String input) {
        System.out.println(input);
    }


}
