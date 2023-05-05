package com.lulapps.vivienda360.ui.buscar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lulapps.vivienda360.R;
import com.lulapps.vivienda360.SeccionBuscarFragment;

public class BuscarFragment extends Fragment {

    FrameLayout contedor_ppal;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contedor_ppal= view.findViewById(R.id.contenedor_ppal_buscar);

        System.out.println("Rev1:\tCargó buscar previo");
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        SeccionBuscarFragment seccionBuscarFragment= new SeccionBuscarFragment();
        fragmentTransaction.replace(R.id.contenedor_ppal_buscar, seccionBuscarFragment);
        fragmentTransaction.commit();


    }
}