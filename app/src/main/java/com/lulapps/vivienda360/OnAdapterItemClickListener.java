package com.lulapps.vivienda360;

import android.view.View;

public interface OnAdapterItemClickListener {
    void onAdapterItemClickListener(View v, int position);
    void onEditarButtonClickListener(View v, int idPublicacion);
    void onEliminarButtonClickListener(View v, int idPublicacion);
    void onContactarButtonClickListener(View v, int idPublicacion);
}