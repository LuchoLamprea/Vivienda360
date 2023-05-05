package com.lulapps.vivienda360.ui.favoritos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lulapps.vivienda360.LoginFragment;
import com.lulapps.vivienda360.MiPerfilFragment;
import com.lulapps.vivienda360.MisFavoritosFragment;
import com.lulapps.vivienda360.R;

public class FavoritosFragment extends Fragment {

    FrameLayout contedor_ppal;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contedor_ppal= view.findViewById(R.id.contenedor_ppal_favoritos);
        if(ArchivoExiste()){
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            MisFavoritosFragment misFavoritosFragment= new MisFavoritosFragment();
            fragmentTransaction.replace(R.id.contenedor_ppal_favoritos, misFavoritosFragment);
            fragmentTransaction.commit();
        }
        else{
            Toast.makeText(this.getContext(), getResources().getString(R.string.inicia_o_registrate_favoritos2), Toast.LENGTH_SHORT).show();
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