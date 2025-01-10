package music.saidweb.playlist.offline;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.interstitial.InterstitialAd;

import music.saidweb.playlist.offline.SongFragment.OnListSongsFragmentInteractionListener;
import music.saidweb.playlist.offline.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link SongFragment.OnListSongsFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MysongRecyclerViewAdapter extends RecyclerView.Adapter<MysongRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final SongFragment.OnListSongsFragmentInteractionListener mListener;
    private InterstitialAd mInterstitialAd;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onOptionClick(int position, View button);
    }


    public MysongRecyclerViewAdapter(List<DummyItem> items, OnListSongsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_song, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListSongsFragmentInteraction(position);
                }
            }
        });

//        holder.optionImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    mListener.optionButtonClicked(position);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView optionImg;
        public DummyItem mItem;

        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            optionImg = (ImageView) view.findViewById(R.id.optionImg);

            optionImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onOptionClick(position, optionImg);
                        }
                    }
                }
            });

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
