/*
 * This file is part of Flying PhotoBooth.
 * 
 * Flying PhotoBooth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flying PhotoBooth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flying PhotoBooth.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fatelon.partyphotobooth.setup.model;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatelon.partyphotobooth.helpers.PreferencesHelper;
import com.fatelon.partyphotobooth.themes.Theme;
import com.groundupworks.partyphotobooth.R;

/**
 * Adapter for the {@link PreferencesHelper.PhotoBoothTheme} selection ui.
 *
 * @author Benedict Lau
 */
public class PhotoBoothThemeAdapter extends BaseSpinnerAdapter<Theme> {

    /**
     * Constructor.
     *
     * @param context the {@link Context}.
     */
    public PhotoBoothThemeAdapter(Context context) {
        super(context, createItems(context.getApplicationContext()));
    }

    @Override
    protected void bindView(Theme item, View view) {
        // Hide unused views.
        view.findViewById(R.id.spinner_item_description).setVisibility(View.GONE);

        // Bind data.
        TextView displayName = ((TextView) view.findViewById(R.id.spinner_item_display_name));
        displayName.setTypeface(item.getFont());
        displayName.setText(item.getDisplayName());
        int iconRes = item.getIconResource();
        if (iconRes != Theme.RESOURCE_NONE) {
            ((ImageView) view.findViewById(R.id.spinner_item_icon)).setImageResource(iconRes);
        } else {
            ((ImageView) view.findViewById(R.id.spinner_item_icon)).setImageDrawable(null);
        }
    }

    //
    // Private methods.
    //

    /**
     * Creates the list of {@link Theme} for the selection ui.
     *
     * @param context the {@link Context}.
     * @return the {@link SparseArray} of {@link Theme}.
     */
    private static SparseArray<Theme> createItems(Context context) {
        SparseArray<Theme> themes = new SparseArray<>();
        PreferencesHelper.PhotoBoothTheme[] themeNames = PreferencesHelper.PhotoBoothTheme.values();
        for (PreferencesHelper.PhotoBoothTheme themeName : themeNames) {
            themes.put(themeName.ordinal(), Theme.from(context, themeName));
        }
        return themes;
    }

    //
    // Public methods.
    //

    /**
     * Gets the {@link PreferencesHelper.PhotoBoothTheme} based on the item position.
     *
     * @param position position of the item whose data we want within the adapter's data set.
     * @return the {@link PreferencesHelper.PhotoBoothTheme}.
     */
    public PreferencesHelper.PhotoBoothTheme getPhotoBoothTheme(int position) {
        return getItem(position).getTheme();
    }
}
