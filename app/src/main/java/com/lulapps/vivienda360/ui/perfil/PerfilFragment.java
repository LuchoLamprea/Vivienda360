package com.lulapps.vivienda360.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lulapps.vivienda360.LoginFragment;
import com.lulapps.vivienda360.MiPerfilFragment;
import com.lulapps.vivienda360.R;

public class PerfilFragment extends Fragment {

    FrameLayout contedor_ppal;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contedor_ppal= view.findViewById(R.id.contenedor_ppal_perfil);

        System.out.println("Rev1:\tCargó perfil");
        if(ArchivoExiste()){
            System.out.println("Rev1:\tarchivo existe");
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            MiPerfilFragment miPerfilFragment = new MiPerfilFragment();
            fragmentTransaction.replace(R.id.contenedor_ppal_perfil, miPerfilFragment);
            fragmentTransaction.commit();
        }
        else{
            System.out.println("Rev1:\tarchivo no existe");
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            LoginFragment loginFragment= new LoginFragment();
            fragmentTransaction.replace(R.id.contenedor_ppal_perfil, loginFragment);
            fragmentTransaction.commit();
        }
    }

    private boolean ArchivoExiste(){
        for(int i=0; i<getActivity().fileList().length; i++){
            if(getActivity().fileList()[i].equals("user_data.txt")){
                return true;
            }
        }
        return false;
    }
}