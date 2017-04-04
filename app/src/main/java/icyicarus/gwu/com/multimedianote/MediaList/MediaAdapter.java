package icyicarus.gwu.com.multimedianote.MediaList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import icyicarus.gwu.com.multimedianote.R;

public class MediaAdapter extends BaseAdapter {

    private Context context;
    private List<MediaListCellData> list = new ArrayList<>();

    public MediaAdapter(Context context) {
        this.context = context;
    }

    public void add(MediaListCellData data) {
        list.add(data);
    }

    public void remove(String path) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).path.equals(path))
                list.remove(i);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MediaListCellData getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_media_list, null);
        }

        MediaListCellData data = getItem(position);
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.imageViewMediaCellIcon);
        TextView tvPath = (TextView) convertView.findViewById(R.id.textViewMediaCellPath);

        if (data.iconID == R.drawable.img_photo) {
            Bitmap bitmap = BitmapFactory.decodeFile(data.path, getBitmapOption(16));
            ivIcon.setImageDrawable(new BitmapDrawable(bitmap));
        } else {
            ivIcon.setImageResource(data.iconID);
        }
        tvPath.setText(data.path);

        return convertView;
    }

    public static BitmapFactory.Options getBitmapOption(int size) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        return options;
    }
}