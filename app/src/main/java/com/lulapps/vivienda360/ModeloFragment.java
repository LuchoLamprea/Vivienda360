package com.lulapps.vivienda360;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ModeloFragment extends Fragment {

    String publicacion;
    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    //private String MODEL_URL="https://github.com/google/model-viewer/blob/master/packages/shared-assets/models/Horse.glb?raw=true";
    //private String MODEL_URL="https://udvivienda360.000webhostapp.com/modelos/modelo_997.glb";
    String URL_Modelo;

    public ModeloFragment() {

    }

    public ModeloFragment(String publicacion, String URL_Modelo) {
        this.publicacion=publicacion;
        this.URL_Modelo=URL_Modelo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modelo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //arFragment = (ArFragment) getParentFragmentManager().findFragmentById(R.id.fragment);
        //arFragment = (ArFragment) getParentFragment().getChildFragmentManager().findFragmentById(R.id.fragment);
        arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.fragment);
        setUpModel();
        setUpPlane();

    }

    private void setUpModel() {
        ModelRenderable.builder()
                .setSource(getContext(),
                        RenderableSource.builder().setSource(
                                        getContext(),
                                        Uri.parse(URL_Modelo),
                                        RenderableSource.SourceType.GLB)
                                .setScale(1f)
                                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                .build())
                .setRegistryId(URL_Modelo)
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                    //aca introducir el codigo para finalizar la barra de progreso y dar paso al setUpPlane
                })
                .exceptionally(throwable -> {
                    Toast.makeText(getContext(), "No se puede cargar el modelo", Toast.LENGTH_SHORT).show();
                    return null;
                });

        //Toast.makeText(getContext(),"Pasó el setUpModel", Toast.LENGTH_SHORT).show();
    }

    private void setUpPlane() {
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            createModel(anchorNode);
        }));
        //Toast.makeText(getContext(),"Finalizó el setUpModel", Toast.LENGTH_SHORT).show();
    }

    private void createModel(AnchorNode anchorNode) {
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(modelRenderable);
        node.select();
        Toast.makeText(getContext(),getResources().getString(R.string.modelo_cargado), Toast.LENGTH_SHORT).show();

    }
}