package com.locservice.ui.emojicon;

import com.locservice.R;
import com.locservice.ui.emojicon.emoji.Emojicon;

import android.content.Context;
import android.view.*;
import android.widget.*;


class EmojiAdapter extends ArrayAdapter<Emojicon> {
    public EmojiAdapter(Context context, Emojicon[] data) {
        super(context, R.layout.emojicon_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (TextView) v.findViewById(R.id.emojicon_icon);
            v.setTag(holder);
        }
        Emojicon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(emoji.getEmoji());
        return v;
    }

    class ViewHolder {
        TextView icon;
    }
}